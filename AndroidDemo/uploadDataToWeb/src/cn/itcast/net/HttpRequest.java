package cn.itcast.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import cn.itcast.utils.StreamTool;

public class HttpRequest {

	
	public static boolean sendXML(String path, String xml)throws Exception{
		byte[] data = xml.getBytes();
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(5 * 1000);
		conn.setDoOutput(true);//���ͨ��post�ύ���ݣ�����������������������
		conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));
		OutputStream outStream = conn.getOutputStream();
		outStream.write(data);
		outStream.flush();
		outStream.close();
		if(conn.getResponseCode()==200){
			return true;
		}
		return false;
	}

	public static boolean sendGetRequest(String path, Map<String, String> params, String enc) throws Exception{
		StringBuilder sb = new StringBuilder(path);
		sb.append('?');
		// ?method=save&title=435435435&timelength=89&
		for(Map.Entry<String, String> entry : params.entrySet()){
			sb.append(entry.getKey()).append('=')
				.append(URLEncoder.encode(entry.getValue(), enc)).append('&');
		}
		sb.deleteCharAt(sb.length()-1);
		
		URL url = new URL(sb.toString());
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		if(conn.getResponseCode()==200){
			return true;
		}
		return false;
	}
	
	public static boolean sendPostRequest(String path, Map<String, String> params, String enc) throws Exception{
		// title=dsfdsf&timelength=23&method=save
		StringBuilder sb = new StringBuilder();
		if(params!=null && !params.isEmpty()){
			for(Map.Entry<String, String> entry : params.entrySet()){
				sb.append(entry.getKey()).append('=')
					.append(URLEncoder.encode(entry.getValue(), enc)).append('&');
			}
			sb.deleteCharAt(sb.length()-1);
		}
		byte[] entitydata = sb.toString().getBytes();//�õ�ʵ��Ķ���������
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(5 * 1000);
		conn.setDoOutput(true);//���ͨ��post�ύ���ݣ�����������������������
		//Content-Type: application/x-www-form-urlencoded
		//Content-Length: 38
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(entitydata.length));
		OutputStream outStream = conn.getOutputStream();
		outStream.write(entitydata);
		outStream.flush();
		outStream.close();
		if(conn.getResponseCode()==200){
			return true;
		}
		return false;
	}
	
	//SSL HTTPS Cookie
	public static boolean sendRequestFromHttpClient(String path, Map<String, String> params, String enc) throws Exception{
		List<NameValuePair> paramPairs = new ArrayList<NameValuePair>();
		if(params!=null && !params.isEmpty()){
			for(Map.Entry<String, String> entry : params.entrySet()){
				paramPairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		UrlEncodedFormEntity entitydata = new UrlEncodedFormEntity(paramPairs, enc);//�õ�������������ʵ������
		HttpPost post = new HttpPost(path); //form
		post.setEntity(entitydata);
		DefaultHttpClient client = new DefaultHttpClient(); //�����
		HttpResponse response = client.execute(post);//ִ������
		if(response.getStatusLine().getStatusCode()==200){
			return true;
		}
		return false;
	}
}
