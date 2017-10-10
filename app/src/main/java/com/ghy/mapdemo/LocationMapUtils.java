package com.ghy.mapdemo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by GHY on 2017/10/9.
 * Desc: App调起高德地图、百度地图相关工具类
 */

public class LocationMapUtils {

    private static final String APP_NAME = "MapDemo";
    private static final String MAP_PACKAGE_NAME_GAODE = "com.autonavi.minimap";
    private static final String MAP_PACKAGE_NAME_BAIDU = "com.baidu.BaiduMap";

    /**
     * 地图应用是否安装
     *
     * @return
     */
    public static boolean isGdMapInstalled(Context context) {
        return isInstallPackage(context, MAP_PACKAGE_NAME_GAODE);
    }

    public static boolean isBaiduMapInstalled(Context context) {
        return isInstallPackage(context, MAP_PACKAGE_NAME_BAIDU);
    }

    private static boolean isInstallPackage(Context context, String packageName) {
//        return new File("/data/data/" + packageName).exists();
        return isAPPInstalled(context, packageName);
    }

    /**
     * 获取打开百度地图应用uri [http://lbsyun.baidu.com/index.php?title=uri/api/android]
     *
     * @param originLat
     * @param originLon
     * @param originName  起点名
     * @param desLat
     * @param desLon
     * @param destination 终点名
     * @param region      城市名
     * @param src         AppName
     * @return
     */
    private static String getBaiduMapUri(String originLat, String originLon, String originName, String desLat, String desLon, String destination, String region, String src) {
        String uri = "intent://map/direction?origin=latlng:%1$s,%2$s|name:%3$s" +
                "&destination=latlng:%4$s,%5$s|name:%6$s&mode=driving&region=%7$s&src=%8$s#Intent;" +
                "scheme=bdapp;package=com.baidu.BaiduMap;end";

        return String.format(uri, originLat, originLon, originName, desLat, desLon, destination, region, src);
    }

    /**
     * 获取打开高德地图应用uri
     */
    private static String getGdMapUri(String appName, String slat, String slon, String sname, String dlat, String dlon, String dname) {
        String uri = "androidamap://route?sourceApplication=%1$s&slat=%2$s&slon=%3$s&sname=%4$s&dlat=%5$s&dlon=%6$s&dname=%7$s&dev=0&m=0&t=2";
        return String.format(uri, appName, slat, slon, sname, dlat, dlon, dname);
    }


    /**
     * 网页版百度地图 有经纬度
     *
     * @param originLat
     * @param originLon
     * @param originName
     * @param desLat
     * @param desLon
     * @param destination
     * @param region      当给定region时，认为起点和终点都在同一城市，除非单独给定起点或终点的城市。-->注：必填，不填不会显示导航路线
     * @param appName
     * @return
     */
    private static String getWebBaiduMapUri(String originLat, String originLon, String originName, String desLat, String desLon, String destination, String region, String appName) {
        String uri = "http://api.map.baidu.com/direction?origin=latlng:%1$s,%2$s|name:%3$s" +
                "&destination=latlng:%4$s,%5$s|name:%6$s&mode=driving&region=%7$s&output=html" +
                "&src=%8$s";
        return String.format(uri, originLat, originLon, originName, desLat, desLon, destination, region, appName);
    }

    /**
     * 启动高德地图进行导航
     */
    public static void openGaoDeNavi(Context context, double slat, double slon, String sname,
                                     double dlat, double dlon, String dname) {
        if (context == null) return;
        if (!isGdMapInstalled(context)) {
            Toast.makeText(context, "未安装高德地图", Toast.LENGTH_SHORT).show();
            return;
        }
        String uri = getGdMapUri(APP_NAME, String.valueOf(slat), String.valueOf(slon),
                sname, String.valueOf(dlat), String.valueOf(dlon), dname);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(MAP_PACKAGE_NAME_GAODE);
        intent.setData(Uri.parse(uri));
        context.startActivity(intent);
    }

    /**
     * 打开百度地图进行导航
     * 使用高德定位，则经纬度需要转换
     */
    public static void openBaiduNavi(Context context, double slat, double slon, String sname,
                                     double dlat, double dlon, String dname, String city) {
        if (context == null) return;
        if (!isBaiduMapInstalled(context)) {
            Toast.makeText(context, "未安装百度地图", Toast.LENGTH_SHORT).show();
            return;
        }
        //转换经纬度
        double[] sChange = gaoDeToBaidu(slat, slon);
        double[] dChange = gaoDeToBaidu(dlat, dlon);
        String uri = getBaiduMapUri(String.valueOf(sChange[0]), String.valueOf(sChange[1]), sname,
                String.valueOf(dChange[0]), String.valueOf(dChange[1]), dname, city, APP_NAME);
        Intent intent;
        try {
            intent = Intent.parseUri(uri, 0);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开浏览器进行百度地图导航
     */
    public static void openWebMap(Context context, double slat, double slon, String sname,
                                  double dlat, double dlon, String dname, String city) {
        Uri mapUri = Uri.parse(getWebBaiduMapUri(String.valueOf(slat), String.valueOf(slon), sname,
                String.valueOf(dlat), String.valueOf(dlon),
                dname, city, APP_NAME));
        Intent location = new Intent(Intent.ACTION_VIEW, mapUri);
        context.startActivity(location);
    }


    /**
     * 百度地图定位经纬度转高德经纬度
     *
     * @param bd_lat
     * @param bd_lon
     * @return
     */
    private static double[] bdToGaoDe(double bd_lat, double bd_lon) {
        double[] gd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        gd_lat_lon[0] = z * Math.cos(theta);
        gd_lat_lon[1] = z * Math.sin(theta);
        return gd_lat_lon;
    }

    /**
     * 高德地图定位经纬度转百度经纬度
     *
     * @param gd_lat
     * @param gd_lon
     * @return
     */
    private static double[] gaoDeToBaidu(double gd_lat, double gd_lon) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.sin(theta) + 0.006;
        bd_lat_lon[1] = z * Math.cos(theta) + 0.0065;
        return bd_lat_lon;
    }

    /**
     * 判断是否安装了某个应用
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean isAPPInstalled(Context context, String pkgName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }

}
