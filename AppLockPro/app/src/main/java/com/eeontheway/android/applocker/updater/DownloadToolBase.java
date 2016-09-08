package com.eeontheway.android.applocker.updater;


/**
 * 文件下载器（单线程)
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public abstract class DownloadToolBase implements IDownloadTool {
    protected DownloadResultListener downloadListener;

    /**
     * 设置下载结果监听器
     * @param downloadListener 下载结果监听器
     */
    public void setDownloadListener(DownloadResultListener downloadListener) {
        this.downloadListener = downloadListener;
    }
}
