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
		InputStream inStream = conn.getInputStream();//通过输入流获取html数据
		byte[] data = StreamTool.readInputStream(inStream);//得到html的二进制数据
		String html = new String(data, "gb2312");
		return html;
	}

}
