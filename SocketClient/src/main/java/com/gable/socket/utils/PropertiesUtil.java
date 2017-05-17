package com.gable.socket.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * 读取配置文件信息
 * 
 * @author mj
 *
 */
public class PropertiesUtil {

	static Logger log = Logger.getLogger(PropertiesUtil.class);
	private static Properties p;

	/**
	 * 程序初始化加载配置文件中的变量
	 * @return
	 */
	public static Map<String, String> initApplicationProperties() {
		Map<String, String> map = new HashMap<String, String>();
		if (p == null)
			p = initProperties();
		InputStreamReader inputStream2 = null;
		try {
			inputStream2 = new InputStreamReader(PropertiesUtil.class.getResourceAsStream("/application.properties"), "UTF-8");
			p.load(inputStream2);
			Set<Object> keySet = p.keySet();
			Iterator<Object> iterator = keySet.iterator();
			while (iterator.hasNext()) {
				Object next = iterator.next();
				map.put(next.toString(), p.getProperty(next.toString()).trim());
			}
		} catch (IOException e) {
			log.error("_____初始化加载配置文件变量失败");
		}finally{
			try {
				inputStream2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * 获取单个变量
	 * @param key
	 * @return
	 */
	public static String getProByKey(String key) {
		String result = "";
		if (p == null)
			p = initProperties();
		InputStream inputStream2 = Object.class.getResourceAsStream("/application.properties");
		try {
			p.load(inputStream2);
			result = p.getProperty(key).trim();
		} catch (IOException e) {
			log.error("_____获取变量失败:"+e.toString());
		}
		return result;
	}

	public synchronized static Properties initProperties() {
		if (p == null) {
			p = new Properties();
		}
		return p;
	}
}
