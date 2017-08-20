package cn.itcast.file;

import cn.itcast.service.FileService;
import android.test.AndroidTestCase;
import android.util.Log;

public class FileServiceTest extends AndroidTestCase {
	private static final String TAG = "FileServiceTest";

	public void testSave() throws Throwable{
		FileService service = new FileService(this.getContext());
		service.save("itcast.txt", "www.itcast.cn");
	}
	
	public void testReadFile() throws Throwable{
		FileService service = new FileService(this.getContext());
		String result = service.readFile("www.txt");
		Log.i(TAG, result);
	}
	
	public void testSaveAppend() throws Throwable{
		FileService service = new FileService(this.getContext());
		service.saveAppend("append.txt", ",www.csdn.cn");
	}
	
	public void testSaveReadable() throws Throwable{
		FileService service = new FileService(this.getContext());
		service.saveReadable("readable.txt", "www.sohu.com");
	}
	
	public void testSaveWriteable() throws Throwable{
		FileService service = new FileService(this.getContext());
		service.saveWriteable("writeable.txt", "www.sina.com.cn");
	}
	
	public void testSaveRW() throws Throwable{
		FileService service = new FileService(this.getContext());
		service.saveRW("rw.txt", "www.joyo.com");
	}
}
