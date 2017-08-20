package cn.itcast.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.itcast.domain.Person;

public class OtherPersonService {
	private DBOpenHelper dbOpenHelper;
	
	public OtherPersonService(Context context) {
		this.dbOpenHelper = new DBOpenHelper(context);
	}

	public void save(Person person){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", person.getName());
		db.insert("person", null, values);
	}
	
	public void update(Person person){
		// update person set name =? where personid =?
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", person.getName());
		db.update("person", values, "personid=?", new String[]{person.getId().toString()});
	}
	
	public void delete(Integer id){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.delete("person", "personid=?", new String[]{id.toString()});
	}
	
	public Person find(Integer id){
		//如果只对数据进行读取，建议使用此方法
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("person", new String[]{"personid", "name"},
				"personid=?", new String[]{id.toString()}, null, null, null);
		//select personid,name from person where personid=? order by ... limit 3,5
		if(cursor.moveToFirst()){
			int personid = cursor.getInt(cursor.getColumnIndex("personid"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			return new Person(personid, name);
		}
		return null;
	}

	public List<Person> getScrollData(Integer offset, Integer maxResult){
		List<Person> persons = new ArrayList<Person>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("person", null, null, null, null, null, null, offset+","+ maxResult);
		while(cursor.moveToNext()){
			int personid = cursor.getInt(cursor.getColumnIndex("personid"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			Person person = new Person(personid, name);
			persons.add(person);
		}
		cursor.close();
		return persons;
	}

	public long getCount() {// select count(*) from person
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("person", new String[]{"count(*)"}, null, null, null, null, null);
		cursor.moveToFirst();
		return cursor.getLong(0);
	}
}
