package com.gable.socket.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * json辅助类
 * 
 * @author mj
 *
 */
public class JsonUtil {
	/**
	 * 对象转json字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String toJsonString(Object obj) {
		return JSON.toJSONString(obj);
	}

	/**
	 * json字符串转对象
	 * 
	 * @param jsonStr
	 * @param clazz
	 * @return
	 */
	public static <T> T getObject(String jsonStr, Class<T> clazz) {
		T parseObject = JSONObject.parseObject(jsonStr, clazz);
		return parseObject;
	}

	/**
	 * 将json字符串转Map<String,String>
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static Map<String, String> getMap(String jsonStr) {
		Map<String, String> result = new HashMap<String, String>();
		Map map = getObject(jsonStr, Map.class);
		for (Object obj : map.keySet()) {
			result.put(obj+"", map.get(obj)+"");
		}
		return result;
	}
}
