package com.eeontheway.android.applocker.push;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 个推服务
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class GetTuiPush implements IPush {
    private static final String appId = "DIB5qDDshd8SnmEUcDGaU";
    private static final String appkey = "6a8QobLFBg75RSzDoAaBp3";
    private static final String MASTERSECRET = "GWUoVVG4Ub6EoFAxpiRTk1";

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void pushMsg(PushInfo info, String clientId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("action", "pushmessage"); // pushmessage为接口名，注意全部小写
                /*---以下代码用于设定接口相应参数---*/
        param.put("appkey", appkey);
        param.put("appid", appId);
        // 注：透传内容后面需用来验证接口调用是否成功，假定填写为hello girl~
        param.put("data", info.toString());

        Date curDate = new Date(System.currentTimeMillis());
        param.put("time", formatter.format(curDate)); // 当前请求时间，可选
        param.put("clientid", clientId); // 您获取的ClientID
        param.put("expire", 3600); // 消息超时时间，单位为秒，可选

        // 生成Sign值，用于鉴权
        param.put("sign", GetuiSdkHttpPost.makeSign(MASTERSECRET, param));

        GetuiSdkHttpPost.httpPost(param);
    }
}
