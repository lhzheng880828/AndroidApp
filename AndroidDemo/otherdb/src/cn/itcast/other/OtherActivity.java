package cn.itcast.other;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class OtherActivity extends Activity {
    private static final String TAG = "OtherActivity";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Uri insertUri = Uri.parse("content://cn.itcast.providers.personprovider/person");
        ContentResolver contentResolver = this.getContentResolver();
        //对指定uri进行监听，如果该uri代表的数据发生变化，就会调用PersonObserver中的onChange()
        contentResolver.registerContentObserver(insertUri, true, new PersonObserver(new Handler()));
    }
    
    private final class PersonObserver extends ContentObserver{
		public PersonObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			ContentResolver contentResolver = getContentResolver();
			Uri selectUri = Uri.parse("content://cn.itcast.providers.personprovider/person");
			Cursor cursor = contentResolver.query(selectUri, null, null, null, "personid desc");
			while(cursor.moveToNext()){
				int id = cursor.getInt(cursor.getColumnIndex("personid"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				int amount = cursor.getInt(cursor.getColumnIndex("amount"));
				Log.i(TAG, "id="+ id + ",name="+ name+ ",amount="+ amount);
			}
		}
    	
    }

}