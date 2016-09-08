package com.eeontheway.android.applocker.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.utils.SystemUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * 系统启动时显示的Activiy
 * 主要用于显示整个启动进度，Logo等信息
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class StartupActivity extends AppCompatActivity {
    private static final String COPY_ITEM_NAME = "item";
    private static final String COPY_ITEM_SOURCE = "SourcePath";
    private static final String COPY_ITEM_DEST = "DestPath";
    private boolean animationPlayOver = false;
    private boolean copyAssertFileOk = false;

    /**
     * 启动该Activity
     * @param context 上下文
     */
    public static void start (Context context) {
        Intent intent = new Intent(context, StartupActivity.class);
        context.startActivity(intent);
    }

    /**
     * Activity的OnCreate回调
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        initViews();
        startLoadAllResource();

        // Debug
        String sourcePath = getDatabasePath("applocklist.db").getPath();
        String destPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "applocklist.db";

        InputStream reader = null;
        OutputStream writer = null;
        try {
            File destFile = new File(destPath);

            // 如果目标文件比较旧，则说明需发从App资源中拷贝覆盖
            reader = new FileInputStream(new File(sourcePath));
            writer = new FileOutputStream(destFile);

            // 开始复制文件
            byte[] buffer = new byte[1024];
            int currentCount = 0;
            while ((currentCount = reader.read(buffer, 0, buffer.length)) != -1) {
                writer.write(buffer, 0, currentCount);
            }

            // 结束读取，刷新缓冲，关闭输入输出流
            writer.flush();
            writer.close();
            reader.close();
        } catch (IOException e) {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 加载所有需要的资源
     */
    private void startLoadAllResource() {
        // 由于加载比较耗时，所以使用了异步任务去完整所有事项
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                copyAssertFile();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                copyAssertFileOk = true;
                loadMainUI();
            }
        }.execute(null, null, null);
    }

    /**
     * 将Assert目录下的所有文件拷到file目录下
     * 该过程比较费时，需要放到另一个线程中去处理
     */
    private void copyAssertFile() {
        // 解析xml文件中的拷贝配置
        Map<String, String> copyConfigMap = new HashMap<>();
        XmlResourceParser parse = getResources().getXml(R.xml.files_copy_config);
        try {
            // 初始解析器的状态为查看item
            XML_STATE state = XML_STATE.FIND_ITEM;

            // 循环解析xml，提取各个配置
            int eventType = parse.getEventType();
            String sourceFile = null;
            String destFile = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String tagName = parse.getName();
                        if (tagName.equals(COPY_ITEM_NAME)) {
                            // 找到item，则下一步的动作为查看源和目标
                            state = XML_STATE.FIND_PATH;
                        } else if (tagName.equals(COPY_ITEM_SOURCE) && (state == XML_STATE.FIND_PATH)) {
                            // 找到源，并且当前确实是要查找路径，则存储配置
                            sourceFile = parse.nextText();
                        } else if (tagName.equals(COPY_ITEM_DEST) && (state == XML_STATE.FIND_PATH)) {
                            // 找到目标，并且当前确实是要查找路径，则储配置
                            destFile = parse.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        // 如果当前是结束item查询，则结束当前item查找
                        String endTagName = parse.getName();
                        if (endTagName.equals(COPY_ITEM_NAME)) {
                            // 存储取得的路径配置
                            if ((sourceFile != null) && (destFile != null)) {
                                copyConfigMap.put(sourceFile, destFile);
                            }

                            // 开始新的查询配置
                            state = XML_STATE.FIND_ITEM;
                            sourceFile = null;
                            destFile = null;
                        }
                        break;
                }

                eventType = parse.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 遍历执行文件复制过程
        long appInstalledTime = SystemUtils.getAppInstalledTime(this);
        for (Map.Entry entry : copyConfigMap.entrySet()) {
            String rootPath = getFilesDir().getPath();
            String sourcePath = (String) entry.getKey();
            String destPath = (String) entry.getValue();

            InputStream reader = null;
            OutputStream writer = null;
            try {
                File destFile = new File(rootPath + "/" + destPath);

                // 比较App的安装时间和目标文件的修改时间
                if (!destFile.exists() || (destFile.lastModified() < appInstalledTime)) {
                    // 如果目标文件比较旧，则说明需发从App资源中拷贝覆盖
                    reader = getAssets().open(sourcePath);
                    writer = new FileOutputStream(destFile);

                    // 开始复制文件
                    byte[] buffer = new byte[1024];
                    int currentCount = 0;
                    while ((currentCount = reader.read(buffer, 0, buffer.length)) != -1) {
                        writer.write(buffer, 0, currentCount);
                    }

                    // 结束读取，刷新缓冲，关闭输入输出流
                    writer.flush();
                    writer.close();
                    reader.close();
                }
            } catch (IOException e) {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                    if (writer != null) {
                        writer.close();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 初始化界面
     */
    private void initViews() {
        // 配置启动界面的渐变效果
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
        Integer delay = getResources().getInteger(R.integer.splashAnimationDelay);
        alphaAnimation.setDuration(delay);
        RelativeLayout rl_main = (RelativeLayout)findViewById(R.id.rl_main);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationPlayOver = true;

                // 动画播放完毕后进入主UI
                loadMainUI();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rl_main.startAnimation(alphaAnimation);
    }

    /**
     * 进入到主界面中
     */
    private void loadMainUI () {
        // 动画播放和文件拷贝分属两个线程，仅当二者同时完毕时，才可进入主UI
        if (copyAssertFileOk && animationPlayOver) {
            MainActivity.start(this);
            finish();
        }
    }


    private enum XML_STATE {FIND_ITEM, FIND_PATH}
}
