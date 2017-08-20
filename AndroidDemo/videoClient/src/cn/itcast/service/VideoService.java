package cn.itcast.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import cn.itcast.domain.Video;
import cn.itcast.utils.StreamTool;

public class VideoService {
	/**
	 * 获取最新的视频资讯
	 * @return
	 * @throws Exception
	 */
	public static List<Video> getLastVideos() throws Exception{
		String path = "http://192.168.1.100:8080/videoweb/video/list.do";
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setReadTimeout(5*1000);
		conn.setRequestMethod("GET");
		InputStream inStream = conn.getInputStream();
		return parseXML(inStream);
	}
	
	public static List<Video> getJSONLastVideos() throws Exception{
		List<Video> videos = new ArrayList<Video>();
		String path = "http://192.168.1.100:8080/videoweb/video/list.do?format=json";
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setReadTimeout(5*1000);
		conn.setRequestMethod("GET");
		InputStream inStream = conn.getInputStream();
		byte[] data = StreamTool.readInputStream(inStream);
		String json = new String(data);
		JSONArray array = new JSONArray(json);
		for(int i=0 ; i < array.length() ; i++){
			JSONObject item = array.getJSONObject(i);
			int id = item.getInt("id");
			String title = item.getString("title");
			int timelength = item.getInt("timelength");
			videos.add(new Video(id, title, timelength));
		}
		return videos;
	}
	/**
	 * 解析服务器返回的协议，得到视频资讯
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	private static List<Video> parseXML(InputStream inStream) throws Exception{
		List<Video> videos = null;
		Video video = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inStream, "UTF-8");
		int eventType = parser.getEventType();//产生第一个事件
		while(eventType!=XmlPullParser.END_DOCUMENT){//只要不是文档结束事件
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				videos = new ArrayList<Video>();
				break;
	
			case XmlPullParser.START_TAG:
				String name = parser.getName();//获取解析器当前指向的元素的名称
				if("video".equals(name)){
					video = new Video();
					video.setId(new Integer(parser.getAttributeValue(0)));
				}
				if(video!=null){
					if("title".equals(name)){
						video.setTitle(parser.nextText());//获取解析器当前指向元素的下一个文本节点的值
					}
					if("timelength".equals(name)){
						video.setTime(new Integer(parser.nextText()));
					}
				}
				break;
				
			case XmlPullParser.END_TAG:
				if("video".equals(parser.getName())){
					videos.add(video);
					video = null;
				}
				break;
			}
			eventType = parser.next();
		}
		return videos;
	}
}
