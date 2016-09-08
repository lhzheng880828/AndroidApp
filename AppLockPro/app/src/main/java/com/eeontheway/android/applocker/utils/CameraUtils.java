package com.eeontheway.android.applocker.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * 摄像头抓拍功能类
 * Fix: 暂不用camera2，后续考虑兼容性
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class CameraUtils {
    private Activity activity;
    private Camera camera;
    private boolean isOpened;
    private OnPhotoCapturedListener listener;

    /**
     * 构造函数
     * @param activity 上下文
     */
    public CameraUtils (Activity activity) {
        this.activity = activity;
    }

    /**
     * 打开摄像头
     * @param facing 摄像头类型：Camera.CameraInfo.CAMERA_FACING_BACK/CAMERA_FACING_FRONT
     * @return true 打开成功; false 打开失败
     */
    public boolean open (int facing) {
        // 检查是否有摄像头
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return false;
        }

        // 遍历摄像头信息
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraNum = Camera.getNumberOfCameras();
        for (int i = 0;  i < cameraNum; i++) {
            // 找出类型匹配报像头
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facing) {
                try {
                    // 打开报像头
                    camera = Camera.open(i);

                    // 设置打开标记
                    isOpened = true;
                    return true;
                } catch (Exception e) {
                    camera = null;
                }
            }
        }
        return false;
    }

    /**
     * 关闭摄像头
     */
    public void close () {
        camera.release();
    }

    /**
     * 监听器：当图片捕获后调用
     */
    public interface OnPhotoCapturedListener {
        void onPhotoCaptured (Bitmap bitmap);
    }

    /**
     * 设置照片捕获成功的监听器
     * @param listener 监听器
     */
    public void setOnPhotoCapturedListener (OnPhotoCapturedListener listener) {
        this.listener = listener;
    }

    /**
     * 抓取照片
     */
    public boolean startCapturePhoto () {
        // 如果没有打开，直接退出
        if (isOpened == false) {
            return false;
        }

        camera.startPreview();

        // 抓取照片
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] imgData, Camera camera) {
                if (listener != null) {
                    // 转化成Bitmap，再由外部处理
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
                    listener.onPhotoCaptured(bitmap);
                    bitmap.recycle();
                }
            }
        });

        return true;
    }

    /**
     * 设置Holder
     * @param holder 监听Holder
     */
    public void setSurfaceHolder (SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
