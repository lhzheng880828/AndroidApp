package com.eeontheway.android.applocker.updater;


/**
 * 文件下载器（单线程)
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public interface IDownloadTool {
    /**
     *  下载结果监听器
     */
    interface DownloadResultListener {
        /**
         * 开始下载回调
         * @param totalSize 总的下载大小
         */
        void onStart (int totalSize);

        /**
         * 下载过程回调
         * @param donwloadedSize 已经完成的下载量
         */
        void onProgress(int donwloadedSize);

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
     * 开始下载文件
     * @param srcPath 源上载
     * @param destPath 文件存储目录
     */
    void startDownload (String srcPath, String destPath);

    /**
     * 设置下载结果监听器
     * @param downloadListener 下载结果监听器
     */
    void setDownloadListener(DownloadResultListener downloadListener);
}
