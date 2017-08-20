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
	 * ��ȡ���µ���Ƶ��Ѷ
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
	 * �������������ص�Э�飬�õ���Ƶ��Ѷ
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	private static List<Video> parseXML(InputStream inStream) throws Exception{
		List<Video> videos = null;
		Video video = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inStream, "UTF-8");
		int eventType = parser.getEventType();//������һ���¼�
		while(eventType!=XmlPullParser.END_DOCUMENT){//ֻҪ�����ĵ������¼�
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				videos = new ArrayList<Video>();
				break;
	
			case XmlPullParser.START_TAG:
				String name = parser.getName();//��ȡ��������ǰָ���Ԫ�ص�����
				if("video".equals(name)){
					video = new Video();
					video.setId(new Integer(parser.getAttributeValue(0)));
				}
				if(video!=null){
					if("title".equals(name)){
						video.setTitle(parser.nextText());//��ȡ��������ǰָ��Ԫ�ص���һ���ı��ڵ��ֵ
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
