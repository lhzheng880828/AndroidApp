package com.eeontheway.android.applocker.locate;

import java.io.Serializable;

/**
 * 位置描述类
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class Position implements Serializable, Cloneable {
    private float radius;
    private double latitude;
    private double longitude;
    private long time;
    private String address = "unknown";

    /**
     * 用一个位置更新本位置
     * @param postion 新位置
     */
    public void update (Position postion) {
        this.latitude = postion.latitude;
        this.longitude = postion.longitude;
        this.address = postion.address;
    }

    @Override
    public Position clone() {
        Position position = null;

        try {
            position = (Position) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return position;
    }

    /**
     * 获取有效半径
     * @return 半径
     */
    public float getRadius() {
        return radius;
    }

    /**
     * 设置半径
     * @param radius 半径
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * 获取纬度
     * @return
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * 设置纬度
     * @param latitude 纬度
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * 获取经度
     * @return 经度
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * 设置经度
     * @param longitude 经度
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * 获取所在的地址
     * @return 所在地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置所在地址
     * @param address 所在地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取定位的时间
     *
     * @return 定位时间
     */
    public long getTime() {
        return time;
    }

    /**
     * 设置定位时间
     *
     * @param time 定位时间
     */
    public void setTime(long time) {
        this.time = time;
    }
}
