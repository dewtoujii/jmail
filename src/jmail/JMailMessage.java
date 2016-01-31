/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmail;

import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.activation.DataContentHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.crypto.provider.AESKeyGenerator;

import crypto.AES;
import crypto.Ciphertext;

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

	public JMailMessage(MimeMessage mimeMessage) throws MessagingException, IOException {
		this.toAddress = mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString();
		this.emailAddress = mimeMessage.getFrom()[0].toString();
		this.sendDate = mimeMessage.getSentDate();
		this.subject = mimeMessage.getSubject();
		// TODO handle decryption of body here
//		MimeMultipart mmp = (MimeMultipart) mimeMessage.getContent();
//		MimeBodyPart encryptedDataPart = (MimeBodyPart) mmp.getBodyPart(0);
//		MimeBodyPart encryptedKeyPart = (MimeBodyPart) mmp.getBodyPart(1);
//		Ciphertext encryptedText = (Ciphertext) encryptedDataPart.getContent();
//		Key aesKey = (Key) encryptedKeyPart.getContent();
//		String decryptedText = AES.decrypt(aesKey, encryptedText).toString();
//		this.body = decryptedText;
		this.body = mimeMessage.getContent().toString();
	}

	public MimeMessage toMimeMessage(Session session) throws MessagingException {
		MimeMessage message = new MimeMessage(session);

		// TODO handle encryption of body here
//		Key aesKey;
//		try {
//			aesKey = AES.getNewKey();
//			KeyFactory kf = null;
//			
//			Ciphertext encryptedText = AES.encrypt(aesKey, body.getBytes());
//			MimeBodyPart encryptedDataPart = new MimeBodyPart();
//			encryptedDataPart.setContent(encryptedText, "application/java-vm");
//			MimeBodyPart encryptedKeyPart = new MimeBodyPart();
//			encryptedKeyPart.setContent(aesKey, "application/java-vm");
//
//			MimeMultipart mp = new MimeMultipart(encryptedDataPart, encryptedKeyPart);
//			message.setContent(mp);
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
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
