package cn.itcast.test;

import junit.framework.Assert;
import cn.itcast.service.PersonService;
import android.test.AndroidTestCase;
import android.util.Log;

public class PersonServiceTest extends AndroidTestCase {
	private static final String TAG = "PersonServiceTest";

	public void testSave() throws Throwable{
		PersonService service = new PersonService();
		int b = service.save();//检验save()方法运行是否正常
		//Log.i(TAG, "result="+ b);
		//System.out.println();
		//System.err.println("result="+ b);
		Assert.assertEquals(78, b);
	}
}
