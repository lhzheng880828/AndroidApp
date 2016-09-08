package com.eeontheway.android.applocker.lock;

import android.content.ContentValues;
import android.content.Context;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.utils.SystemUtils;

import java.util.Calendar;

/**
 * 时间锁定配置
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class TimeLockCondition extends BaseLockCondition {
    public static int DAY7 = (1 << 7);
    public static int DAY6 = (1 << 6);
    public static int DAY5 = (1 << 5);
    public static int DAY4 = (1 << 4);
    public static int DAY3 = (1 << 3);
    public static int DAY2 = (1 << 2);
    public static int DAY1 = (1 << 1);

    private String startTime;
    private String endTime;
    private int day = DAY1|DAY2|DAY3|DAY4|DAY5|DAY6|DAY7;

    /**
     * 复制锁定信息
     * 只复制应用层的信息
     * @param lockConfig 锁定信息
     */
    public void copy (BaseLockCondition lockConfig) {
        super.copy(lockConfig);

        TimeLockCondition newConfig = (TimeLockCondition)lockConfig;
        this.startTime = newConfig.startTime;
        this.endTime = newConfig.endTime;
        this.day = newConfig.day;
    }

    /**
     * 克隆接口
     * @return
     */
    @Override
    public Object clone() {
        TimeLockCondition timeLockCondition = (TimeLockCondition)super.clone();
        timeLockCondition.startTime = new String(startTime);
        timeLockCondition.endTime = new String(endTime);
        return timeLockCondition;
    }

    /**
     * 判断两个对像否判断
     * @param condition 判断的对像
     * @return true/false
     */
    public boolean isMatch(BaseLockCondition condition) {
        boolean match = super.isMatch(condition);
        if (match) {
            if (condition instanceof TimeLockCondition) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 获取起始时间
     * @return 起始时间
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * 设置起始时间
     * @param startTime 起始时间
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取结束时间
     * @return 结束时间
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * 设置结束时间
     * @param endTime 结束时间
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取有效的日
     * @return 有效日
     */
    public int getDay() {
        return day;
    }

    /**
     * 设置有效日
     * @param day 有效日
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * 检查指定日是否被设置
     * @param day 取DAY7~DAY1
     * @return true/false
     */
    public boolean isDaySet (int day) {
        return (this.day & day) == day;
    }

    /**
     * 获取星期的字符串列表
     * @return 字符串列表
     */
    public String getDayString (Context context) {
        StringBuffer stringBuffer = new StringBuffer();

        if (day == 0) {
            stringBuffer.append(context.getString(R.string.all_day));
        } else {
            if (isDaySet(DAY1)) {
                stringBuffer.append(context.getString(R.string.week, context.getString(R.string.day1)));
            }
            if (isDaySet(DAY2)) {
                stringBuffer.append(context.getString(R.string.week, context.getString(R.string.day2)));
            }
            if (isDaySet(DAY3)) {
                stringBuffer.append(context.getString(R.string.week, context.getString(R.string.day3)));
            }
            if (isDaySet(DAY4)) {
                stringBuffer.append(context.getString(R.string.week, context.getString(R.string.day4)));
            }
            if (isDaySet(DAY5)) {
                stringBuffer.append(context.getString(R.string.week, context.getString(R.string.day5)));
            }
            if (isDaySet(DAY6)) {
                stringBuffer.append(context.getString(R.string.week, context.getString(R.string.day6)));
            }
            if (isDaySet(DAY7)) {
                stringBuffer.append(context.getString(R.string.week, context.getString(R.string.day7)));
            }
        }

        return stringBuffer.toString();
    }

    /**
     * 检查指定的日期是否在该范围内
     * @param calendar 日间
     * @return true/false
     */
    public boolean isMatch (Calendar calendar) {
        // 比较星期
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                if (!isDaySet(TimeLockCondition.DAY1)) {
                    return false;
                }
                break;
            case Calendar.TUESDAY:
                if (!isDaySet(TimeLockCondition.DAY2)) {
                    return false;
                }
                break;
            case Calendar.WEDNESDAY:
                if (!isDaySet(TimeLockCondition.DAY3)) {
                    return false;
                }
                break;
            case Calendar.THURSDAY:
                if (!isDaySet(TimeLockCondition.DAY4)) {
                    return false;
                }
                break;
            case Calendar.FRIDAY:
                if (!isDaySet(TimeLockCondition.DAY5)) {
                    return false;
                }
                break;
            case Calendar.SATURDAY:
                if (!isDaySet(TimeLockCondition.DAY6)) {
                    return false;
                }
                break;
            case Calendar.SUNDAY:
                if (!isDaySet(TimeLockCondition.DAY7)) {
                    return false;
                }
                break;
        }

        // 只比较时/分
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(SystemUtils.formatDate(getStartTime(), "HH:mm"));
        startCalendar.set(0, 0, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(SystemUtils.formatDate(getEndTime(), "HH:mm"));
        endCalendar.set(0, 0, 0);

        // 超始时间比结束时间早，正常方式比较
        if (startCalendar.compareTo(endCalendar) <= 0) {
            // 在start_time ~ end_time之间？匹配
            if ((calendar.compareTo(startCalendar) >= 0) && (calendar.compareTo(endCalendar) <= 0)) {
                return true;
            }
        } else {
            // 判断当前时间是否在0点之前
            Calendar zeroCalendra = Calendar.getInstance();
            zeroCalendra.set(0, 0, 23, 59, 59);

            // 在0点之前，比起始时间大，比结束时间也要大
            if (calendar.compareTo(zeroCalendra) <= 0) {
                if ((calendar.compareTo(startCalendar) >= 0) || (calendar.compareTo(endCalendar) >= 0)) {
                    return true;
                }
            } else {
                // 0点之后，比起始时间小，比结束时间也要小
                if ((calendar.compareTo(startCalendar) <= 0) || (calendar.compareTo(endCalendar) <= 0)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 获取对像名称
     * 该名称要求与数据库中表名相同
     * @return 对像名称
     */
    public String getName () {
        return "time_lock_config";
    }

    /**
     * 获取HashMap的值列表
     * 该名称要求与数据库中表名相同
     * @return 值列表
     */
    public ContentValues getMapValues () {
        ContentValues values = new ContentValues();

        values.putAll(super.getMapValues());
        values.put(ConditionDatabaseOpenHelper.TIME_FIELD_START_TIME, startTime);
        values.put(ConditionDatabaseOpenHelper.TIME_FIELD_END_TIME, endTime);
        values.put(ConditionDatabaseOpenHelper.TIME_FIELD_DAY, day);

        return values;
    }

    /**
     * 用ContentValues的值更新结构中的数据
     * ContentValues列名称要求与数据库中表名相同
     * @return 值列表
     */
    public void setMapValues (ContentValues values) {
        super.setMapValues(values);
        startTime = values.getAsString(ConditionDatabaseOpenHelper.TIME_FIELD_START_TIME);
        endTime = values.getAsString(ConditionDatabaseOpenHelper.TIME_FIELD_END_TIME);
        day = values.getAsInteger(ConditionDatabaseOpenHelper.TIME_FIELD_DAY);
    }
}
