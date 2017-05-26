package com.gable.socket.thread;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gable.socket.bean.SocketBean;
import com.gable.socket.utils.HttpHelper;
import com.gable.socket.utils.JsonUtil;
import com.gable.socket.utils.TreadLocalRunFlag;

public class InputSocketThread implements Runnable {

	Logger log = Logger.getLogger(KeepAliveWatchThread.class);

	private String LocalAddress;

	private Socket socket;

	public InputSocketThread(Socket socket, String LocalAddress) {
		this.socket = socket;
		this.LocalAddress = LocalAddress;
	}

	@Override
	public void run() {
		while ((boolean) TreadLocalRunFlag.threadLoacl.get().get("RunFlag")) {
			try {
				InputStream in = socket.getInputStream();
				if (in.available() > 0) {
					System.out.println("=====================读取到的长度："+in.available());
					try {
						ObjectInputStream ois = new ObjectInputStream(in);
						Object obj = ois.readObject();
						if (obj != null) {
							String jsonStr = JsonUtil.toJsonString(obj);
							log.info("_____InputSocketThread1,接受来自服务器的参数：" + jsonStr);
							SocketBean object = JsonUtil.getObject(jsonStr, SocketBean.class);
							String serviceURL = object.getServiceURL(); // 转发的请求路径
							String param = object.getParam(); // 转发的参数

							String url = LocalAddress + serviceURL;
							Map<String, String> paramMap = JsonUtil.getMap(param);
							try {
								log.info("_____InputSocketThread4,客户端转发地址：" + url + ",参数：" + paramMap);
								// 尽可能的将返回数据写入到SocketServer这一步，优先与其他业务处理
								String resultJson = HttpHelper.postRequestResponseBodyAsString(url, paramMap);
								if (!StringUtils.isEmpty(resultJson)) {
									JSONObject jsonObj = JSON.parseObject(resultJson);
									SocketBean sb = new SocketBean(object.getUid(), jsonObj.getString("data"),
											jsonObj.getInteger("ret"), jsonObj.getString("msg"));

									try {
										ObjectOutputStream oos = new ObjectOutputStream(
												new BufferedOutputStream(socket.getOutputStream()));
										oos.writeObject(sb);
										oos.flush();
										log.info("_____InputSocketThread6,客户端转发结果：" + resultJson);
									} catch (IOException e) {
										e.printStackTrace();
									}

									// 有文件时，从阿里云上面下载文件，写到本地，不关心结果
									String filePath = paramMap.get("fileAddress");
									if (!StringUtils.isEmpty(filePath))
										new Thread(new FileUploadThread(filePath)).start();
								}
							} catch (Exception e) {
								log.error("_____InputSocketThread5,客户端转发失败：" + e.toString());
							}
						}
					} catch (Exception e1) {
						log.info("_____读取服务端端异常:" + e1);
						socket.close();
					}
				} else {
					Thread.sleep(10);
				}
			} catch (Exception e) {
				log.info("_____InputSocketThread2,处理失败，关闭客户端：" + e.toString());
				TreadLocalRunFlag.threadLoacl.get().put("RunFlag", false);
				try {
					if (!socket.isClosed()) {
						socket.close();
					}
				} catch (IOException e1) {
					log.info("_____InputSocketThread3,关闭客户端异常：" + e1.toString());
				}
			}
		}
	}
	
	public static List<byte[]> readStreamList(InputStream inStream) throws Exception {  
		List<byte[]> list = new ArrayList<byte[]>();
	    int count = 0;  
	    while (count == 0) {  
	        count = inStream.available();  
	    }  
	    byte[] b = new byte[count];  
	    inStream.read(b);  
	    list.add(b);
	    return list;  
	}
	
	public static byte[] readStream(InputStream inStream) throws Exception {  
	    int count = 0;
	    while (count == 0) {
	        count = inStream.available();
	    }
	    byte[] b = new byte[count];  
	    inStream.read(b);  
	    return b;  
	}
}
