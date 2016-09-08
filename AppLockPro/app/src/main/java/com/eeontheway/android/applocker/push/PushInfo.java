package com.eeontheway.android.applocker.push;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 推送消息
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class PushInfo {
    private Object object;

    /**
     * 将消息转换为字符串
     * @return 转化生成的字符串
     */
    public String toString () {
        if (object != null) {
            return ((JSONObject) object).toString();
        } else {
            return "";
        }
    }

    /**
     * 创建一个JSON形式的反馈字符串
     * @param id topic的ID
     * @param title 反馈标题
     * @param content 反馈内容
     */
    public void createJSONFeedBack (String id, String title, String content) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "feedback");

            JSONObject payloadObject = new JSONObject();
            payloadObject.put("id", id);
            payloadObject.put("title", title);
            payloadObject.put("content", content);

            jsonObject.put("payload", payloadObject);

            object = jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
