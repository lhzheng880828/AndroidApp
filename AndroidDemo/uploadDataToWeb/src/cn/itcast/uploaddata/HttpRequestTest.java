package cn.itcast.uploaddata;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import cn.itcast.net.HttpRequest;
import android.test.AndroidTestCase;
import android.util.Log;

public class HttpRequestTest extends AndroidTestCase {
	private static final String TAG = "HttpRequestTest";
	
	public void testSendXMLRequest() throws Throwable{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><persons><person id=\"23\"><name>liming</name><age>30</age></person></persons>";
		HttpRequest.sendXML("http://192.168.1.100:8080/videoweb/video/manage.do?method=getXML", xml);
	}

	public void testSendGetRequest() throws Throwable{
		//?method=save&title=xxxx&timelength=90
		Map<String, String> params = new HashMap<String, String>();
		params.put("method", "save");
		params.put("title", "liming");
		params.put("timelength", "80");
		
		HttpRequest.sendGetRequest("http://192.168.1.100:8080/videoweb/video/manage.do", params, "UTF-8");
	}
	
	public void testSendPostRequest() throws Throwable{
		Map<String, String> params = new HashMap<String, String>();
		params.put("method", "save");
		params.put("title", "中国");
		params.put("timelength", "80");
		
		HttpRequest.sendPostRequest("http://192.168.1.100:8080/videoweb/video/manage.do", params, "UTF-8");
	}
	
	public void testSendRequestFromHttpClient() throws Throwable{
		Map<String, String> params = new HashMap<String, String>();
		params.put("method", "save");
		params.put("title", "传智播客");
		params.put("timelength", "80");
		
		boolean result = HttpRequest.sendRequestFromHttpClient("http://192.168.1.100:8080/videoweb/video/manage.do", params, "UTF-8");
		Log.i("HttRequestTest", ""+ result);
	}
}
