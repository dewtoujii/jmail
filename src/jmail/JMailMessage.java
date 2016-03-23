package jmail;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
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
import crypto.RSA;
import crypto.RSAKeys;

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
		
		// decryption of the message body
		Object content = mimeMessage.getContent();
		
		if(content instanceof String) {
			this.body = (String)content;
		}
		else if(content instanceof MimeMultipart) {
			MimeMultipart mmp = (MimeMultipart) mimeMessage.getContent();
			if(mmp.getCount()== 3) {
				MimeBodyPart encryptedDataPart = (MimeBodyPart) mmp.getBodyPart(0);
				MimeBodyPart ivPart = (MimeBodyPart) mmp.getBodyPart(1);
				MimeBodyPart encryptedKeyPart = (MimeBodyPart) mmp.getBodyPart(2);
				
				byte[] encText = JavaMailConnection.inputStreamToBytes(encryptedDataPart.getInputStream());
				byte[] iv = JavaMailConnection.inputStreamToBytes(ivPart.getInputStream());
				byte[] keyBytes = JavaMailConnection.inputStreamToBytes(encryptedKeyPart.getInputStream());
				
				byte[] decryptedKeyBytes = null;
				try {
					decryptedKeyBytes = RSA.decrypt(RSAKeys.getPrivateKey(), keyBytes);
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException e) {
				}
				
				Ciphertext ciphertext = new Ciphertext(encText, iv);
				
				Key aesKey = new SecretKeySpec(decryptedKeyBytes, "AES");
				byte[] decryptedBytes = AES.decrypt(aesKey, ciphertext);
				this.body = new String(decryptedBytes);
			}
			else {
				this.body = "Fehler beim Lesen der MimeMessage";
			}
			
		}
		else {
			this.body = "Fehler beim Lesen der MimeMessage";
		}
	}

	public MimeMessage toMimeMessage(Session session) throws MessagingException, IOException {
		MimeMessage message = new MimeMessage(session);
		
		// encryption of the message body
		Key aesKey;
		try {
			aesKey = AES.getNewKey();			
			
			// encrypt body with AES
			Ciphertext ciphertext = AES.encrypt(aesKey, body.getBytes());
			
			// Encrypted body part
			MimeBodyPart encryptedDataPart = new MimeBodyPart();
			DataSource dataSource = new ByteArrayDataSource(ciphertext.encText, "application/octet-stream");
			encryptedDataPart.setDataHandler(new DataHandler(dataSource));
			
			// Initialization Vector part
			MimeBodyPart ivPart = new MimeBodyPart();
			DataSource ivSource = new ByteArrayDataSource(ciphertext.iv, "application/octet-stream");
			ivPart.setDataHandler(new DataHandler(ivSource));
			
			// Key part
			MimeBodyPart encryptedKeyPart = new MimeBodyPart();
			byte[] keyBytes = aesKey.getEncoded();
			byte[] encryptedKeyBytes = null;
			encryptedKeyBytes = RSA.encrypt(RSAKeys.getPublicKey(), keyBytes);
			
			DataSource keySource = new ByteArrayDataSource(encryptedKeyBytes, "application/octet-stream");
			encryptedKeyPart.setDataHandler(new DataHandler(keySource));
			
			// create Multipart
			MimeMultipart mp = new MimeMultipart(encryptedDataPart, ivPart, encryptedKeyPart);
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
