package com.eeontheway.android.applocker.updater;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Http文件下载器（单线程)
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class HttpDownloadTool extends DownloadToolBase {
    private Context context;
    private final int DOWNLOAD_START = 0;
    private final int DOWNLOAD_PROGRESS = 1;
    private final int DOWNLOAD_END = 2;

    private String urlPath;
    private String destPath;
    private Handler handler;

    /**
     * 构造函数
     */
    public HttpDownloadTool(Context context) {
        this.context = context;
        handler = new DownloadHandler();
    }

    /**
     * 启动文件下载
     * @param srcPath 源文件URL
     * @param destPath 目标文件存储路径
     */
    public void startDownload (String srcPath, String destPath) {
        this.urlPath = srcPath;
        this.destPath = destPath;

        new Thread(new DownloadRunnable()).start();
    }

    /**
     * 发送开始下载信息给主线程
     * @param totalSize 总的文件大小
     */
    private void sendStartMessage (int totalSize) {
        Message message = handler.obtainMessage();
        message.what = DOWNLOAD_START;
        message.arg1 = totalSize;
        handler.sendMessage(message);
    }

    /**
     * 发送下载进度信息给主线程
     * @param downloadedSize 已下载的数据量
     */
    private void sendProgressMessage (int downloadedSize) {
        Message message = handler.obtainMessage();
        message.what = DOWNLOAD_PROGRESS;
        message.arg1 = downloadedSize;
        handler.sendMessage(message);

    }

    /**
     * 发送最终操作结果信息
     * @param result 操作结果码, 非0表示有错误
     * @param errorMessage 错误信息串
     */
    private void sendEndMessage (int result, String errorMessage) {
        Message message = handler.obtainMessage();
        message.what = DOWNLOAD_END;
        message.arg1 = result;
        message.obj = errorMessage;
        handler.sendMessage(message);
    }

    /**
     * 与下载线程通信的事件处理器
     */
    private class DownloadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DOWNLOAD_START) {
                downloadListener.onStart(msg.arg1);
            } else if (msg.what == DOWNLOAD_PROGRESS) {
                downloadListener.onProgress(msg.arg1);
            } else {
                if (msg.arg1 == 0) {
                    downloadListener.onSuccess();
                } else {
                    downloadListener.onFail(msg.arg1, (String)msg.obj);
                }
            }
        }
    }

    /**
     * 下载线程
     */
    private class DownloadRunnable implements Runnable {
        private HttpURLConnection httpURLConnection;
        private InputStream is;
        private FileOutputStream fo;


        @Override
        public void run() {
            try {
                // 发送Http请求
                URL url = new URL(urlPath);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
                httpURLConnection.setConnectTimeout(5000);

                // 发生错误，立即退出
                if (httpURLConnection.getResponseCode() != 200) {
                    sendEndMessage(-1, httpURLConnection.getResponseMessage());
                    return;
                }

                // 服务器连接成功
                int totalSize = httpURLConnection.getContentLength();
                sendStartMessage(totalSize);

                // 读取整个文件，写入到目标文件中
                is = httpURLConnection.getInputStream();
                fo = new FileOutputStream(destPath);
                byte[] buffer = new byte[2048];
                int downloadedSize = 0;
                while (downloadedSize < totalSize) {
                    int currentSize = is.read(buffer);
                    if (currentSize > 0) {
                        fo.write(buffer, 0, currentSize);

                        downloadedSize += currentSize;
                        sendProgressMessage(downloadedSize);
//
//                        try {
//                            Thread.sleep(0);
//                        }catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    }
                }
                sendEndMessage(0, null);
            } catch (Exception e) {
                e.printStackTrace();
                sendEndMessage(-1, e.getMessage());
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (fo != null) {
                        fo.close();
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                    sendEndMessage(-1, e.getMessage());
                }

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }
    }
}
