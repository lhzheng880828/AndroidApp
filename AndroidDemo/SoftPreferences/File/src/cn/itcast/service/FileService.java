package cn.itcast.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.os.Environment;

public class FileService {
	private Context context;
	
	public FileService(Context context) {
		this.context = context;
	}
	/**
	 * 以私有文件保存内容
	 * @param filename 文件名称
	 * @param content 文件内容
	 * @throws Exception
	 */
	public void saveToSDCard(String filename, String content) throws Exception{
		File file = new File(Environment.getExternalStorageDirectory(), filename);
		FileOutputStream outStream = new FileOutputStream(file);
		outStream.write(content.getBytes());
		outStream.close();
	}
	
	/**
	 * 以私有文件保存内容
	 * @param filename 文件名称
	 * @param content 文件内容
	 * @throws Exception
	 */
	public void save(String filename, String content) throws Exception{
		FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
		outStream.write(content.getBytes());
		outStream.close();
	}
	/**
	 * 以追加方式保存内容
	 * @param filename 文件名称
	 * @param content 文件内容
	 * @throws Exception
	 */
	public void saveAppend(String filename, String content) throws Exception{// ctrl+shift+y / x
		FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_APPEND);
		outStream.write(content.getBytes());
		outStream.close();
	}
	/**
	 * 保存内容,注：允许其他应用从该文件中读取内容
	 * @param filename 文件名称
	 * @param content 文件内容
	 * @throws Exception
	 */
	public void saveReadable(String filename, String content) throws Exception{
		FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_WORLD_READABLE);
		outStream.write(content.getBytes());
		outStream.close();
	}
	/**
	 * 保存内容,注：允许其他应用往该文件写入内容
	 * @param filename 文件名称
	 * @param content 文件内容
	 * @throws Exception
	 */
	public void saveWriteable(String filename, String content) throws Exception{
		FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_WORLD_WRITEABLE);
		outStream.write(content.getBytes());
		outStream.close();
	}
	/**
	 * 保存内容,注：允许其他应用对该文件读和写
	 * @param filename 文件名称
	 * @param content 文件内容
	 * @throws Exception
	 */
	public void saveRW(String filename, String content) throws Exception{
		FileOutputStream outStream = context.openFileOutput(filename, 
				Context.MODE_WORLD_READABLE+ Context.MODE_WORLD_WRITEABLE);
		outStream.write(content.getBytes());
		outStream.close();
	}
	/**
	 * 读取文件内容
	 * @param filename 文件名称
	 * @return
	 * @throws Exception
	 */
	public String readFile(String filename) throws Exception{
		FileInputStream inStream = context.openFileInput(filename);
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while( (len = inStream.read(buffer))!= -1){
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();//得到文件的二进制数据
		outStream.close();
		inStream.close();
		return new String(data);
	}
}
