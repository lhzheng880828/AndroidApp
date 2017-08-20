package cn.itcast.html;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.itcast.domain.Contact;
import cn.itcast.service.ContactService;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
    private ContactService contactService;
    private WebView webView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        contactService = new ContactService();
        webView = (WebView)this.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new ContactPlugin(), "contact");
        //webView.loadUrl("file:///android_asset/index.html");
        webView.loadUrl("http://192.168.1.100:8080/videoweb/index.html");
    }
    
    private final class ContactPlugin{
    	public void getContacts(){
    		List<Contact> contacts = contactService.getContacts();
    		try {
				JSONArray array = new JSONArray();
				for(Contact contact : contacts){
					JSONObject item = new JSONObject();
					item.put("id", contact.getId());
					item.put("name", contact.getName());
					item.put("mobile", contact.getMobile());
					array.put(item);
				}
				String json = array.toString();
				Log.i(TAG, json);
				webView.loadUrl("javascript:show('"+ json +"')");
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
    	
    	public void call(String mobile){
    		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ mobile));
    		startActivity(intent);
    	}
    }
}