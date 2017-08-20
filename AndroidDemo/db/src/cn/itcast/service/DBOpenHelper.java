package cn.itcast.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String DATABASENAME = "itcast.db"; //数据库名称
	private static final int DATABASEVERSION = 2;//数据库版本

	public DBOpenHelper(Context context) {
		super(context, DATABASENAME, null, DATABASEVERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE person (personid integer primary key autoincrement, name varchar(20), amount integer)");//执行有更改的sql语句
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS person");
		onCreate(db);
	}

}
