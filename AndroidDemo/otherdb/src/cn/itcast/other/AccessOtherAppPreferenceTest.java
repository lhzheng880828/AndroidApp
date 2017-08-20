package cn.itcast.other;


import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;
import android.util.Log;

public class AccessOtherAppPreferenceTest extends AndroidTestCase {
	private static final String TAG = "AccessOtherAppPreferenceTest";
	
	public void testAccessOtherAppPreference() throws Throwable{
		//String path = "/data/data/cn.itcast.preferences/shared_prefs/itcast.xml";
		//File file = new File(path);
		// SAX来完成xml文件的解析，才能得到参数
		
		Context otherContext = getContext().createPackageContext("cn.itcast.preferences",
				Context.CONTEXT_IGNORE_SECURITY);
		SharedPreferences preferences = otherContext.getSharedPreferences("itcast", Context.MODE_PRIVATE);
		String name = preferences.getString("name", "");
		int age = preferences.getInt("age", 20);
		Log.i(TAG, "name="+ name + ",age="+ age);
	}

}
