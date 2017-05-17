package com.gable.socket.utils;

import java.util.HashMap;

public class TreadLocalRunFlag {
	public static ThreadLocal<HashMap<String,Object>> threadLoacl = new ThreadLocal<HashMap<String,Object>>(){ 
	        @Override 
	        protected HashMap initialValue() {
	        	HashMap<String,Object> map = new HashMap<String,Object>();
	        	map.put("RunFlag", true);	//SocketClient 监控 SocketServer 标识
	        	map.put("SendFlag", true);	//邮件开关
	            return map; 
	        } 
	    }; 
}
