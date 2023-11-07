package com.itbank.component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Component
public class MailComponent {
	
	private final String host = "smtp.naver.com";
	private final int port = 465;
	private String serverID = "";	// 메일 계정 아이디
	private String serverPW = "";	// 메일 계정 비밀번호
	
	private Properties props;
	
	// classpath 에서 특정 파일 불러오기
	// src/main/java
	// src/main/resources
	@Value("classpath:mailForm.html")
	private Resource mailForm;
	
	private String tag = "";
	
	public MailComponent() throws FileNotFoundException, IOException{
		// 파일 내용 불러와서 태그에 넣어두기
		props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.trust", host);
	}
	

	public int sendMimeMessage(HashMap<String, String> param) {
		Session mailSession = Session.getDefaultInstance(props, new Authenticator() {
			String un = serverID;
			String pw = serverPW;
			
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(un, pw);
			}
		});
		
		mailSession.setDebug(true);
		
		Message message = new MimeMessage(mailSession);
		String tag = "";
		try {
			Scanner sc = new Scanner(mailForm.getFile());
			while(sc.hasNextLine()) {
				tag += sc.nextLine();
			}
			sc.close();
			
			message.setFrom(new InternetAddress(serverID + "@naver.com"));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(param.get("receiver")));
			message.setSubject(param.get("subject"));
			message.setContent(
					String.format(tag, param.get("content")),
					"text/html; charset=utf-8");
			Transport.send(message);
			return 1;
			
		} catch (MessagingException | IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	

}
