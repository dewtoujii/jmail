package jmail;

import java.io.IOException;
import java.security.Key;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import crypto.AES;
import crypto.Ciphertext;
import crypto.RSA;
import crypto.RSAKeys;
import de.unifreiburg.cs.proglang.jgs.support.Constraints;
import de.unifreiburg.cs.proglang.jgs.support.Effects;
import de.unifreiburg.cs.proglang.jgs.support.Sec;
import exceptionhandling.JMCHelper;
import exceptionhandling.Result;

/**
 *
 * @author Tobi
 */
public class JMailMessage {
	@Sec("pub")
	private final String toAddress;
	@Sec("pub")
	private final String emailAddress;
	@Sec("pub")
	private final Date sendDate;
	@Sec("pub")
	private final String subject;
	@Sec("Receiver")
	private final String body;

	@Constraints({ "@0 <= pub", "@1 <= pub", "@2 <= pub", "@3 <= pub", "@4 <= Receiver" })
	public JMailMessage(String toAddress, String emailAddress, Date sendDate, String subject, String body) {
		this.toAddress = toAddress;
		this.emailAddress = emailAddress;
		this.sendDate = sendDate;
		this.subject = subject;
		this.body = body;
	}

	@Constraints({ "@0 <= pub" })
	public JMailMessage(MimeMessage mimeMessage) {
		try {
			this.toAddress = mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString();
			this.emailAddress = mimeMessage.getFrom()[0].toString();
			this.sendDate = mimeMessage.getSentDate();
			this.subject = mimeMessage.getSubject();

			// decryption of the message body
			Result<Object> getContentResult = JMCHelper.getContent(mimeMessage);
			Object content = null;
			if(getContentResult.isSuccess())
				content = getContentResult.getObject();
			else
				System.err.println("Fehler beim Lesen des Inhalts!");
			

			String tempBodyContent = "Fehler beim Lesen der MimeMessage";

			if (content instanceof String) {
				tempBodyContent = (String) content;
			} else if (content instanceof MimeMultipart) {
				MimeMultipart mmp = (MimeMultipart) content;
				if (mmp.getCount() == 3) {
					MimeBodyPart encryptedDataPart = (MimeBodyPart) mmp.getBodyPart(0);
					MimeBodyPart ivPart = (MimeBodyPart) mmp.getBodyPart(1);
					MimeBodyPart encryptedKeyPart = (MimeBodyPart) mmp.getBodyPart(2);

					byte[] encText = JavaMailConnection.inputStreamToBytes(encryptedDataPart.getInputStream());
					byte[] iv = JavaMailConnection.inputStreamToBytes(ivPart.getInputStream());
					byte[] keyBytes = JavaMailConnection.inputStreamToBytes(encryptedKeyPart.getInputStream());

					byte[] decryptedKeyBytes = null;
					decryptedKeyBytes = RSA.decrypt(RSAKeys.getPrivateKey(), keyBytes);

					Ciphertext ciphertext = new Ciphertext(encText, iv);

					Key aesKey = new SecretKeySpec(decryptedKeyBytes, "AES");
					byte[] decryptedBytes = AES.decrypt(aesKey, ciphertext);
					tempBodyContent = new String(decryptedBytes);
				}
			}

			this.body = tempBodyContent;

		} catch (MessagingException e) {
			throw new RuntimeException("MessagingException!", e);
		} catch (IOException e) {
			throw new RuntimeException("IOException!", e);
		}
	}

	@Constraints({ "@0 <= pub" })
	// Session public
	public MimeMessage toMimeMessage(Session session) {
		// encryption of the message body
		Key aesKey;
		aesKey = AES.getNewKey();

		// encrypt body with AES
		Ciphertext ciphertext = AES.encrypt(aesKey, body.getBytes());

		Result<MimeBodyPart> bodyPartResult;

		// Encrypted body part
		DataSource dataSource = new ByteArrayDataSource(ciphertext.encText, "application/octet-stream");
		bodyPartResult = JMCHelper.createMimeBodyPart(new DataHandler(dataSource));
		MimeBodyPart encryptedDataPart;
		if (bodyPartResult.isSuccess())
			encryptedDataPart = bodyPartResult.getObject();
		else
			throw new RuntimeException("MessagingException!", bodyPartResult.getException());

		// Initialization Vector part
		DataSource ivSource = new ByteArrayDataSource(ciphertext.iv, "application/octet-stream");
		bodyPartResult = JMCHelper.createMimeBodyPart(new DataHandler(ivSource));
		MimeBodyPart ivPart;
		if (bodyPartResult.isSuccess())
			ivPart = bodyPartResult.getObject();
		else
			throw new RuntimeException("MessagingException!", bodyPartResult.getException());

		// Key part
		byte[] keyBytes = aesKey.getEncoded();
		byte[] encryptedKeyBytes = null;
		encryptedKeyBytes = RSA.encrypt(RSAKeys.getPublicKey(), keyBytes);

		DataSource keySource = new ByteArrayDataSource(encryptedKeyBytes, "application/octet-stream");
		bodyPartResult = JMCHelper.createMimeBodyPart(new DataHandler(keySource));
		MimeBodyPart encryptedKeyPart;
		if (bodyPartResult.isSuccess())
			encryptedKeyPart = bodyPartResult.getObject();
		else
			throw new RuntimeException("MessagingException!", bodyPartResult.getException());

		// create Multipart
		Result<MimeMultipart> multipartResult = JMCHelper.createMimeMultipart(encryptedDataPart, ivPart,
				encryptedKeyPart);
		MimeMultipart mp;
		if (multipartResult.isSuccess())
			mp = multipartResult.getObject();
		else
			throw new RuntimeException("MessagingException!", multipartResult.getException());

		Result<MimeMessage> messageResult = JMCHelper.createMultipartMimeMessage(session, mp, emailAddress, toAddress,
				subject, sendDate);
		MimeMessage message;
		if (messageResult.isSuccess())
			message = messageResult.getObject();
		else
			throw new RuntimeException("MessagingException!", messageResult.getException());

		return message;
	}

	@Effects({ "Owner" })
	public void print() {
		System.out.println("From: " + emailAddress);
		System.out.println("Subject: " + subject);
		System.out.println("Text: " + body);
	}
}
