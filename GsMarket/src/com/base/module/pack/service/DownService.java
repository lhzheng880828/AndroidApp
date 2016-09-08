/****************************************************************************
 *
 * FILENAME:        com.base.module.service.DownService.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: 2012-2-6
 *
 * DESCRIPTION:     The class encapsulates the music ring tone operations.
 *
 * vi: set ts=4:
 *
 * Copyright (c) 2009-2011 by Grandstream Networks, Inc.
 * All rights reserved.
 *
 * This material is proprietary to Grandstream Networks, Inc. and,
 * in addition to the above mentioned Copyright, may be
 * subject to protection under other intellectual property
 * regimes, including patents, trade secrets, designs and/or
 * trademarks.
 *
 * Any use of this material for any purpose, except with an
 * express license from Grandstream Networks, Inc. is strictly
 * prohibited.
 *
 ***************************************************************************/
package com.base.module.pack.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.view.View;

import com.base.module.pack.main.AppActivity;
import com.base.module.pack.main.ExpandActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.base.module.language.GuiDisplay;
import com.base.module.pack.R;
import com.base.module.pack.bean.ApkInfo;
import com.base.module.pack.bean.GetApkInfo;
import com.base.module.pack.bean.Pack;
import com.base.module.pack.common.Utils;
import com.base.module.pack.dao.PackDao;
import com.base.module.pack.inter.PackDaoInter;
import com.base.module.pack.method.PackMethod;
import com.base.module.pack.method.SilentInstall;
import com.base.module.pack.method.MyTime;
import com.base.module.pack.main.Download;
import com.base.module.pack.provider.PackProvider;
import com.base.module.pack.task.TaskPool;
import com.base.module.pack.common.Log;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class DownService extends Service {
    protected static final String TAG = "DownService";
    private final static String DOWNLOAD_PATH = "/data/data/com.base.module.pack/package";
    private static ExecutorService executorService = Executors.newFixedThreadPool(15);
    private PackDaoInter mPackDao;
    private final static long mMaxFileSize = 50000000;
    protected int mNotice_id = 0;
    public final static int BEGIN_WAIT = 0;
    public final static int BEGIN_START = 1;
    public final static int END_WAIT = 2;
    public final static int REMOVE_QUEUE = 3;

    String mResendTag;
    private Notification mNotification = null;
    private NotificationManager manager = null;
    private long downTotalSize;
    private final static int LIMIT_DWNLOAD_COUNT = 2;
    private final int TOAST_ARG_0 = 0;
    private final int TOAST_ARG_1 = 1;
    //private final int TOAST_ARG_2 = 2;
    public final static int REMOVE_TYPE_STRING = 1;
    public final static int REMOVE_TYPE_TASK = 0;
    /**
     * the hole task, include wait tasks which is no place, which is
     * more of number
     */
    private static List<String> packLNames;
    private GuiDisplay mGuidisplay;
    /**
     * the wait tasks which is no place
     */
    private TaskPool mTaskPool;
    public static EndHandler mEndHandler;
    private ToastHandler mToastHandler;
    public static MyHandler mHandler;
    private Toast mToast;
    /**
     * the wait tasks which is more for number
     */
    private Queue<Pack> WaitQueue = new LinkedList<Pack>();
    private List<Intent> mIntents = new ArrayList<Intent>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPackDao = PackDao.getInstance(getBaseContext());
        downTotalSize = 0;
        mGuidisplay = GuiDisplay.instance();
        packLNames = new ArrayList<String>();
        mTaskPool = TaskPool.getInstance();
        mHandler = new MyHandler();
        mEndHandler = new EndHandler();
        mToastHandler = new ToastHandler();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mToast = Toast.makeText(DownService.this, "",Toast.LENGTH_LONG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // when package manager kill this service , then restart service ,  so intent is null
        // we have change downning app state .
        Log.d(intent +"");
        if (intent == null) {
            List<Pack> packs = mPackDao.findPacks();
            manager.cancel(mNotice_id);
            if (packs != null && !packs.isEmpty()) {
                for (Pack pack : packs) {
                    int packState = pack.getPackState();
                    if (packState == Pack.STATE_IN_DOWN ) {
                        mPackDao.failDownloadByLname(pack.getPackLName());
                    }else if (packState == Pack.STATE_IN_INSTALL) {
                        mPackDao.failInstallByLname(pack.getPackLName());
                    }
                }
            }
        }else {
            new beginThread(intent).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("onDestory  ");
        mPackDao.close();
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent name) {
        Log.d("stopService ");
        return super.stopService(name);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("onUnbind  ");
        return super.onUnbind(intent);
    }
    //  private beginThread mBeginThread = new beginThread();

    public class beginThread extends Thread {
        public beginThread(Intent _intent) {
            this.intent = _intent;
        }

        private Intent intent;
        private boolean flag = true;

        public void run() {
            beginTask(intent);
        }

        public void finish() {
            flag = false;
        }
    }

    private synchronized void beginTask(Intent intent) {
        if (intent == null) {
            return ;
        }
        String url = "";
        url = intent.getStringExtra("uri");
        String iconUrl = intent.getStringExtra("iconUrl");
        long fileSize = intent.getLongExtra("fileSize", 0);
        if(iconUrl==null){
            iconUrl="";
        }
        String downType = intent.getStringExtra("type");
        if (downType == null || downType.equals("")) {
            downType = Pack.DOWN_TYPE_NORMAL;
            url = intent.getStringExtra("uri");
        }

        if (!PackMethod.isHaveInternet(this)) {
            Message msg = mToastHandler.obtainMessage();
            msg.what = TOAST_ARG_0;
            msg.arg1 = 3358;
            mToastHandler.sendMessage(msg);
            return;
        }

        Log.d(TAG, "-----------------------beginTask---------------------url==" + url);
        //http://market.ipvideotalk.com:9023/filestorage//resources/software/Evernote_for_Android/Evernote_for_Android_4.2.1.apk
        //?packagename=com.evernote.world&appname=Evern
        if (PackMethod.isValid(url, false) || Pack.DOWN_TYPE_ICON.equals(downType)) {
            ApkInfo apkinfo = null;
            if (Pack.DOWN_TYPE_ICON.equals(downType)) {
                String appcode = intent.getStringExtra("appcode");
                if (appcode != null && !appcode.isEmpty()) {
                    apkinfo = GetApkInfo.getNewVersionApkInfo(appcode);
                }

            } else {
                Object ob = intent.getSerializableExtra(PackMethod.APK_INFO);
                if (ob == null) {
                    apkinfo = GetApkInfo.getApkInfo(url);
                    if(fileSize>0 || fileSize>apkinfo.getFilesize()){
                        apkinfo.setFilesize(fileSize);
                    }
                } else {
                    apkinfo = (ApkInfo) ob;
                }
            }
            if (apkinfo == null) {

                showToast(mGuidisplay.getValue(this, 3357));
            } else {
                boolean isUpdate = intent.getBooleanExtra("toUpdate", false);
                String mLname = apkinfo.getPackageName();
                String appName = apkinfo.getAppname();
                long packSize = apkinfo.getFilesize();

                if (!packLNames.contains(mLname)) {
                    if (isUpdate) {
                        Message msg = mToastHandler.obtainMessage();
                        msg.what = TOAST_ARG_1;
                        msg.arg1 = 3348;
                        msg.obj = appName;
                        mToastHandler.sendMessage(msg);
                    } else {
                        Message msg = mToastHandler.obtainMessage();
                        msg.what = TOAST_ARG_1;
                        msg.arg1 = 3319;
                        msg.obj = appName;
                        mToastHandler.sendMessage(msg);
                    }
                    packLNames.add(mLname);


                    Pack pack = new Pack();
                    pack.setPackVersion(apkinfo.getVersion());
                    pack.setPackDownRoute(apkinfo.getDownloadUrl());
                    pack.setPackDownload(downType);
                    pack.setPackName(appName);
                    pack.setPackSerial(apkinfo.getAppcode());
                    pack.setPackUpdateTime(MyTime.getdate());
                    pack.setPackNationRoute(DOWNLOAD_PATH);
                    pack.setPackState(Pack.STATE_WAIT);
                    pack.setPackLName(mLname);
                    pack.setPackSize(packSize);
                    pack.setIconUrl(iconUrl);
                    if (isUpdate) {
                        pack.setPackUpdate(Pack.IN_UPDATE);
                    }
                    Log.d(TAG + "addApkInfo isUpdate "+isUpdate);
                    addApkInfo(pack);
                    PackMethod.sendBrowserDataChangedBroader(this.getBaseContext(),appName);
                    File dir = new File(DOWNLOAD_PATH);
                    if (!dir.exists()) {
                        dir.mkdirs();
                        Utils.chmod(dir.getAbsolutePath(), "777");
                    }
                    long free = mMaxFileSize < dir.getFreeSpace() ? mMaxFileSize : dir.getFreeSpace();

                    if (free < packSize) {
                        Message msg = mToastHandler.obtainMessage();
                        msg.what = TOAST_ARG_1;
                        msg.arg1 = 3324;
                        msg.obj = appName;
                        mToastHandler.sendMessage(msg);
                    } else {
                        int downloadCount = mTaskPool.getSize();
                        if (downloadCount < LIMIT_DWNLOAD_COUNT && downTotalSize + packSize <= free) {
                            downTotalSize = downTotalSize + packSize;
                            Message msg = mHandler.obtainMessage();
                            msg.what = BEGIN_START;
                            msg.obj = pack;
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = mHandler.obtainMessage();
                            msg.what = BEGIN_WAIT;
                            msg.obj = pack;
                            mHandler.sendMessage(msg);
                        }
                    }
                } else {
                    Message msg = mToastHandler.obtainMessage();
                    msg.what = TOAST_ARG_1;
                    msg.arg1 = 3321;
                    msg.obj = appName;
                    mToastHandler.sendMessage(msg);
                }
            }


        } else {
            Message msg = mToastHandler.obtainMessage();
            msg.what = TOAST_ARG_0;
            msg.arg1 = 3343;
            mToastHandler.sendMessage(msg);
        }
    }

    public class DownloaderTask extends AsyncTask<String, Integer, Pack> {
        int TaskId;
        public String appCode;
        public String appName;
        private String uri;
        String l_name;
        private long length;
        //private boolean isStop = false;
        private long count = 0;
        public Boolean isUpdate = false;
        private Pack pack;		

        public DownloaderTask(Pack pack) {
            this.TaskId = (int) (Long.parseLong(MyTime.getdate()) % 100000);
            this.pack = pack;
            this.appCode = pack.getPackSerial();
            this.appName = pack.getPackName();
            this.l_name = pack.getPackLName();
            this.uri = pack.getPackDownRoute();
            if (pack.getPackUpdate() != null && pack.getPackUpdate().equals(Pack.IN_UPDATE)) {
                this.isUpdate = true;
            }
        }

        public long getCount() {
            return count;
        }

        public long getLength() {
            return length;
        }

        public int getTaskId() {
            return TaskId;
        }

        /*public boolean getIsStop() {
        return isStop;
    }*/

        public Pack getPack() {
            return pack;
        }

        public void setPack(Pack pack) {
            this.pack = pack;
        }


        @Override
        protected Pack doInBackground(String... params) {
            try {
                uri = uri.replaceAll(" ", "%20");
                HttpGet get = new HttpGet(uri);

                HttpParams httpParameters = new BasicHttpParams();
                int timeoutConnection = 3000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                int timeoutSocket = 5000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
                HttpResponse response = httpClient.execute(get);
                if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                    HttpEntity entity = response.getEntity();
                    length = pack.getPackSize();//entity.getContentLength();
                    /*if(-1!=length){
                        pack.setPackSize(length);
                    }else{
                        length=pack.getPackSize();
                    }*/

                    if (isPackLNames(l_name)) {
                        mTaskPool.put(getBaseContext(), l_name, this);
                        File file = new File(DOWNLOAD_PATH, appCode);
                        pack.setPackState(Pack.STATE_IN_DOWN);
                        addApkInfo(pack);
                        InputStream input = entity.getContent();
                        FileOutputStream fos = null;
                        BufferedInputStream bis = null;
                        BufferedOutputStream bos = null;
                        try {
                            fos = new FileOutputStream(file);
                            bis = new BufferedInputStream(input);
                            bos = new BufferedOutputStream(fos);
                            byte[] b = new byte[1024*1024];
                            int j = 0;
                            while (!this.isCancelled() && isCanceld != true && ((j = bis.read(b)) != -1)   /*&& isStop == false*/) {
                                bos.write(b, 0, j);
                                count += j;
                                if (length > count && (count/1024) % 50==0) {
                                    int progressnum = (int) ((count / (float) length) * 100);
                                    publishProgress(progressnum);
                                    if (packLNames.size() == 1) {
                                        setNotification(null,progressnum,appName,true);
                                    }
                                }
                            }
                            /*if (isStop == true) {
                            this.dowloadFail();
                        } else {*/
                            if(!this.isCancelled()){
                                Log.d("isCancelled false");
                               // pack.setPackSize(count);
                                Utils.chmod(file.getAbsolutePath(), "644");
                                publishProgress(100);
                                pack.setPackState(Pack.STATE_IN_INSTALL);

                            }else{
                                Thread.sleep(0);
                                Log.d("isCancelled true");
                            }

                            //}

                        } catch (Exception e) {
                            Log.d(pack.getPackName(),
                                    "e-------------------------two--------------------------"
                                            + e.toString());
                            this.onCancelled();
                        } finally {
                            if (bos != null) {
                                try {
                                    bos.flush();
                                    bos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (fos != null) {
                                        try {
                                            fos.flush();
                                            fos.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            if (bis != null) {
                                try {
                                    bis.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (input != null) {
                                        try {
                                            input.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }

                            if(entity !=null)
                                entity.consumeContent();
                        }

                        if (pack.getPackState() == Pack.STATE_IN_INSTALL) {
                            setPackageInfo(getBaseContext(), pack);

                        }
                    }/* else {
                    isStop = true;
                }*/

                }else{
                    this.onCancelled();
                }
            } catch (IOException e) {
                Log.d("IOException e2");
                this.onCancelled();
                e.printStackTrace();
            }		
            if (pack.getPackState() == Pack.STATE_IN_INSTALL) {
                publishProgress(101);
                silentInstall(this);
            }
            return pack;
        }

        @Override
        protected void onPostExecute(Pack pack) {

        }



        @Override
        protected void onPreExecute() {


            //super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //setNotification(noticeTitle,values[0],appName,false);
            //manager.notify(mNotice_id, mNotification);
            if (values[0] == 100) {
                removeWaiQueue(appName);
                if (!isUpdate) {
                    showToast(appName + " " + mGuidisplay.getValue(getBaseContext(), 3323));
                }
            }

            if(values[0] == 101){
                setNotification(appName + PackMethod.getSpace() +mGuidisplay.getValue(getBaseContext(), 3331),100,appName,true);
            }
        }

        boolean isCanceld =false;

        @Override
        protected void onCancelled() {
            Log.d("DownService onCancelled");
            if(!isCanceld){
                isCanceld = true;
            }else{
                return;
            }
            File file = new File(DOWNLOAD_PATH, this.appCode);
            if (file.isFile() && file.exists()) {
                Utils.deleteFile(file.getAbsolutePath());
            }
            Message msg = mEndHandler.obtainMessage(Pack.STATE_ER_DOWN);
            msg.arg1 = this.TaskId;
            msg.obj = this;
            mEndHandler.sendMessage(msg);

        }

        private Resources getResources(Context context, String apkPath) throws Exception {
            String PATH_AssetManager = "android.content.res.AssetManager";
            Class<?> assetMagCls = Class.forName(PATH_AssetManager);
            Constructor<?> assetMagCt = assetMagCls.getConstructor((Class[]) null);
            Object assetMag = assetMagCt.newInstance((Object[]) null);
            Class<?>[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath", typeArgs);
            Object[] valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
            Resources res = context.getResources();
            typeArgs = new Class[3];
            typeArgs[0] = assetMag.getClass();
            typeArgs[1] = res.getDisplayMetrics().getClass();
            typeArgs[2] = res.getConfiguration().getClass();
            Constructor<?> resCt = Resources.class.getConstructor(typeArgs);
            valueArgs = new Object[3];
            valueArgs[0] = assetMag;
            valueArgs[1] = res.getDisplayMetrics();
            valueArgs[2] = res.getConfiguration();
            res = (Resources) resCt.newInstance(valueArgs);
            return res;
        }

        private void setPackageInfo(Context context, Pack pack) {
            String apkPath = pack.getPackNationRoute() + "/" + pack.getPackSerial();
            PackageManager pm = context.getPackageManager();
            Log.d("apkPath "+apkPath);
            PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                String name = info.packageName;
                String className = info.applicationInfo.name;
                if (className != null) {
                    Log.i("CLASS NAME", className);
                }
                pack.setPackInstallInfor(className);
                String info_version = info.versionName;
                if (pack.getPackVersion() == null || !pack.getPackVersion().equals(info_version)) {
                    pack.setPackVersion(info_version);
                }
                pack.setPackVersionCode(info.versionCode);

                if (!name.equals(pack.getPackLName())) {
                    pack.setPackLName(name);
                }
                String iconName = pack.getPackLName();
                Resources res = null;
                try {
                    res = getResources(context, apkPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (res != null) {
                    Uri uri = null;
                    InputStream is = null;
                    OutputStream os = null;
                    is = res.openRawResource(info.applicationInfo.icon);
                    try {
                        if (is != null && is.available() > 0) {
                            os = PackProvider.getIconFileOutputStream(DownService.this, iconName);
                            if (Utils.saveFile(is, os)) {
                                uri = Uri.withAppendedPath(PackProvider.CONTENT_URI, iconName);
                                PackProvider.looserPermission(DownService.this, iconName);
                                pack.setIconUrl(uri.toString().trim());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                pack.setPackDelete(Pack.UN_DELETED);
                addApkInfo(pack);
            }
        }
    }


    private void silentInstall(DownloaderTask task) {
        SilentInstall installer = new SilentInstall(this, task, mEndHandler);
        SilentInstall.startInstall(installer);		
    }

    public class EndHandler extends Handler {
        public void handleMessage(Message msg) {
            Log.d(msg.what+"2");
            DownloaderTask task = (DownloaderTask) msg.obj;
            Pack pack = task.pack;
            String l_name = pack.getPackLName();
            //String class_name = pack.getPackInstallInfor();
            String app_name = pack.getPackName();
            String uri = pack.getPackDownRoute();
            String downType = pack.getPackDownload();
            String appcode = pack.getPackSerial();
            boolean isUpdate = false;
            if (pack.getPackUpdate() != null && pack.getPackUpdate().equals(Pack.IN_UPDATE))
                isUpdate = true;
            downTotalSize -= pack.getPackSize();
            mTaskPool.remove(getBaseContext(), l_name);
            packLNames.remove(l_name);
            try {
                File file = new File(DOWNLOAD_PATH, pack.getPackSerial());
                if (file.isFile() && file.exists())
                    file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //DownloaderTask pretask = mTaskPool.getPrevTask();
            if (packLNames.size() > 0) {
                /* if(pretask.getPack().getPackState()==Pack.STATE_IN_INSTALL){
                String s=pretask.getPack().getPackName() + PackMethod.getSpace() + mGuidisplay.getValue(getBaseContext(), 3331);
                setNotification(s,100,pretask.getPack().getPackName(),true);
                  }*/

                //if(packLNames.size()!=0){

                setNotification("",0,"",true);
                //}
            } else {
                manager.cancel(mNotice_id);

            }


            synchronized (o) {
                if (!WaitQueue.isEmpty() && WaitQueue.size() > 0) {

                    Pack pack_next;
                    Log.i("mTaskPool.getSize()", "" + mTaskPool.getSize());
                    int size = mTaskPool.getSize();
                    if (size < LIMIT_DWNLOAD_COUNT) {
                        while (LIMIT_DWNLOAD_COUNT > size) {
                            if (!WaitQueue.isEmpty() && WaitQueue.size() > 0) {
                                pack_next = WaitQueue.poll();
                                if (packLNames.contains(pack_next.getPackLName())) {
                                    DownloaderTask task_temp = new DownloaderTask(pack_next);
                                    task_temp.execute(pack_next.getPackDownRoute());
                                    size++;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            switch (msg.what) {
            case SilentInstall.INSTALL_COMPLETE:
                if (msg.arg2 == PackageManager.INSTALL_SUCCEEDED) {
                    if (isUpdate) {
                        showToast(app_name + " " + mGuidisplay.getValue(getBaseContext(), 3349));
                        pack.setPackUpdate("0");
                        pack.setPackState(0);
                        addApkInfo(pack);
                    } else {
                        showToast(app_name + " " + mGuidisplay.getValue(getBaseContext(), 3332));
                        PackMethod.sendInstallSuccessBroader(getBaseContext(), l_name, app_name, downType, uri, appcode);
                    }
                } else {
                    Log.d("no Install_succeeded");
                    if (isUpdate) {
                        String text = app_name + Utils.getSpace() + mGuidisplay.getValue(getBaseContext(), 3350);
                        if (msg.arg2 == PackageManager.INSTALL_FAILED_INSUFFICIENT_STORAGE) {
                            text = mGuidisplay.getValue(getBaseContext(), 3327) + text;
                        }

                        showToast(text);
                        Log.i(TAG, "Update Failed!!!");
                    } else {
                        String text = app_name + Utils.getSpace() + mGuidisplay.getValue(getBaseContext(), 3333);
                        if (msg.arg2 == PackageManager.INSTALL_FAILED_INSUFFICIENT_STORAGE) {
                            text = mGuidisplay.getValue(getBaseContext(), 3327) + text;
                        }
                        showToast(text);
                        Log.i(TAG, "Install Failed!!!");
                    }
                    if (mPackDao == null)
                        mPackDao = PackDao.getInstance(getBaseContext());
                    else if (!mPackDao.isOpen())
                        mPackDao.open();
                    mPackDao.failInstallByLname(l_name);
                    PackMethod.sendRefreshBroader(getBaseContext());
                }
                break;
            case Pack.STATE_ER_DOWN: {
                Log.d("Pack state_er_down " + isUpdate);
                if (isUpdate) {
                    showToast(app_name + " " + mGuidisplay.getValue(getBaseContext(), 3350));
                } else {
                    showToast(app_name + " " + mGuidisplay.getValue(getBaseContext(), 3315) + " !");
                    Log.i(TAG, "Down Failed!!!");
                }
                //setNotification();
                if (mPackDao == null)
                    mPackDao = PackDao.getInstance(getBaseContext());
                else if (!mPackDao.isOpen())
                    mPackDao.open();
                mPackDao.failDownloadByLname(l_name);
                PackMethod.sendRefreshBroader(getBaseContext());
            }
            break;
            default:
                break;
            }
            if (packLNames.isEmpty()) {
                SilentInstall.endThread();
            }
            PackMethod.sendBrowserDataChangedBroader(getBaseContext(),l_name);
        }
    };

    private void setNotification(String noticeTitle,int progress,String appName,boolean isInstall){
        if(mNotification==null){
            mNotification = new Notification(R.drawable.downloading_3, noticeTitle, System.currentTimeMillis());
            mNotification.flags = mNotification.flags | Notification.FLAG_ONGOING_EVENT;
            Intent intent = new Intent();
            intent.setClass(getBaseContext(), ExpandActivity.class);
            intent.putExtra(AppActivity.EXPANDFLAG, AppActivity.DOWNFLAG);
            mNotification.contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
        }
        mNotification.tickerText = noticeTitle;
        mNotification.contentView = new RemoteViews(getApplication().getPackageName(), R.layout.install_notice);
       // Log.d("packLNames.size()---------------- "+packLNames.size());
        if (packLNames.size() == 1) {
            mNotification.contentView.setViewVisibility(R.id.install_notice_processlayout,View.VISIBLE);
            mNotification.contentView.setViewVisibility(R.id.install_notice_title_progress,View.GONE);
            mNotification.contentView.setTextViewText(R.id.install_notice_percent,progress+"%");
            mNotification.contentView.setProgressBar(R.id.install_notice_pb,100,progress,false);
            mNotification.contentView.setTextViewText(R.id.install_notice_appname, appName);
            manager.notify(mNotice_id, mNotification);
        }else if(packLNames.size()>1){
            mNotification.contentView.setViewVisibility(R.id.install_notice_processlayout, View.GONE);
            mNotification.contentView.setViewVisibility(R.id.install_notice_title_progress,View.VISIBLE);
            mNotification.contentView.setTextViewText(R.id.install_notice_title_progress, "   ("
                    + packLNames.size() + PackMethod.getSpace() + mGuidisplay.getValue(getBaseContext(), 3355) + ")");
            manager.notify(mNotice_id, mNotification);
        }else{
            manager.cancelAll();//cancel(mNotice_id);
        }
    }

    public synchronized void addApkInfo(Pack pack) {
        if (mPackDao != null) {
            if (!mPackDao.isOpen()) {
                mPackDao.open();
            }
            mPackDao.insertOrUpdate(pack);
            PackMethod.sendRefreshBroader(getBaseContext());
        }
    }

    private void showToast(String text) {
        Log.d("Downservice show toast  " + text);
        mToast.setText(text);
        mToast.show();
    }

    class ToastHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(msg.arg1 + " " +msg.arg2);
            switch (msg.what) {
            case TOAST_ARG_0: {
                showToast(mGuidisplay.getValue(getBaseContext(), msg.arg1));
                break;
            }
            case TOAST_ARG_1: {
                showToast(msg.obj + Utils.getSpace() + mGuidisplay.getValue(getBaseContext(), msg.arg1));
                break;
            }
            /*case TOAST_ARG_2: {
            Log.i(TAG, "--------"+msg.obj);
            showToast(msg.obj.toString());
            break;
        }*/
            }
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "-------"+msg.obj.getClass().getName());

            Pack pack = null;
            switch (msg.what) {
            case BEGIN_WAIT: {
                pack = (Pack) msg.obj;
                WaitQueue.add(pack);
                break;
            }
            case BEGIN_START: {
                pack = (Pack) msg.obj;
                DownloaderTask task = new DownloaderTask(pack);
                task.executeOnExecutor(executorService,pack.getPackDownRoute());
                break;
            }
            case REMOVE_QUEUE: {
                removeWaiQueue((String) msg.obj);
                setNotification(null,0,null,false);
                return;
            }
            default:
                break;
            }
            boolean isUpdate=false ;
            if (pack.getPackUpdate() != null && pack.getPackUpdate().equals(Pack.IN_UPDATE)) {
                isUpdate = true;
            }
            String noticeTitle = "";
            if (!isUpdate) {
                noticeTitle =pack.getPackName() + PackMethod.getSpace() + mGuidisplay.getValue(getBaseContext(), 3325);
            } else{
                noticeTitle =pack.getPackName() + PackMethod.getSpace() + mGuidisplay.getValue(getBaseContext(), 61);
            }

            setNotification(noticeTitle,0,pack.getPackName(),false);
        }
    };

    public static boolean isPackLNames(String l_name) {
        if (packLNames != null && packLNames.contains(l_name)) {
            return true;
        }
        return false;
    }

    public void removeWaiQueue(String l_name) {
        packLNames.remove(l_name);
        for (Pack pack : WaitQueue) {
            if (pack.getPackLName().equals(l_name)) {
                WaitQueue.remove(pack);
                break;
            }
        }
    }
    public static Object o = new Object();
}