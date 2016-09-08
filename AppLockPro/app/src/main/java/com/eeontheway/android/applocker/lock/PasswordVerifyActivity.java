package com.eeontheway.android.applocker.lock;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.locate.LocationService;
import com.eeontheway.android.applocker.locate.Position;
import com.eeontheway.android.applocker.main.SettingsManager;
import com.eeontheway.android.applocker.utils.CameraUtils;
import com.eeontheway.android.applocker.utils.DisplayUtil;
import com.eeontheway.android.applocker.ui.NumberPasswordView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 拦截应用启动的Activity
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class PasswordVerifyActivity extends AppCompatActivity {
    private final static String PARAM_PACKAGE_NAME = "packageName";
    private final static String PARAM_APP_NAME = "appName";
    private final static String PARAM_ICON_NAME = "iconName";

    private SettingsManager settingsManager;
    private LockConfigManager lockConfigManager;
    private CameraUtils cameraUtils;
    private String photoName;
    private AccessLog accessLog;

    private static AppCompatActivity activity;
    private static String lastPasswordErrorPackageName;
    private String packageName;
    private String appName;

    private TextView tv_name;
    private ImageView iv_icon;
    private NumberPasswordView cv_password;
    private TextView tv_password_error_alert;
    private SurfaceView camera_surfaceview;

    /**
     * Activity的onCreate回调
     * @param savedInstanceState 之前保存的状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_locker_password);

        activity = this;
        settingsManager = SettingsManager.getInstance(this);
        lockConfigManager = LockConfigManager.getInstance(this);

        initViews ();
        initCameraCapture();
    }

    /**
     * Activity的onDestroy回调
     */
    @Override
    protected void onDestroy() {
        // 清除Static引用，以释放内存
        activity = null;

        lockConfigManager.freeInstance();
        cameraUtils.close();
        super.onDestroy();
    }

    /**
     * 设计新的Intent
     * 因该Activity的启动模式为SignalInstance，多次启动会传递不同的参数。
     * 无下述代码将导致只能使用第一次的启动时的Intent
     * @param intent 新的Intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * Activity的OnStart()界面
     */
    @Override
    protected void onStart() {
        super.onStart();

        updateContentView();
    }

    /**
     * 回退按钮处理，什么都不做
     * 避免用户按下返回键关闭界面
     */
    @Override
    public void onBackPressed() {

    }

    /**
     * 刷新主界面显示
     */
    private void updateContentView () {
        // 获取应用信息
        packageName = getIntent().getStringExtra(PARAM_PACKAGE_NAME);
        appName = getIntent().getStringExtra(PARAM_APP_NAME);
        byte [] icon = getIntent().getByteArrayExtra(PARAM_ICON_NAME);

        // 刷新界面
        tv_name.setText(String.format(getString(R.string.app_locker_msg), appName));
        iv_icon.setImageBitmap(BitmapFactory.decodeByteArray(icon, 0, icon.length));
    }

    /**
     * 初始化界面
     */
    private void initViews() {
        tv_name = (TextView)findViewById(R.id.tv_name);
        iv_icon = (ImageView)findViewById(R.id.iv_icon);
        tv_password_error_alert = (TextView)findViewById(R.id.tv_password_error_alert);
        camera_surfaceview = (SurfaceView)findViewById(R.id.camera_surfaceview);

        // 确认按钮
        cv_password = (NumberPasswordView)findViewById(R.id.cv_password);
        cv_password.setMaxRetryCount(settingsManager.getCaptureOnFailCount());
        cv_password.setPasswordCallback(new NumberPasswordView.PasswordCallback() {
            @Override
            public boolean verifyPassword (String password) {
                // 检查密码是否匹配
                if (password.isEmpty()) {
                    return false;
                } else if (password.equals(settingsManager.getPassword())) {
                    // 如果密码相符，通知服务暂停对某个App的管控，并关闭自己
                    notifyPasswordOk();

                    // 判断是否有连续输入超过指定次数的密码，如果有的话，则弹窗提示
                    if (lastPasswordErrorPackageName != null) {
                        showLatestPasswordErrorInfo();
                    }
                    finish();

                    // 密码输入正确，重设包
                    lastPasswordErrorPackageName = null;
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onMaxErrorAccuor() {
                savePasswordErrorLog();
            }

            @Override
            public void onPasswordSetOk(String password) {
                // 不必实现
            }
        });
    }

    /**
     * 显示最近密码错误的信息
     */
    private void showLatestPasswordErrorInfo() {
        AccessLog accessLog = lockConfigManager.getAccessLog(lastPasswordErrorPackageName);
        if (accessLog != null) {
            // 启动日志显示界面
            LockLogActivity.startActivity(this, accessLog);
        }
    }

    /**
     * 初始化照片捕获
     */
    private void initCameraCapture() {
        cameraUtils = new CameraUtils(this);
        SurfaceHolder holder = camera_surfaceview.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraUtils.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        cameraUtils.setSurfaceHolder(holder);

        // 配置照片捕获后的处理
        cameraUtils.setOnPhotoCapturedListener(new CameraUtils.OnPhotoCapturedListener() {
            @Override
            public void onPhotoCaptured(Bitmap bitmap) {
                // 保存图片
                savePhoto(bitmap);
            }
        });
    }

    /**
     * 将文件保存到指定的内部路径中
     * @param fileName 文件保存的路径
     */
    private String savePhotoToInternal(byte [] imgData, String fileName) {
        // 直接在Data目录下的创建图像存储目录
        File dir = new File(getFilesDir().getPath() + File.separator + AccessLog.PHOTO_PATH_PREFIX);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.delete();
            dir.mkdir();
        }

        File file = new File(dir.getPath() + File.separator + fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(imgData);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("AppLocker", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("AppLocker", "Error accessing file: " + e.getMessage());
        }

        return file.getAbsolutePath();
    }

    /**
     * 将日志信息保存到数据库中
     * @param accessLog 日志信息
     */
    private void saveLogInfoToDatabase (AccessLog accessLog) {
        lockConfigManager.addAccessLog(accessLog);
    }

    /**
     * 将照片添加到相册中
     * @param bitmap 照片
     * @param fileName 存储的文件名
     * @return 存储的文件路径URI
     */
    private String savePhotoToGallery (Bitmap bitmap, String fileName) {
        String uri = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, fileName, "");
        return  uri;
    }

    /**
     * 保存密码错误的日志信息
     */
    private void savePasswordErrorLog () {
        // 获取各项配置参数，先保存进数据库
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HH_mm");
        Date date = new Date();
        photoName = packageName + "_" + dateFormat.format(date) + "_" +
                settingsManager.getCaptureOnFailCount();

        // 生成日志信息
        accessLog = new AccessLog();
        accessLog.setPackageName(packageName);
        accessLog.setAppName(appName);
        accessLog.setPasswordErrorCount(settingsManager.getCaptureOnFailCount());
        dateFormat.applyPattern("yyyy-MM-dd KK:mm:ss");
        accessLog.setTime(dateFormat.format(date));

        LocationService service = LocationService.getInstance(this);
        Position position = service.getLastPosition();
        String address;
        if ((position == null) || (position.getAddress() == null)) {
            address = getString(R.string.unknwon_location);
        } else {
            address = position.getAddress();
        }
        accessLog.setLocation(address);

        // 先启动照相并保存后，才能获取最近路径
        if (settingsManager.isCaptureOnFailEnable()) {
            startCapturePhoto();
        }

        // 获取最新路径后，才能保存至数据库中
        saveLogInfoToDatabase(accessLog);

        // 纪录超过错误次数的包名
        lastPasswordErrorPackageName = packageName;
    }

    /**
     * 启动摄像头抓拍程序
     */
    private void startCapturePhoto () {
        // 启动抓拍，如果不成功，则创建一个空的图片保存起来
        // 不成功的原因可能是不存在前置摄像头
        if (cameraUtils.startCapturePhoto() == false) {
            // 创建一个空白图像，存储起来
            savePhoto(Bitmap.createBitmap(400, 400, Bitmap.Config.RGB_565));
        }
    }

    /**
     * 保存照片
     * @param bitmap 需要保存的照片
     */
    private void savePhoto (Bitmap bitmap) {
        Bitmap newBitmap;
        Bitmap rotatedBitmap;

        // 创建新的Bitmap，注意处理图片旋转
        if (DisplayUtil.getDisplayOrient(this) == Configuration.ORIENTATION_PORTRAIT) {
            newBitmap = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(), bitmap.getConfig());
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            rotatedBitmap = bitmap;
        }

        // 先绘制照片
        Paint paint = new Paint();
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(rotatedBitmap, 0, 0, paint);

        // 再打上标记
        if (settingsManager.isAddTagToPhoto()) {
            float textSize = DisplayUtil.sp2px(this, 15);
            paint.setTextSize(textSize);
            paint.setColor(getResources().getColor(R.color.red));
            float startX = 10, startY = 10;
            canvas.drawText(getString(R.string.appLocker_err_detect, accessLog.getAppName()),
                    startX, 1 * textSize + startY, paint);
            canvas.drawText(getString(R.string.password_err_counter, accessLog.getPasswordErrorCount()),
                    startX, 2 * textSize + startY, paint);
            canvas.drawText(getString(R.string.time_args, accessLog.getTime()),
                    startX, 3 * textSize + startY, paint);
        }

        // 保存图像
        String path;
        if (!settingsManager.isCaptureOnFailPhotoInGallray()) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            path = savePhotoToInternal(stream.toByteArray(), photoName);
        } else {
            // 保存在相册，路径是在保存时才得知的
            path = savePhotoToGallery(newBitmap, photoName);
        }

        // 刷新照片存储路径
        accessLog.setPhotoPath(path);
        lockConfigManager.updateAccessLog(accessLog);

        // 释放资源
        newBitmap.recycle();
        if (!rotatedBitmap.isRecycled()) {
            rotatedBitmap.recycle();
        }
    }

    /**
     * 通知外界密码输入正确
     */
    private void notifyPasswordOk () {
        LockService.broadcastAppUnlocked(PasswordVerifyActivity.this, packageName);
    }

    /**
     * 启动该Activity
     * @param context 启动Activity的上下文
     * @param packageName 包名
     * @param flag 启动标记，可用于特殊用途
     */
    public static void startActivity (Context context, String packageName,
                                      String appName, byte [] icon, int flag) {
        Intent intent = new Intent(context, PasswordVerifyActivity.class);
        intent.putExtra(PARAM_PACKAGE_NAME, packageName);
        intent.putExtra(PARAM_APP_NAME, appName);
        intent.putExtra(PARAM_ICON_NAME, icon);
        intent.setFlags(flag);
        context.startActivity(intent);
    }

    /**
     * 关闭该Activity
     * 由于该Activity只有一份，所以该方法合理
     */
    public static void finishActivity () {
        if (activity != null) {
            activity.finish();
            activity = null;
        }
    }
}
