package jmail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import com.sun.mail.pop3.POP3Store;

public class JavaMailConnection {

	private final String hostname;
	private final String username;
	private final String password;
	private Session session;
	private Service mailService;
	private Folder emailFolder;

	public JavaMailConnection(String hostname, String username, String password) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
	}

	public void connectSMTP() {
		if(getConnectionType() != 0) {
			System.out.println("There is already a connection!");
			return;
		}
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", hostname);
		props.put("mail.smtp.auth", "true");

		Authenticator auth = new Authenticator() {
			@Override
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		};

		session = Session.getInstance(props, auth);

		try {
			mailService = session.getTransport();
			mailService.connect();
		} catch (NoSuchProviderException e) {
		} catch (MessagingException e) {
		}

	}

	public void connectPOP3() {
		if(getConnectionType() != 0) {
			System.out.println("There is already a connection!");
			return;
		}
		Properties props = new Properties();
		props.put("mail.pop3.host", hostname);
		session = Session.getInstance(props);
		try {
			mailService = (POP3Store) session.getStore("pop3");
			mailService.connect(username, password);
		} catch (NoSuchProviderException e) {
		} catch (MessagingException e) {
		}

	}

	public void close() {
		try {
			if (emailFolder != null)
				emailFolder.close(false);
			if (mailService != null) {
				mailService.close();
				mailService = null;
			}
		} catch (MessagingException e) {
		}
	}

	private int getConnectionType() {
		if (mailService != null && mailService.isConnected()) {
			if (mailService instanceof Transport)
				return 1;
			else if (mailService instanceof POP3Store)
				return 2;
		}
		return 0;
	}

	public Session getSession() {
		return session;
	}

	public boolean sendMessage(MimeMessage mimeMessage) {
		if (mailService instanceof Transport) {
			Transport transport = (Transport) mailService;
			try {
				transport.sendMessage(mimeMessage, mimeMessage.getRecipients(Message.RecipientType.TO));
			} catch (MessagingException e) {
				System.out.println("MessagingException!");
				return false;
			}
			return true;
		}
		return false;
	}

	public boolean sendMessage(String fromAddress, String toAddress, String subject, byte[] messageBytes) {
		MimeMessage message = new MimeMessage(getSession());
		DataSource messageSource = new ByteArrayDataSource(messageBytes, "application/octet-stream");

		try {
			message.setDataHandler(new DataHandler(messageSource));
			message.setFrom(new InternetAddress(fromAddress));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
			message.setSubject(subject);
			sendMessage(message);
		} catch (MessagingException e) {
			System.out.println("MessagingException!");
			return false;
		}
		return true;
	}

	public int getMessageCount() {
		int messageCount = 0;
		try {
			POP3Store emailStore = (POP3Store) mailService;
			if(emailFolder == null) {
			emailFolder = emailStore.getFolder("INBOX");
			}
			if(!emailFolder.isOpen()) {
				emailFolder.open(Folder.READ_ONLY);
			}			
			messageCount = emailFolder.getMessageCount();
		} catch (MessagingException e) {
			System.out.println("MessagingException!");
		}

		return messageCount;
	}

	public byte[] getMessage(int index) {
		byte[] messageBytes = new byte[] {};
		MimeMessage mm = getMimeMessage(index);
		try {
			messageBytes = inputStreamToBytes(mm.getDataHandler().getDataSource().getInputStream());
		} catch (IOException | MessagingException e) {
			System.out.println("IO or Messaging Exception!");
		}
		return messageBytes;
	}

	public MimeMessage getMimeMessage(int index) {
		MimeMessage mimeMessage = null;
		try {

			POP3Store emailStore = (POP3Store) mailService;
			if (emailFolder == null) {
				emailFolder = emailStore.getFolder("INBOX");
			}
			if (!emailFolder.isOpen()) {
				emailFolder.open(Folder.READ_ONLY);
			}
			mimeMessage = (MimeMessage) emailFolder.getMessage(index);
		} catch (MessagingException e) {
			System.out.println("MessagingException!");
		}

		return mimeMessage;
	}

	protected static byte[] inputStreamToBytes(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int byteRead;
		while ((byteRead = is.read()) != -1) {
			baos.write(byteRead);
		}
		baos.close();

		return baos.toByteArray();
	}
}
