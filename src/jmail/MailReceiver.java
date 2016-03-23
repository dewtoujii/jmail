package jmail;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MailReceiver {

	private final JavaMailConnection javaMailConnection;

	public MailReceiver(String hostname, String username, String pw) {
		this.javaMailConnection = new JavaMailConnection(hostname, username, pw);
	}

	public static void main(String[] args) {
		String hostname, username, pw;
		if (args.length == 3) {
			hostname = args[0];
			username = args[1];
			pw = args[2];
		} else {
			System.out.println("Falsche Parameter!");
			return;
		}

		MailReceiver receiver = new MailReceiver(hostname, username, pw);
		receiver.connect();
		receiver.printAllMessages();
		receiver.close();
	}

	public void connect() {
		System.out.print("Connecting...");
		if (javaMailConnection != null) {
			javaMailConnection.connectPOP3();
			System.out.println("Done");
		} else {
			System.out.println("Error");
		}

	}

	public void close() {
		if (javaMailConnection != null)
			javaMailConnection.close();
	}

	public JMailMessage getJMailMessage(int index) {
		if (javaMailConnection == null)
			return null;
		MimeMessage mimeMessage = javaMailConnection.getMimeMessage(index);
		JMailMessage jMailMessage = null;
		try {
			jMailMessage = new JMailMessage(mimeMessage);
		} catch (ClassNotFoundException | MessagingException | IOException e) {
			System.out.println("Exception");
		}
		return jMailMessage;
	}

	public void printAllMessages() {
		int messageCount = javaMailConnection != null ? javaMailConnection.getMessageCount() : 0;

		System.out.println("Number of messages: " + messageCount);

		for (int i = 1; i <= messageCount; i++) {
			System.out.println("==============================");
			System.out.println("Email #" + i);
			JMailMessage jMailMessage = getJMailMessage(i);
			jMailMessage.print();
		}
	}
}
