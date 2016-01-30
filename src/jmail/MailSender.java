package jmail;

import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

public class MailSender {
	private final String hostname;
	private final String username;
	private final String pw;
	private final String emailAddress;

	public MailSender(String hostname, String username, String pw, String emailAddress) {
		this.hostname = hostname;
		this.username = username;
		this.pw = pw;
		this.emailAddress = emailAddress;
	}

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.print("Username: ");
		String username = input.nextLine();
		System.out.print("Password:");
		String pw = input.nextLine();
		MailSender sender = new MailSender("localhost", username, pw, username);

		JMailMessage jMailMessage = sender.readMessage();

		try {
			sender.sendMessage(jMailMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}

	public void sendMessage(JMailMessage jMailMessage) throws MessagingException {
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", hostname);
		props.put("mail.smtp.auth", "true");

		Authenticator auth = new SMTPAuthenticator();

		Session mailSession = Session.getDefaultInstance(props, auth);
		// Session mailSession = Session.getDefaultInstance(props);

		MimeMessage mimeMessage = jMailMessage.toMimeMessage(mailSession);

		// uncomment for debugging infos to stdout
		// mailSession.setDebug(true);
		Transport transport = mailSession.getTransport();
		transport.connect();
		transport.sendMessage(mimeMessage, mimeMessage.getRecipients(Message.RecipientType.TO));
		transport.close();
	}

	public JMailMessage readMessage() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Recipient: ");
		String recipient = scanner.nextLine();
		System.out.print("Subject: ");
		String subject = scanner.nextLine();
		System.out.print("Text: ");
		String content = scanner.nextLine();
		System.out.print("Sending...");

		return new JMailMessage(recipient, emailAddress, new Date(System.currentTimeMillis()), subject, content);
	}

	private class SMTPAuthenticator extends Authenticator {
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, pw);
		}
	}
}