/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmail;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import crypto.AES;
import crypto.Ciphertext;
import util.ByteUtils;

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

	public JMailMessage(MimeMessage mimeMessage) throws MessagingException, IOException, ClassNotFoundException {
		this.toAddress = mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString();
		this.emailAddress = mimeMessage.getFrom()[0].toString();
		this.sendDate = mimeMessage.getSentDate();
		this.subject = mimeMessage.getSubject();
		
		// TODO handle decryption of body here
		Object content = mimeMessage.getContent();
		
		if(content instanceof String) {
			this.body = (String)content;
		}
		else {
			MimeMultipart mmp = (MimeMultipart) mimeMessage.getContent();
			MimeBodyPart encryptedDataPart = (MimeBodyPart) mmp.getBodyPart(0);
			MimeBodyPart encryptedKeyPart = (MimeBodyPart) mmp.getBodyPart(1);
			
			InputStream dataIS = encryptedDataPart.getInputStream();		
			Ciphertext encryptedText = (Ciphertext) ByteUtils.deserialize(ByteUtils.toByteArray(dataIS));
			
			InputStream keyIS = encryptedKeyPart.getInputStream();
			Key aesKey = (Key) ByteUtils.deserialize(ByteUtils.toByteArray(keyIS));
			byte[] decryptedBytes = AES.decrypt(aesKey, encryptedText);
			this.body = new String(decryptedBytes, "UTF-8");;
		}		
	}

	public MimeMessage toMimeMessage(Session session) throws MessagingException, IOException {
		MimeMessage message = new MimeMessage(session);

		// TODO handle encryption of body here
		
		Key aesKey;
		try {
			aesKey = AES.getNewKey();			
			
			Ciphertext encryptedText = AES.encrypt(aesKey, body.getBytes());
			
			MimeBodyPart encryptedDataPart = new MimeBodyPart();
			byte[] encryptedDataBytes = ByteUtils.serialize(encryptedText);
			DataSource s = new ByteArrayDataSource(encryptedDataBytes, "application/octet-stream");
			encryptedDataPart.setDataHandler(new DataHandler(s));
			
			MimeBodyPart encryptedKeyPart = new MimeBodyPart();
			byte[] keyBytes = ByteUtils.serialize(aesKey);
			DataSource s1 = new ByteArrayDataSource(keyBytes, "application/octet-stream");
			encryptedKeyPart.setDataHandler(new DataHandler(s1));
			
			MimeMultipart mp = new MimeMultipart(encryptedDataPart, encryptedKeyPart);
			message.setContent(mp);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
//		message.setContent(body, "text/plain");

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
