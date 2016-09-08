package com.eeontheway.android.applocker.updater;

/**
 * 升级信息获取器接口
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public interface IUpdateInfoGetter {
    /**
     * 获取结果的监听器
     */
    interface ResultListener {
        /**
         * 获取失败
         * @param errorCode 错误码
         * @param msg 错误消息
         */
        void onFail (int errorCode, String msg);

        /**
         * 获取成功
         * @param info 获取的结果
         */
        void onSuccess (UpdateInfo info);
    }

    /**
     * 初始化
     */
    void init ();

    /**
     * 反初始化
     */
    void unInit ();

    /**
     * 设置获取结果监听器
     * @param listener
     */
    void setResultListener(ResultListener listener);

    /**
     * 请求获取升级信息结构
     * @param src 获取的源地址
     */
    void requestGetInfo (String src);
}
