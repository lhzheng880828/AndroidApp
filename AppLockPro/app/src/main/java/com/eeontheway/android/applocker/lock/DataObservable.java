package com.eeontheway.android.applocker.lock;

import java.util.Observable;


/**
 * 数据变化通知器
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class DataObservable extends Observable {
    private boolean enable = true;

    /**
     * 设置是否使能监听器
     * @param enable 是否使能
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * 通知某个项数据发生改变
     * @param dataType 数据类型
     */
    void notifyItemChanged (DataType dataType) {
        ObserverInfo observerInfo = new ObserverInfo();
        observerInfo.dataType = dataType;
        observerInfo.changeType = ChangeType.UNKNOWN;
        observerInfo.startPos = 0;
        observerInfo.count = 0;

        notify(observerInfo);
    }

    /**
     * 通知某个项数据发生改变
     * @param dataType 数据类型
     * @param position 修改的位置
     */
    void notifyItemChanged (DataType dataType, int position) {
        ObserverInfo observerInfo = new ObserverInfo();
        observerInfo.dataType = dataType;
        observerInfo.changeType = ChangeType.UPDATE;
        observerInfo.startPos = position;
        observerInfo.count = 1;

        notify(observerInfo);
    }

    /**
     * 通知某个位置插入了1个新数据
     * @param dataType 数据类型
     * @param position 插入的位置
     */
    void notifyItemInserted(DataType dataType, int position, int itemCount) {
        ObserverInfo observerInfo = new ObserverInfo();
        observerInfo.dataType = dataType;
        observerInfo.changeType = ChangeType.INSERT;
        observerInfo.startPos = position;
        observerInfo.count = itemCount;

        notify(observerInfo);
    }

    /**
     * 通知某个起始位置删除了多个数据
     * @param dataType 数据类型
     * @param positionStart 删除的起始位置
     * @param itemCount 删除的数据类型数据
     */
    void notifyItemRangeRemoved(DataType dataType, int positionStart, int itemCount) {
        ObserverInfo observerInfo = new ObserverInfo();
        observerInfo.dataType = dataType;
        observerInfo.changeType = ChangeType.REMOVE;
        observerInfo.startPos = positionStart;
        observerInfo.count = itemCount;

        notify(observerInfo);
    }

    /**
     * 通知各个监听器
     * @param info 监听的消息
     */
    private void notify (ObserverInfo info) {
        if (enable) {
            setChanged();
            super.notifyObservers(info);
        }
    }

    public enum ChangeType {
        UNKNOWN,            // 未知，只知是有变化
        UPDATE,             // 更新操作
        INSERT,             // 插入操作
        REMOVE,             // 删除操作
    }


    public enum DataType {
        MODE_LIST,          // 模式列表
        APP_LIST,           // App列表
        CONDITION_LIST,     // 锁定条件列表
        LOG_LIST            // 消息列表
    }

    /**
     * 用于数据库变化的通知器
     *
     * @author lishutong
     * @version v1.0
     * @Time 2016-12-15
     */
    public class ObserverInfo {
        public DataType dataType;           // 数据类型
        public ChangeType changeType;       // 操作类型
        public int startPos;                // 起始Item序号
        public int count;                   // 发生变化的数量
    }
}