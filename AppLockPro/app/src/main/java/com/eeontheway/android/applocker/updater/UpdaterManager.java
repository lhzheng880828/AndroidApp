package com.eeontheway.android.applocker.updater;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.main.SettingsManager;
import com.eeontheway.android.applocker.utils.SystemUtils;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * APK升级文件器
 * 注意该管理器只能从Activity启动，相应的Activity要检查安装包运行器的返回结果
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class UpdaterManager {
    public static final int REQUEST_INSTALL_APP = 0;

    private static final String PREFS_NAME = "UpdatePref";
    private static final String PREF_SKIPPED_VERSION = "SkippedVersion";

    private Activity context;
    private SettingsManager settingsManager;
    private SimpleDateFormat simpleDateFormat;

    private IUpdateInfoGetter updateInfoGetter;
    private IUpdateLogManager updateLogManager;
    private IDownloadTool downloadTool;

    private ProgressDialog progressDialog;
    private int fileTotalSize;
    private UpdateInfo updateInfo;

    private boolean wifiConnected;
    private boolean mobileConnected;
    private int requestCode;

    /**
     * 构造器
     * @param context 上下文对像
     */
    public UpdaterManager(Activity context, int requestCode) {
        this.context = context;
        this.requestCode = requestCode;

        settingsManager = SettingsManager.getInstance(context);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        updateInfoGetter = UpdateInfoGetterFactory.create(context);
        updateLogManager = UpdateLogManagerFactory.create(context);
        downloadTool = DownloadToolFactory.create(context);

        progressDialog = new ProgressDialog(context);
    }

    /**
     * 检查网络状态
     */
    private void checkNetworkState () {
        wifiConnected = SystemUtils.isNetworkConnected(context, ConnectivityManager.TYPE_WIFI);
        mobileConnected = SystemUtils.isNetworkConnected(context, ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * 显示WIFI未连接的对话框，提示用户，比较耗流量
     */
    private void showWifiNotConntectedDialog (final String srcPath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.wifi_unavaliable);
        builder.setMessage(R.string.wifi_unavaliable_msg);
        builder.setIcon(R.drawable.ic_warning);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.continue_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppUpdate(srcPath);
            }
        });
        builder.setNegativeButton(R.string.abort_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 用户手动更新
     * 不管有没有WIFI都自动检查更新
     */
    public void manuUpdate (String srcPath) {
        // 检查WIFI状态
        checkNetworkState();

        // 如果WIFI和移动网络均不可用，提示用户
        if (!wifiConnected && !mobileConnected) {
            Toast.makeText(context, context.getString(R.string.network_unavaliable), Toast.LENGTH_SHORT);
            return;
        } else if (mobileConnected) {
            // 如果移动网络可用，弹出对话框让用户选择是否更新
            showWifiNotConntectedDialog(srcPath);
        } else {
            // 其它情况，进行更新
            startAppUpdate(srcPath);
        }
    }

    /**
     * 自动检查更新
     */
    public void autoUpdate (String srcPath) {
        // 检查WIFI状态
        checkNetworkState();

        // 当WIFI可用或者允许在移动网络下更新时，才更新；其它情况保持静默
        if (wifiConnected || (mobileConnected && !settingsManager.isUpdateOnlyWhenWifiConntected())) {
            startAppUpdate(srcPath);
        }
    }

    /**
     * 显示升级对话框
     */
    private void showUpdateInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.foundNewVersion);
        builder.setMessage(updateInfo.getUpdateLog());
        builder.setIcon(R.drawable.ic_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.download_install, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startDownloadAndInstall(updateInfo.getUrl());
                dialog.dismiss();
            }
        });

        // 1、如果WIFI不可用，允许用户停止更新
        if (!wifiConnected && (mobileConnected)) {
            builder.setCancelable(true);
            builder.setNegativeButton(R.string.update_later, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else if (updateInfo.isForece() == false) {
            // 强制更新，不显示取消按钮，用户必须升级
            builder.setNegativeButton(R.string.skip_this_version, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 升级被取消，保存取消日志
                    UpdateLog updateLog = updateLogManager.allocateUpdateLog(
                                                            SystemUtils.getVersionCode(context),
                                                            updateInfo.getVersionNum(),
                                                            true);
                    updateLogManager.saveUpdateLog(updateLog);

                    // 保存跳过信息
                    setVersionSkipped(updateInfo.getVersionNum());

                    dialog.dismiss();
                }
            });
        }

        builder.create().show();
    }


    /**
     * 检查是否需要更新
     * @return true/false
     */
    public boolean CheckIfNeedUpdate () {
        if (updateInfo.getVersionNum() > SystemUtils.getVersionCode(context)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取App更新信息
     * @param srcPath 更新信息的获取路径
     */
    public void startAppUpdate (String srcPath) {
        // 配置监听器
        updateInfoGetter.setResultListener(new IUpdateInfoGetter.ResultListener() {
            @Override
            public void onFail(int errorCode, String msg) {
                // 获取失败，提示信息
                Toast.makeText(context, context.getString(R.string.checkUpdateFaild, msg),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(UpdateInfo info) {
                // 获取成功，缓存信息
                updateInfo = info;
                if (CheckIfNeedUpdate()) {
                    if (isVersionSkipped(info.getVersionNum())) {
                        Toast.makeText(context, context.getString(R.string.version_skipped),
                                                    Toast.LENGTH_SHORT).show();
                    } else {
                        showUpdateInfoDialog();
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.no_need_update),
                                                    Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 请求获取信息，之后就继续开始下载更新
        updateInfoGetter.requestGetInfo(srcPath);
    }


    /**
     * 显示下载进度框
     */
    private void showDownloadProgressDialog () {
        progressDialog.setMessage(context.getString(R.string.downloading));
        progressDialog.setTitle(context.getString(R.string.updating));
        progressDialog.setMax(fileTotalSize);
        progressDialog.setIcon(R.drawable.ic_download);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

    /**
     * 检查指定版本是否已经被跳过
     * @param versionNum 待检查的版本
     * @return true 跳过; fase 未跳过
     */
    private boolean isVersionSkipped (int versionNum) {
        SharedPreferences version = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int skippedVersion = version.getInt(PREF_SKIPPED_VERSION, 0);
        return (skippedVersion >= versionNum);
    }

    /**
     * 设置跳过指定的版本
     * @param versionNum 待设定的版本
     */
    private void setVersionSkipped (int versionNum) {
        SharedPreferences version = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = version.edit();
        editor.putInt(PREF_SKIPPED_VERSION, versionNum);
        editor.commit();
    }

    /**
     * 开始下载并安装App
     * @param srcPath 下载的源文件路径
     */
    public void startDownloadAndInstall (String srcPath) {
        // 保存在外部SD卡上
        final String destPath = Environment.getExternalStorageDirectory() + File.separator +
                            new File(srcPath).getName();

        // 配置下载监听器
        downloadTool.setDownloadListener(new IDownloadTool.DownloadResultListener() {
            @Override
            public void onStart(int totalSize) {
                // 启动下载，显示下载进度条
                fileTotalSize = totalSize;
                showDownloadProgressDialog();
            }

            @Override
            public void onProgress(int downloadedSize) {
                // 正在下载，刷新进度条
                progressDialog.setProgress(downloadedSize);
            }

            @Override
            public void onFail(int errorCode, String msg) {
                // 下载失败，提示信息，关闭下载进度对话框
                Toast.makeText(context, context.getString(R.string.download_failed, msg),
                                                    Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess() {
                // 下载完成后，不管用户有没有安装，都假定升级完成
                UpdateLog updateLog = updateLogManager.allocateUpdateLog(
                                                    SystemUtils.getVersionCode(context),
                                                    updateInfo.getVersionNum(),
                                                    false);
                updateLogManager.saveUpdateLog(updateLog);

                // 下载成功，安装App
                SystemUtils.installApp(context, destPath, true, requestCode);

                progressDialog.hide();
            }
        });

        // 启动下载
        downloadTool.startDownload(srcPath, destPath);
    }
}
