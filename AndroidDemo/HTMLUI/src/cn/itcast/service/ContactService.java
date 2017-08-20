package cn.itcast.service;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.domain.Contact;

public class ContactService {

	public List<Contact> getContacts(){
		List<Contact> contacts = new ArrayList<Contact>();
		contacts.add(new Contact(34, "ÕÅĞ¢Ïé", "1398320333"));
		contacts.add(new Contact(39, "·ëÍş", "1332432444"));
		contacts.add(new Contact(67, "ÀÏ±Ï", "1300320333"));
		return contacts;
	}
}
