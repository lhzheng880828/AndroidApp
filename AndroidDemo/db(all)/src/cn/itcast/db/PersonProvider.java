package cn.itcast.db;

import cn.itcast.service.DBOpenHelper;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PersonProvider extends ContentProvider {
	private DBOpenHelper dbOpenHelper;
	private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	private static final int PERSONS = 1;
	private static final int PERSON = 2;
	static{
		MATCHER.addURI("cn.itcast.providers.personprovider", "person", PERSONS);
		MATCHER.addURI("cn.itcast.providers.personprovider", "person/#", PERSON);
	}	
	//删除person表中的所有记录   /person
	//删除person表中指定id的记录 /person/10
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int count = 0;
		switch (MATCHER.match(uri)) {
		case PERSONS:
			count = db.delete("person", selection, selectionArgs);
			return count;
			
		case PERSON:
			long id = ContentUris.parseId(uri);
			String where = "personid="+ id;
			if(selection!=null && !"".equals(selection)){
				where = selection + " and " + where;
			}
			count = db.delete("person", where, selectionArgs);
			return count;
			
		default:
			throw new IllegalArgumentException("Unkwon Uri:"+ uri.toString());
		}
	}

	@Override
	public String getType(Uri uri) {//返回当前操作的数据的mimeType
		switch (MATCHER.match(uri)) {
		case PERSONS:			
			return "vnd.android.cursor.dir/person";
			
		case PERSON:			
			return "vnd.android.cursor.item/person";
			
		default:
			throw new IllegalArgumentException("Unkwon Uri:"+ uri.toString());
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {// /person
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		switch (MATCHER.match(uri)) {
		case PERSONS:
			long rowid = db.insert("person", "name", values); 
			Uri insertUri = ContentUris.withAppendedId(uri, rowid);//得到代表新增记录的Uri
			this.getContext().getContentResolver().notifyChange(uri, null);
			return insertUri;

		default:
			throw new IllegalArgumentException("Unkwon Uri:"+ uri.toString());
		}
	}

	@Override
	public boolean onCreate() {
		this.dbOpenHelper = new DBOpenHelper(this.getContext());
		return false;
	}
	//查询person表中的所有记录   /person
	//查询person表中指定id的记录 /person/10
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		switch (MATCHER.match(uri)) {
		case PERSONS:
			return db.query("person", projection, selection, selectionArgs, null, null, sortOrder);
			
		case PERSON:
			long id = ContentUris.parseId(uri);
			String where = "personid="+ id;
			if(selection!=null && !"".equals(selection)){
				where = selection + " and " + where;
			}
			return db.query("person", projection, where, selectionArgs, null, null, sortOrder);
			
		default:
			throw new IllegalArgumentException("Unkwon Uri:"+ uri.toString());
		}
	}
	
	//更新person表中的所有记录   /person
	//更新person表中指定id的记录 /person/10
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int count = 0;
		switch (MATCHER.match(uri)) {
		case PERSONS:
			count = db.update("person", values, selection, selectionArgs);
			return count;
			
		case PERSON:
			long id = ContentUris.parseId(uri);
			String where = "personid="+ id;
			if(selection!=null && !"".equals(selection)){
				where = selection + " and " + where;
			}
			count = db.update("person", values, where, selectionArgs);
			return count;
			
		default:
			throw new IllegalArgumentException("Unkwon Uri:"+ uri.toString());
		}
	}

}
