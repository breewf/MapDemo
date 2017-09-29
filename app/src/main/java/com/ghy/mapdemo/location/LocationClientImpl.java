package com.ghy.mapdemo.location;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ghy.mapdemo.location.entity.Location;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 高德定位
 * Created by wang on 2015/12/23.
 */
public class LocationClientImpl {

    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //定位回调
    private OnLocationListener onLocationListener;

    /**
     * @param context use getApplicationContext()
     */
    public LocationClientImpl(Context context, OnLocationListener onLocationListener) {
        this.onLocationListener = onLocationListener;
        mLocationClient = new AMapLocationClient(context);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
    }


    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {

        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {//定位成功回调信息，设置相关消息
//                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                    amapLocation.getLatitude();//获取纬度
//                    amapLocation.getLongitude();//获取经度
//                    amapLocation.getAccuracy();//获取精度信息
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                    Date date = new Date(amapLocation.getTime());
                    String time = df.format(date);//定位时间
//                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果
//                    amapLocation.getCountry();//国家信息
//                    amapLocation.getProvince();//省信息
//                    amapLocation.getCity();//城市信息
//                    amapLocation.getDistrict();//城区信息
//                    amapLocation.getCityCode();//城市编码

                    Location location = new Location();
                    location.setLatitude(amapLocation.getLatitude());
                    location.setLongitude(amapLocation.getLongitude());
                    location.setTime(time);
                    location.setAddress(amapLocation.getAddress());
                    location.setCity(amapLocation.getCity());
                    location.setDistrict(amapLocation.getDistrict());
                    location.setProvince(amapLocation.getProvince());
                    location.setCityCode(amapLocation.getCityCode());
                    if (onLocationListener != null)
                        onLocationListener.onSuccess(location);
                } else {//显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                    if (onLocationListener != null)
                        onLocationListener.onFailure(amapLocation.getErrorInfo());
                }
            }

        }
    };


    //初始化定位
    public void start() {
        //初始化定位参数
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        locationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        locationOption.setOnceLocation(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        locationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        locationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(locationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    /**
     * 停止定位
     */
    public void stop() {
        mLocationClient.stopLocation();
    }

    /**
     * 销毁定位
     */
    public void destroy() {
        mLocationClient.onDestroy();
    }

}
