package com.eeontheway.android.applocker.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.feedback.FeedBackInfoActivity;
import com.eeontheway.android.applocker.feedback.FeedBackListActivity;
import com.eeontheway.android.applocker.utils.Configuration;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 推送消息处理器
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class PushMessageProcessor {
    public static final String KEY_TYPE = "type";

    public static final String TYPE_FEEDBACK = "feedback";
    public static final String KEY_PAYLOAD = "payload";
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";

    private Context context;

    /**
     * 构造函数
     * @param context 上下文
     */
    public PushMessageProcessor(Context context) {
        this.context = context;
    }

    /**
     * 消息处理器
     * @param payload 消息负载
     *
     * 消息示例
     * {
     ×    "type": "feedback",
     *    "payload": {
     *        "id": "idxxx"
     *        "title": "关于XXX问题的回复",
     *        "content": "具体来说，回复如下...."
     *    }
     * }
     */
    public void processMessage (byte [] payload) {
        try {
            JSONObject jsonObject = new JSONObject(new String(payload));
            switch (jsonObject.getString(KEY_TYPE)) {
                case TYPE_FEEDBACK:    // 收到反馈消息
                    // 解析出标题和内容
                    JSONObject feedbakcInfo = jsonObject.getJSONObject(KEY_PAYLOAD);
                    String id = feedbakcInfo.getString(KEY_ID);
                    String title = feedbakcInfo.getString(KEY_TITLE);
                    String content = feedbakcInfo.getString(KEY_CONTENT);

                    // 显示反馈信息
                    showFeedBackMessage(id, title, content);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(Configuration.LOG_TAG, "Json Error: " + e.getMessage());
        }
    }

    /**
     * 处理并显示反馈推送消息
     */
    public void showFeedBackMessage (String id, String title, String content) {
        // 创建Notify点击时跳转目标
        Intent notifyIntent = new Intent(context, FeedBackInfoActivity.class);
        notifyIntent.putExtra(KEY_ID, id);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                                                context,
                                                0,
                                                notifyIntent,
                                                PendingIntent.FLAG_UPDATE_CURRENT
                                            );

        // 生成Notify并显示
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.main_logo_small)
                .setContentTitle(title)
                .setContentText(content)
                .setTicker(context.getString(R.string.recv_feedback))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(notifyPendingIntent)
                .build();
        NotificationManager manager = (NotificationManager)context.getSystemService(
                                                            Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }
}
