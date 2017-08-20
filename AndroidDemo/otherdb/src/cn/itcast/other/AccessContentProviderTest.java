package cn.itcast.other;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

public class AccessContentProviderTest extends AndroidTestCase {
	private static final String TAG = "AccessContentProviderTest";
	
	/**
	 * 往内容提供者添加数据
	 * @throws Throwable
	 */
	public void testInsert() throws Throwable{
		ContentResolver contentResolver = this.getContext().getContentResolver();
		Uri insertUri = Uri.parse("content://cn.itcast.providers.personprovider/person");
		ContentValues values = new ContentValues();
		values.put("name", "zhangxiaoxiao");
		values.put("amount", 90);
		Uri uri = contentResolver.insert(insertUri, values);
		Log.i(TAG, uri.toString());
	}

	/**
	 * 更新内容提供者中的数据
	 * @throws Throwable
	 */
	public void testUpdate() throws Throwable{
		ContentResolver contentResolver = this.getContext().getContentResolver();
		Uri updateUri = Uri.parse("content://cn.itcast.providers.personprovider/person/1");
		ContentValues values = new ContentValues();
		values.put("name", "lili");
		contentResolver.update(updateUri, values, null, null);
	}
	
	/**
	 * 从内容提供者中删除数据
	 * @throws Throwable
	 */
	public void testDelete() throws Throwable{
		ContentResolver contentResolver = this.getContext().getContentResolver();
		Uri deleteUri = Uri.parse("content://cn.itcast.providers.personprovider/person/1");
		contentResolver.delete(deleteUri, null, null);
	}
	
	/**
	 * 获取内容提供者中的数据
	 * @throws Throwable
	 */
	public void testFind() throws Throwable{
		ContentResolver contentResolver = this.getContext().getContentResolver();
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
