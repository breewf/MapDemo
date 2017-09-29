package com.ghy.mapdemo.location;


import com.ghy.mapdemo.location.entity.Location;

/**
 * 定位回调
 * Created by wang on 2015/12/23.
 */
public interface OnLocationListener {
    /**
     * 定位成功返回结果
     *
     * @param location 定位结果
     */
    void onSuccess(Location location);

    /**
     * 定位失败
     *
     * @param errInfo 错误信息
     */
    void onFailure(String errInfo);
}
