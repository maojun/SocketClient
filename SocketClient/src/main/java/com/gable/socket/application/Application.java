package com.gable.socket.application;

import java.net.Socket;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;

import com.gable.socket.thread.InputSocketThread;
import com.gable.socket.thread.KeepAliveWatchThread;
import com.gable.socket.utils.InitUtil;
import com.gable.socket.utils.PropertiesUtil;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("com.gable.socket.*")
public class Application extends SpringBootServletInitializer
		implements InitializingBean, ApplicationListener<ContextRefreshedEvent> {

	Logger log = Logger.getLogger(Application.class);
	@Value("${PORT:0}")
	Integer PORT; // 约定端口

	@Value("${ADDRESS:''}")
	String ADDRESS; // 服务器IP地址

	// 心跳包间隔时间
	@Value("${KEEPALIVEDELAY}")
	Long keepAliveDelay;

	// 内部服务器转发地址
	@Value("${LocalAddress}")
	String LocalAddress;

	//客户端线程连接数
	@Value("${maxThreadNum}")
	Integer maxThreadNum;
	
	// 容器加载完毕，发送心跳包，监听服务器端业务数据
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		try {
			for (int i = 0; i < maxThreadNum; i++) {
				Socket socket = new Socket(ADDRESS, PORT);
				// 心跳包
				KeepAliveWatchThread sat = new KeepAliveWatchThread(socket, PORT, ADDRESS, keepAliveDelay, LocalAddress,i+1);
				InitUtil.executorService.execute(sat);

				// 接受来自服务器端的请求
				InputSocketThread ist = new InputSocketThread(socket, LocalAddress,i+1);
				InitUtil.executorService.execute(ist);
			}
			log.info("_____Application1,连接服务器成功,ADDRESS:" + ADDRESS + ",端口:" + PORT);
		} catch (Exception e) {
			log.info("_____Application2,连接服务器异常,ADDRESS:" + ADDRESS + ",端口:" + PORT + ",error:" + e.toString());
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 加载配置文件中的变量
		InitUtil.propertiesMap = PropertiesUtil.initApplicationProperties();
		log.info("_____initProperties:" + InitUtil.propertiesMap);
	}
}
