package com.eeontheway.android.applocker.lock;

import java.util.ArrayList;
import java.util.List;

/**
 * 锁定条件工厂
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class LockConditionFactory {
    /**
     * 基于时间段的锁定类型
     */
    public static final int TYPE_TIME = 0;

    /**
     * 基于地理位置的锁定类型
     */
    public static final int TYPE_POSITION = 1;

    /**
     * 获取支持的条件类型列表
     * @return 类型列表
     */
    public static List<Integer> getConditionTypeList () {
        List<Integer> typeList = new ArrayList<>();
        typeList.add(TYPE_TIME);
        typeList.add(TYPE_POSITION);
        return typeList;
    }

    /**
     * 获取指定条件类型的名称
     * @return 名称
     */
    public static String createConditionTypeName (int type) {
        String name = null;

        switch (type) {
            case TYPE_TIME:
                name = ConditionDatabaseOpenHelper.time_lock_config_tableName;
                break;
            case TYPE_POSITION:
                name = ConditionDatabaseOpenHelper.position_lock_config_tableName;
                break;
        }
        return name;
    }

    /**
     * 获取指定类型名的各个配置项名
     * @param type 指定的条件类型
     * @return 配置名称列表
     */
    public static String [] createConditionFieldNames (int type) {
        String [] strings = null;

        switch (type) {
            case TYPE_TIME:
                strings = new String[5];
                strings[0] = ConditionDatabaseOpenHelper.BASE_COND_FIELD_ID;
                strings[1] = ConditionDatabaseOpenHelper.BASE_COND_FIELD_ENABLE;
                strings[2] = ConditionDatabaseOpenHelper.TIME_FIELD_START_TIME;
                strings[3] = ConditionDatabaseOpenHelper.TIME_FIELD_END_TIME;
                strings[4] = ConditionDatabaseOpenHelper.TIME_FIELD_DAY;
                break;
            case TYPE_POSITION:
                strings = new String[6];
                strings[0] = ConditionDatabaseOpenHelper.BASE_COND_FIELD_ID;
                strings[1] = ConditionDatabaseOpenHelper.BASE_COND_FIELD_ENABLE;
                strings[2] = ConditionDatabaseOpenHelper.POSITION_FIELD_LATITUDE;
                strings[3] = ConditionDatabaseOpenHelper.POSITION_FIELD_LONGITUDE;
                strings[4] = ConditionDatabaseOpenHelper.POSITION_FIELD_ADDRESS;
                strings[5] = ConditionDatabaseOpenHelper.POSITION_FIELD_RADIUS;
                break;
        }
        return strings;
    }

    /**
     * 根据类型创建锁定条件对像
     * @param type 比较的类型
     * @return 锁定条件对像
     */
    public static BaseLockCondition createCondition (int type) {
        BaseLockCondition condition = null;

        switch (type) {
            case TYPE_TIME:
                condition = new TimeLockCondition();
                break;
            case TYPE_POSITION:
                condition = new PositionLockCondition();
                break;
        }

        return condition;
    }
}
