package com.gable.socket.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

/**
 * aliyun邮件发送
 * 
 * @author mj
 *
 */
public class AliMailSendUtil {
	static Logger log = Logger.getLogger(AliMailSendUtil.class);
	// 阿里SMTP服务器地址

	public static Boolean sendMail() {
		try {
			// 配置发送邮件的环境属性
			final Properties props = new Properties();
			// 表示SMTP发送邮件，需要进行身份验证
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.host", InitUtil.propertiesMap.get("ALIDM_SMTP_HOST"));
			props.put("mail.smtp.port", InitUtil.propertiesMap.get("ALIDM_SMTP_PORT"));

			// 发件人的账号
			props.put("mail.user", InitUtil.propertiesMap.get("FROMUSER"));
			// 访问SMTP服务时需要提供的密码(邮箱密码)
			props.put("mail.password", InitUtil.propertiesMap.get("FROMPASSWORD"));
			// 构建授权信息，用于进行SMTP进行身份验证
			Authenticator authenticator = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					// 用户名、密码
					String userName = props.getProperty("mail.user");
					String password = props.getProperty("mail.password");
					return new PasswordAuthentication(userName, password);
				}
			};
			// 使用环境属性和授权信息，创建邮件会话
			Session mailSession = Session.getInstance(props, authenticator);
			// 创建邮件消息
			MimeMessage message = new MimeMessage(mailSession);
			// 设置发件人
			InternetAddress form = new InternetAddress(props.getProperty("mail.user"));
			message.setFrom(form);
			// 设置收件人
			String toUser = InitUtil.propertiesMap.get("TO_USER");
			String[] address = toUser.split(",");
			InternetAddress[] sendTo = new InternetAddress[address.length];
			for (int i = 0; i < address.length; i++) {
				sendTo[i] = new InternetAddress(address[i]);
			}
			message.setRecipients(MimeMessage.RecipientType.TO, sendTo);
			// 设置邮件标题
			message.setSubject(InitUtil.propertiesMap.get("MAIL_SUBJECT"));
			// 设置邮件的内容体
			message.setContent(InitUtil.propertiesMap.get("MAIL_CONTENT"), "text/html;charset=UTF-8");
			// 发送邮件
			Transport.send(message);
		} catch (Exception e) {
			log.error("_____AliMailSendUtil,sendMail,邮件发送失败：" + e.toString());
			return false;
		}
		return true;
	}
}
