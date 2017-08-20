package cn.itcast.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.itcast.utils.StreamTool;

public class ImageService {
	
	public static byte[] getImage(String path) throws Exception {
		URL url = new URL("http://i3.itc.cn/20100707/76c_0969b700_d5b4_41cd_8243_9b486be92cc4_0.jpg");
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
		return StreamTool.readInputStream(inStream);//得到图片的二进制数据
	}

}
