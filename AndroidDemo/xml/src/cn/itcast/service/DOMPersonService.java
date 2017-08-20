package cn.itcast.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.itcast.domain.Person;
/**
 * 采用DOM解析XML内容
 */
public class DOMPersonService {

	public static List<Person> getPersons(InputStream inStream) throws Throwable{
		List<Person> persons = new ArrayList<Person>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document documnet = builder.parse(inStream);
		Element root = documnet.getDocumentElement();
		NodeList personNodes = root.getElementsByTagName("person");
		for(int i=0 ; i < personNodes.getLength(); i++){
			Person person = new Person();
			Element personElement = (Element)personNodes.item(i);
			person.setId(new Integer(personElement.getAttribute("id")));
			NodeList personChilds = personElement.getChildNodes();
			for(int y=0 ; y < personChilds.getLength(); y++){
				if(personChilds.item(y).getNodeType()==Node.ELEMENT_NODE){//判断当前节点是否是元素类型节点
					Element childElement = (Element)personChilds.item(y);
					if("name".equals(childElement.getNodeName())){
						person.setName(childElement.getFirstChild().getNodeValue());
					}else if("age".equals(childElement.getNodeName())){
						person.setAge(new Short(childElement.getFirstChild().getNodeValue()));
					}
				}
			}
			persons.add(person);
		}
		return persons;
	}
}
