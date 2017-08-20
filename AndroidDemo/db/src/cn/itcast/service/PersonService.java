package cn.itcast.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.itcast.domain.Person;

public class PersonService {
	private DBOpenHelper dbOpenHelper;
	
	public PersonService(Context context) {
		this.dbOpenHelper = new DBOpenHelper(context);
	}

	public void save(Person person){
		//如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("insert into person (name) values(?)", new Object[]{person.getName()});
	}
	
	public void update(Person person){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("update person set name=? where personid=?", 
				new Object[]{person.getName(),person.getId()});
	}
	
	public void delete(Integer id){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from person where personid=?", new Object[]{id.toString()});
	}
	
	public Person find(Integer id){
		//如果只对数据进行读取，建议使用此方法
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from person where personid=?", new String[]{id.toString()});
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
		Cursor cursor = db.rawQuery("select * from person limit ?,?",
				new String[]{offset.toString(), maxResult.toString()});
		while(cursor.moveToNext()){
			int personid = cursor.getInt(cursor.getColumnIndex("personid"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			Person person = new Person(personid, name);
			persons.add(person);
		}
		cursor.close();
		return persons;
	}
	
	public long getCount() {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from person", null);
		cursor.moveToFirst();
		return cursor.getLong(0);
	}
}
