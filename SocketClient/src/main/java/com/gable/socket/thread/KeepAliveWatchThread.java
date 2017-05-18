package com.gable.socket.thread;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.gable.socket.utils.AliMailSendUtil;
import com.gable.socket.utils.InitUtil;
import com.gable.socket.utils.TreadLocalRunFlag;

/**
 * 客户端心跳包通讯类
 * 
 * @author mj
 *
 */
public class KeepAliveWatchThread implements Runnable {

	Logger log = Logger.getLogger(KeepAliveWatchThread.class);

	private Socket socket;

	private Integer port; // 约定端口

	private String address; // 服务器IP地址

	// 心跳包间隔时间
	private Long keepAliveDelay;

	private String LocalAddress;

	public KeepAliveWatchThread(Socket socket, Integer port, String address, Long keepAliveDelay, String LocalAddress) {
		this.socket = socket;
		this.port = port;
		this.address = address;
		this.keepAliveDelay = keepAliveDelay;
		this.LocalAddress = LocalAddress;
	}

	@Override
	public void run() {
		long checkDelay = 10;
		long lastSendTime = System.currentTimeMillis();
		long firstDisconnectTime = 0L;
		Boolean runing = true;
		while (runing) {
			if (System.currentTimeMillis() - lastSendTime > keepAliveDelay) {
				try {
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					oos.writeObject("Heartbeat");
					oos.flush();
					lastSendTime = System.currentTimeMillis();
				} catch (Exception e) {
					log.error("_____KeepAliveWatchThread1,发送心跳失败，等待重新连接:" + e.toString());
					try {
						TreadLocalRunFlag.threadLoacl.get().put("RunFlag", false);
						socket.close();
					} catch (IOException e1) {
						log.error("_____KeepAliveWatchThread2,关闭客户端失败:" + e.toString());
					}
					// 尝试重新连接，如在配置时间内无法重连，则发送邮件
					try {
						// 邮件发送标识，true在一段时间后如果还无法重连，则发送一次邮件，邮件发送过后，则将关闭邮件发送标识
						// && (Boolean) TreadLocalRunFlag.threadLoacl.get().get("SendFlag")
						if (firstDisconnectTime == 0)
							firstDisconnectTime = System.currentTimeMillis();
						
						//尝试重新连接
						Socket socket = new Socket(address, port);
						this.socket = socket;
						
						//重连成功，1.重置监听标识，2.打开邮件发送标识，3.重启监听
						// 重置SocketClient监听SocketServer数据写入标识
						TreadLocalRunFlag.threadLoacl.get().put("RunFlag", true);
						log.info("_____KeepAliveWatchThread3,重新连接服务器成功" + TreadLocalRunFlag.threadLoacl.get());
						firstDisconnectTime = 0; // 重置断开时间
//						TreadLocalRunFlag.threadLoacl.get().put("SendFlag", true); // 打开邮件发送标识
						// 重启监听
						InputSocketThread ist = new InputSocketThread(socket, LocalAddress);
						new Thread(ist).start();
					} catch (Exception e1) {
						log.error("_____KeepAliveWatchThread4,重新尝试连接服务器失败，等待下次重试:" + e1.toString());
						// 如在配置时间内无法重连，则发送邮件 && (Boolean) TreadLocalRunFlag.threadLoacl.get().get("SendFlag")
						if (System.currentTimeMillis() - firstDisconnectTime > Long
								.parseLong(InitUtil.propertiesMap.get("SENDMAILDELAY").toString())
								&& !(Boolean) TreadLocalRunFlag.threadLoacl.get().get("RunFlag")) {
							Boolean sendMail = AliMailSendUtil.sendMail();
							if (sendMail) {
//								TreadLocalRunFlag.threadLoacl.get().put("SendFlag", false);// 关闭邮件发送标识
								firstDisconnectTime = 0; // 重置断开时间
							}
						}
					}

				}
				lastSendTime = System.currentTimeMillis();
			} else {
				try {
					Thread.sleep(checkDelay);
				} catch (InterruptedException e) {
					log.error("_____KeepAliveWatchThread5,心跳包停顿异常:" + e.toString());
				}
			}
		}
	}

}
