package cn.itcast.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import cn.itcast.domain.Person;
import cn.itcast.service.DOMPersonService;
import cn.itcast.service.PULLPersonService;
import cn.itcast.service.SAXPersonService;
import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

public class PersonServiceTest extends AndroidTestCase {
	private static final String TAG = "PersonServiceTest";

	public void testSAXGetPersons() throws Throwable{
		SAXPersonService service = new SAXPersonService();
		InputStream inStream = getClass().getClassLoader().getResourceAsStream("itcast.xml");
		List<Person> persons = service.getPersons(inStream);
		for(Person person : persons){
			Log.i(TAG, person.toString());    
		}
	}
	
	public void testDomGetPersons() throws Throwable{
		InputStream inStream = getClass().getClassLoader().getResourceAsStream("itcast.xml");
		List<Person> persons = DOMPersonService.getPersons(inStream);
		for(Person person : persons){
			Log.i(TAG, person.toString());
		}
	}
	
	public void testPullGetPersons() throws Throwable{
		InputStream inStream = getClass().getClassLoader().getResourceAsStream("itcast.xml");
		List<Person> persons = PULLPersonService.getPersons(inStream);
		for(Person person : persons){
			Log.i(TAG, person.toString());
		}
	}
	
	public void testSave() throws Throwable{
		//File file = new File(this.getContext().getFilesDir(), "person.xml");
		//FileOutputStream outStream = new FileOutputStream(file);
		List<Person> persons = new ArrayList<Person>();
		persons.add(new Person(34, "lili", (short)12));
		persons.add(new Person(56, "¿œ±œ", (short)32));
		persons.add(new Person(39, "¿œ’≈", (short)40));
		/*FileOutputStream outStream = this.getContext().openFileOutput("person.xml", Context.MODE_PRIVATE);
		OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
		BufferedWriter bWriter = new BufferedWriter(writer);
		*/
		StringWriter writer = new StringWriter();
		PULLPersonService.save(persons, writer);
		Log.i(TAG, writer.toString());
	}
}
