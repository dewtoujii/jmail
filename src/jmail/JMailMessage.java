/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmail;

import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Tobi
 */
public class JMailMessage {
	private final String toAddress;
	private final String emailAddress;
	private final Date sendDate;
	private final String subject;
	private final String body;

	public JMailMessage(String toAddress, String emailAddress, Date sendDate, String subject, String body) {
		this.toAddress = toAddress;
		this.emailAddress = emailAddress;
		this.sendDate = sendDate;
		this.subject = subject;
		this.body = body;
	}

	public MimeMessage toMimeMessage(Session session) throws MessagingException {
		MimeMessage message = new MimeMessage(session);
		
		// TODO handle encryption here

		message.setContent(body, "text/plain");
		message.setSubject(subject);
		message.setSentDate(sendDate);
		message.setFrom(new InternetAddress(emailAddress));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));

		return message;
	}
	
	public void print() {
		System.out.println("From: " + emailAddress);
		System.out.println("Subject: " + subject);        
        System.out.println("Text: " + body);
	}
}
