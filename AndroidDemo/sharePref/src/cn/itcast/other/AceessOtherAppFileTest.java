package cn.itcast.other;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.test.AndroidTestCase;
import android.util.Log;

public class AceessOtherAppFileTest extends AndroidTestCase {
	private static final String TAG = "AceessOtherAppFileTest";
	//文件没有发现
	public void testAccessOtherAppFile() throws Throwable{
		String path = "/data/data/cn.itcast.file/files/www.txt";
		File file = new File(path);
		FileInputStream inStream = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while( (len = inStream.read(buffer))!= -1){
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();//得到文件的二进制数据
		outStream.close();
		inStream.close();
		Log.i(TAG, new String(data));
	}
	
	
	public void testAccessOtherAppReadable() throws Throwable{
		String path = "/data/data/cn.itcast.file/files/readable.txt";
		File file = new File(path);
		FileInputStream inStream = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while( (len = inStream.read(buffer))!= -1){
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();//得到文件的二进制数据
		outStream.close();
		inStream.close();
		Log.i(TAG, new String(data));
	}
	
	public void testWriteOtherAppReadable() throws Throwable{
		String path = "/data/data/cn.itcast.file/files/readable.txt";
		File file = new File(path);
		FileOutputStream outStream = new FileOutputStream(file);
		outStream.write("xxxx".getBytes());
		outStream.close();
	}
	
	public void testWriteOtherAppWriteable() throws Throwable{
		String path = "/data/data/cn.itcast.file/files/writeable.txt";
		File file = new File(path);
		FileOutputStream outStream = new FileOutputStream(file);
		outStream.write("liming".getBytes());
		outStream.close();
	}
	
	public void testAccessOtherAppWriteable() throws Throwable{
		String path = "/data/data/cn.itcast.file/files/writeable.txt";
		File file = new File(path);
		FileInputStream inStream = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while( (len = inStream.read(buffer))!= -1){
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();//得到文件的二进制数据
		outStream.close();
		inStream.close();
		Log.i(TAG, new String(data));
	}
	
	public void testWriteOtherAppRW() throws Throwable{
		String path = "/data/data/cn.itcast.file/files/rw.txt";
		File file = new File(path);
		FileOutputStream outStream = new FileOutputStream(file);
		outStream.write("liming".getBytes());
		outStream.close();
	}
	
	public void testAccessOtherAppRW() throws Throwable{
		String path = "/data/data/cn.itcast.file/files/rw.txt";
		File file = new File(path);
		FileInputStream inStream = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while( (len = inStream.read(buffer))!= -1){
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();//得到文件的二进制数据
		outStream.close();
		inStream.close();
		Log.i(TAG, new String(data));
	}
}
