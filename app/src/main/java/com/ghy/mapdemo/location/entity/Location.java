package com.ghy.mapdemo.location.entity;

/**
 * 定位信息实体
 * Created by wang on 2015/12/23.
 */
public class Location {

    private double latitude;//纬度
    private double Longitude;//经度
    private String time;//定位成功时间
    private String address;//详细地址
    private String province;//省
    private String city;//市
    private String district;//区
    private String cityCode;//cityCode

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", Longitude=" + Longitude +
                ", time='" + time + '\'' +
                ", address='" + address + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", cityCode='" + cityCode + '\'' +
                '}';
    }
}
