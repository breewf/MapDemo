package com.ghy.mapdemo;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.ghy.mapdemo.location.LocationClientImpl;
import com.ghy.mapdemo.location.OnLocationListener;
import com.ghy.mapdemo.location.entity.Location;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView = null;
    private AMap aMap = null;

    private LocationClientImpl locationClient = null;
    private Location mLocation;

    private TextView mTextViewRoad;
    private LinearLayout business_layout;
    private LinearLayout business_info_layout;
    private TextView tv_business_phone;
    private TextView tv_business_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        mTextViewRoad = (TextView) findViewById(R.id.btn_road_info);
        business_layout = (LinearLayout) findViewById(R.id.business_layout);
        business_info_layout = (LinearLayout) findViewById(R.id.business_info_layout);
        tv_business_phone = (TextView) findViewById(R.id.tv_business_phone);
        tv_business_navigation = (TextView) findViewById(R.id.tv_business_navigation);

        mTextViewRoad.setSelected(false);
        mTextViewRoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mTextViewRoad.isSelected()) {
                    openRoadInfo(true);
                } else {
                    openRoadInfo(false);
                }
            }
        });

        //点击商家信息布局地图视角镜头移动到商家
        business_info_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
                //地图的缩放级别一共分为 17 级，从 3 到 19。数字越大，展示的图面信息越精细。
                CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(39.996901, 116.397972), 14, 0, 0));
                //AMap类中提供，带有移动过程的动画
                if (aMap != null) aMap.animateCamera(mCameraUpdate, 1000, null);
            }
        });

        tv_business_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "拨打电话", Toast.LENGTH_SHORT).show();
            }
        });

        tv_business_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "开始导航", Toast.LENGTH_SHORT).show();

                if (mLocation == null) {
                    Toast.makeText(MainActivity.this, "定位失败，无法进行导航，请稍后重试", Toast.LENGTH_SHORT).show();
                } else {
//                    LocationMapUtils.openBaiduNavi(StoreMapActivity.this, mLocation.getLatitude(), mLocation.getLongitude(), "我的位置",
//                            39.996901, 116.397972, "国家体育场", mLocation.getCity());
                    LocationMapUtils.openGaoDeNavi(MainActivity.this, mLocation.getLatitude(), mLocation.getLongitude(), "我的位置",
                            39.996901, 116.397972, "国家体育场");
                }

            }
        });

        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        loadLocation();
        loadMapSetting(aMap);
        loadMyPosition(aMap);

        //加载Marker
        initMarker(aMap);
    }

    private void initMarker(AMap aMap) {
        if (aMap == null) return;
        LatLng latLng = new LatLng(39.906901, 116.397972);
//        Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("北京").snippet("DefaultMarker"));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);//在地图上标记位置的经纬度值。必填参数
        markerOptions.title("北京");//点标记的标题
        markerOptions.snippet("天安门");//点标记的内容
        markerOptions.draggable(false);//点标记是否可拖拽
        markerOptions.visible(true);//点标记是否可见
        aMap.addMarker(markerOptions);

        LatLng latLng2 = new LatLng(39.996901, 116.397972);
        aMap.addMarker(new MarkerOptions().position(latLng2).icon(
                BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.icon_location))
        ).title("北京2").snippet("国家体育场"));

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MainActivity.this, "Marker被点击--" + marker.getSnippet(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    /**
     * 路况信息
     *
     * @param isOpenRoadInfo 是否开启
     */
    private void openRoadInfo(boolean isOpenRoadInfo) {
        Drawable drawable;
        if (isOpenRoadInfo) {
            drawable = ContextCompat.getDrawable(this, R.mipmap.icon_road_2);
            mTextViewRoad.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.blue_color));
        } else {
            drawable = ContextCompat.getDrawable(this, R.mipmap.icon_road_1);
            mTextViewRoad.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.black_66));
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mTextViewRoad.setCompoundDrawables(null, null, drawable, null);
        mTextViewRoad.setCompoundDrawablePadding(dip2px(MainActivity.this, -10));
        mTextViewRoad.setSelected(isOpenRoadInfo);

        if (isOpenRoadInfo) {
            if (aMap != null) aMap.setTrafficEnabled(true);//显示实时路况图层，aMap是地图控制器对象。
        } else {
            if (aMap != null) aMap.setTrafficEnabled(false);
        }
    }

    /**
     * 加载地图设置
     */
    private void loadMapSetting(AMap aMap) {
        if (aMap == null) return;
        aMap.getUiSettings().setZoomControlsEnabled(true);//设置缩放按钮是否显示，默认为true
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);//可触发定位并显示当前位置
        aMap.getUiSettings().setScaleControlsEnabled(true);//控制比例尺控件是否显示，默认不显示

        aMap.getUiSettings().setAllGesturesEnabled(true);//所有手势
        aMap.getUiSettings().setZoomGesturesEnabled(true);//缩放手势
        aMap.getUiSettings().setScrollGesturesEnabled(true);//滑动手势
        aMap.getUiSettings().setRotateGesturesEnabled(false);//旋转手势
        aMap.getUiSettings().setTiltGesturesEnabled(true);//倾斜手势
    }

    /**
     * 定位
     */
    private void loadLocation() {
        locationClient = new LocationClientImpl(getApplicationContext(), new OnLocationListener() {

            @Override
            public void onSuccess(Location location) {
                mLocation = location;
                Toast.makeText(MainActivity.this, "定位成功-->>" + location.getAddress() +
                        "--" + location.getLatitude() + "--" + location.getLongitude(), Toast.LENGTH_SHORT).show();

                CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(
                        new CameraPosition(new LatLng(location.getLatitude(), location.getLongitude()), 14, 0, 0));
                //AMap类中提供，带有移动过程的动画
                if (aMap != null) aMap.animateCamera(mCameraUpdate, 1000, null);
            }

            @Override
            public void onFailure(String errInfo) {//定位失败
                Toast.makeText(MainActivity.this, "定位失败-->>" + errInfo, Toast.LENGTH_SHORT).show();
            }
        });
        locationClient.start();
    }

    /**
     * 加载我的位置蓝点
     *
     * @param aMap
     */
    private void loadMyPosition(AMap aMap) {
        if (aMap == null) return;
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) ;//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);//连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
        //以下三种模式从5.1.0版本开始提供
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。
        //方法自5.1.0版本后支持
        //设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。
        myLocationStyle.showMyLocation(true);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        if (locationClient != null) locationClient.destroy();
    }

}
