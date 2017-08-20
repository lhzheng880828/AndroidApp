package cn.itcast.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import cn.itcast.domain.Person;
/**
 * ����Pull����XML����
 */
public class PULLPersonService {
	
	public static void save(List<Person> persons, Writer writer) throws Throwable{
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		
		serializer.startTag(null, "persons");
		for(Person person : persons){
			serializer.startTag(null, "person");
			serializer.attribute(null, "id", person.getId().toString());
			
			serializer.startTag(null, "name");
			serializer.text(person.getName());
			serializer.endTag(null, "name");
			
			serializer.startTag(null, "age");
			serializer.text(person.getAge().toString());
			serializer.endTag(null, "age");
			
			serializer.endTag(null, "person");
		}
		serializer.endTag(null, "persons");		
		serializer.endDocument();
		writer.flush();
		writer.close();
	}

	public static List<Person> getPersons(InputStream inStream) throws Throwable{
		List<Person> persons = null;
		Person person = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inStream, "UTF-8");
		int eventType = parser.getEventType();//������һ���¼�
		while(eventType!=XmlPullParser.END_DOCUMENT){//ֻҪ�����ĵ������¼�
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				persons = new ArrayList<Person>();
				break;
	
			case XmlPullParser.START_TAG:
				String name = parser.getName();//��ȡ��������ǰָ���Ԫ�ص�����
				if("person".equals(name)){
					person = new Person();
					person.setId(new Integer(parser.getAttributeValue(0)));
				}
				if(person!=null){
					if("name".equals(name)){
						person.setName(parser.nextText());//��ȡ��������ǰָ��Ԫ�ص���һ���ı��ڵ��ֵ
					}
					if("age".equals(name)){
						person.setAge(new Short(parser.nextText()));
					}
				}
				break;
				
			case XmlPullParser.END_TAG:
				if("person".equals(parser.getName())){
					persons.add(person);
					person = null;
				}
				break;
			}
			eventType = parser.next();
		}
		return persons;
	}
}
