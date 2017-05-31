package com.gable.socket.utils;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InitUtil {
	/**
	 * 医院ID——socket端口 映射MAP
	 */
	public static Map<String,String> propertiesMap;
	
	/**
	 * 无界线程池
	 */
	public static ExecutorService executorService = Executors.newCachedThreadPool();
}
