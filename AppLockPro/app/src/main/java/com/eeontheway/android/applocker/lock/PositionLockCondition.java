package com.eeontheway.android.applocker.lock;

import android.content.ContentValues;
import android.location.Location;

import com.eeontheway.android.applocker.locate.Position;


/**
 * GPS锁定配置
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class PositionLockCondition extends BaseLockCondition {
    private Position position;

    /**
     * 获取位置
     * @return 位置
     */
    public Position getPosition() {
        return position;
    }

    /**
     * 设置位置
     * @param position 位置
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * 判断两个对像否判断
     * @param condition 判断的对像
     * @return true/false
     */
    public boolean isMatch(BaseLockCondition condition) {
        boolean match = super.isMatch(condition);
        if (match) {
            if (condition instanceof PositionLockCondition) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 复制锁定信息
     * 只复制应用层的信息
     * @param lockConfig 锁定信息
     */
    public void copy (BaseLockCondition lockConfig) {
        super.copy(lockConfig);

        PositionLockCondition newConfig = (PositionLockCondition)lockConfig;
        this.position = newConfig.position;
    }

    /**
     * 克隆接口
     * @return
     */
    @Override
    public Object clone() {
        PositionLockCondition condition = (PositionLockCondition)super.clone();
        return condition;
    }

    /**
     * 检查指定的地址是否在该地址范围内
     * @param cmpPosition 待检查的地址
     * @return true/false
     */
    public boolean isMatch (Position cmpPosition) {
        float[] result = new float[1];

        Location.distanceBetween(position.getLatitude(), position.getLongitude(),
                cmpPosition.getLatitude(), cmpPosition.getLongitude(), result);
        if (result[0] >= position.getRadius()) {
            return false;
        }

        return true;
    }

    /**
     * 获取对像名称
     * 该名称要求与数据库中表名相同
     * @return 对像名称
     */
    public String getName () {
        return "gps_lock_config";
    }

    /**
     * 获取HashMap的值列表
     * 该名称要求与数据库中表名相同
     * @return 值列表
     */
    public ContentValues getMapValues () {
        ContentValues values = new ContentValues();

        values.putAll(super.getMapValues());
        values.put(ConditionDatabaseOpenHelper.POSITION_FIELD_RADIUS, position.getRadius());
        values.put(ConditionDatabaseOpenHelper.POSITION_FIELD_LATITUDE, position.getLatitude());
        values.put(ConditionDatabaseOpenHelper.POSITION_FIELD_LONGITUDE, position.getLongitude());
        values.put(ConditionDatabaseOpenHelper.POSITION_FIELD_ADDRESS, position.getAddress());

        return values;
    }

    /**
     * 用ContentValues的值更新结构中的数据
     * ContentValues列名称要求与数据库中表名相同
     * @return 值列表
     */
    public void setMapValues (ContentValues values) {
        super.setMapValues(values);
        if (position == null) {
            position = new Position();
        }
        position.setRadius(values.getAsFloat(ConditionDatabaseOpenHelper.POSITION_FIELD_RADIUS));
        position.setLatitude(values.getAsDouble(ConditionDatabaseOpenHelper.POSITION_FIELD_LATITUDE));
        position.setLongitude(values.getAsDouble(ConditionDatabaseOpenHelper.POSITION_FIELD_LONGITUDE));
        position.setAddress(values.getAsString(ConditionDatabaseOpenHelper.POSITION_FIELD_ADDRESS));
    }
}
