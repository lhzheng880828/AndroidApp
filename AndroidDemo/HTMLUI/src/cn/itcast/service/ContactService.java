package cn.itcast.service;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.domain.Contact;

public class ContactService {

	public List<Contact> getContacts(){
		List<Contact> contacts = new ArrayList<Contact>();
		contacts.add(new Contact(34, "��Т��", "1398320333"));
		contacts.add(new Contact(39, "����", "1332432444"));
		contacts.add(new Contact(67, "�ϱ�", "1300320333"));
		return contacts;
	}
}
