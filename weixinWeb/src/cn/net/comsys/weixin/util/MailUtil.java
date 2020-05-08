package cn.net.comsys.weixin.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.util.MailSSLSocketFactory;

import cn.hutool.setting.dialect.Props;

public class MailUtil {

	static Props props = new Props("config.properties");
	
	public static void sendMail(String msg){ 
		 try {
			
		
		Properties prop = new Properties();
		// 开启debug调试，以便在控制台查看
		//prop.setProperty("mail.debug", "true"); 
		// 设置邮件服务器主机名
		prop.setProperty("mail.host", props.getProperty("mail.host"));
		// 发送服务器需要身份验证
		prop.setProperty("mail.smtp.auth", "true");
		// 发送邮件协议名称
		prop.setProperty("mail.transport.protocol", "smtp");

		// 开启SSL加密，否则会失败
		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true);
		prop.put("mail.smtp.ssl.enable", "true");
		prop.put("mail.smtp.ssl.socketFactory", sf);

		// 创建session
		Session session = Session.getInstance(prop);
		// 通过session得到transport对象
		Transport ts = session.getTransport();
		// 连接邮件服务器：邮箱类型，帐号，授权码
		ts.connect("smtp.exmail.qq.com",props.getProperty("mail.account"), props.getProperty("mail.pwd"));
		// 创建邮件
		Message message = createSimpleMail(session, msg);
		// 发送邮件
		ts.sendMessage(message, message.getAllRecipients());
		ts.close();
		 } catch (Exception e) {
				// TODO: handle exception
			 e.printStackTrace();
			}
		}
	 /**
	 * @Method: createSimpleMail
	 * @Description: 创建一封只包含文本的邮件
	 */
	 public static MimeMessage createSimpleMail(Session session, String msg)throws Exception {
	 // 创建邮件对象
	 MimeMessage message = new MimeMessage(session);
	 // 指明邮件的发件人
	 message.setFrom(new InternetAddress(props.getProperty("mail.account")));
	 // 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
	// message.setRecipient(Message.RecipientType.TO, new InternetAddress("hejing@comsys.net.cn,lifuli@comsys.net.cn"));
	 message.setRecipients(Message.RecipientType.TO, props.getProperty("weixin_error_tip_tos"));
	 // 邮件的标题
	 message.setSubject("中国音乐学院微信抓取");
	 // 邮件的文本内容
	 message.setContent(msg, "text/html;charset=UTF-8");
	 // 返回创建好的邮件对象
	 return message;
	 }
	 
	 
	 public static void main(String[] args) {
		sendMail("测试001");
	}
}
