package cn.itcast.utils;

import java.util.Properties;
/**
 * ��ȡ�����ļ�
 *
 */
public class ConfigInfo {
	private static Properties cache = new Properties();
	static{
		try {
			cache.load(ConfigInfo.class.getClassLoader().getResourceAsStream("merchantInfo.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * ��ȡָ��key��ֵ
	 * @param key
	 * @return
	 */
	public static String getValue(String key){
		return cache.getProperty(key);
	}
}
