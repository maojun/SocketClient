package com.gable.socket.controller;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloClient {
	@RequestMapping("/helloClient")
	@ResponseBody
	public String helloClient() {
		return "helloClient";
	}

	public static void main(String[] args) throws MessagingException {
		Properties prop = new Properties();
		prop.put("mail.host", "smtp.aliyun.com");
		prop.put("mail.transport.protocol", "smtp");
		prop.put("mail.smtp.auth", "true");
		// 使用java发送邮件5步骤
		// 1.创建sesssion
		Session session = Session.getInstance(prop);
		// 开启session的调试模式，可以查看当前邮件发送状态
		session.setDebug(true);

		// 2.通过session获取Transport对象（发送邮件的核心API）
		Transport ts = session.getTransport();
		// 3.通过邮件用户名密码链接，阿里云默认是开启个人邮箱pop3、smtp协议的，所以无需在阿里云邮箱里设置
		ts.connect("jun-mao@gable-tech.com.cn", "Maojun@123");

		// 4.创建邮件

		Message msg = createSimpleMail(session);

		// 5.发送电子邮件

		ts.sendMessage(msg, msg.getAllRecipients());
	}

	public static MimeMessage createSimpleMail(Session session) throws AddressException, MessagingException {
		// 创建邮件对象
		MimeMessage mm = new MimeMessage(session);
		// 设置发件人
		mm.setFrom(new InternetAddress("jun-mao@gable-tech.com.cn"));
		// 设置收件人
		mm.setRecipient(Message.RecipientType.TO, new InternetAddress("jim-su@gable-tech.com.cn"));
		// 设置抄送人
//		mm.setRecipient(Message.RecipientType.CC, new InternetAddress("XXXX@qq.com"));

		mm.setSubject("XXX网站注册邮件！");
		mm.setContent("验证码为690", "text/html;charset=gbk");

		return mm;

	}
}
