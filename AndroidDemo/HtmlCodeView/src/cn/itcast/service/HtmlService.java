package cn.itcast.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.itcast.utils.StreamTool;

public class HtmlService {

	public static String getHtml(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		InputStream inStream = conn.getInputStream();//ͨ����������ȡhtml����
		byte[] data = StreamTool.readInputStream(inStream);//�õ�html�Ķ���������
		String html = new String(data, "gb2312");
		return html;
	}

}
