package com.eeontheway.android.applocker.updater;

/**
 * 升级日志纪录接口
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public interface IUpdateLogManager {
    /**
     * 日志保存结果的监听器
     */
    interface SaveResultListener {
        /**
         * 获取失败
         * @param errorCode 错误码
         * @param msg 错误消息
         */
        void onFail (int errorCode, String msg);

        /**
         * 获取成功
         */
        void onSuccess ();
    }

    /**
     * 设置获取结果监听器
     * @param listener
     */
    void setSaveResultListener(SaveResultListener listener);

    /**
     * 分配一个升级日志对像
     * @return  日志对像
     */
    UpdateLog allocateUpdateLog (int oldVersion, int newVersion, boolean skipped);

    /**
     * 保存日志信息
     * @param log 待保存的日志信息
     */
    void saveUpdateLog (UpdateLog log);
}
