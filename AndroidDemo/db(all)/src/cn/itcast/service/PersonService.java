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

	public void payment(){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.beginTransaction();//事启事务
		try{
			db.execSQL("update person set amount=amount-10 where personid=?", new Object[]{1});
			db.execSQL("update person set amount=amount+10 where personid=?", new Object[]{2});
			db.setTransactionSuccessful();//设置事务标志为成功，当结束事务时就会提交事务
		}finally{
			db.endTransaction();
		}
	}
	
	public void save(Person person){
		//如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("insert into person (name,amount) values(?,?)",
				new Object[]{person.getName(),person.getAmount()});
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
			int amount = cursor.getInt(cursor.getColumnIndex("amount"));
			Person person = new Person(personid, name);
			person.setAmount(amount);
			return person;
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
			int amount = cursor.getInt(cursor.getColumnIndex("amount"));
			Person person = new Person(personid, name);
			person.setAmount(amount);
			persons.add(person);
		}
		cursor.close();
		return persons;
	}
	
	public Cursor getCursorScrollData(Integer offset, Integer maxResult){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		return db.rawQuery("select personid as _id, name, amount from person limit ?,?",
				new String[]{offset.toString(), maxResult.toString()});
	}
	
	public long getCount() {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from person", null);
		cursor.moveToFirst();
		return cursor.getLong(0);
	}
}
