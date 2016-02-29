package jmail;

import java.io.IOException;
import java.util.Scanner;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MailReceiver {

	private final JavaMailConnection javaMailConnection;

	public MailReceiver(String hostname, String username, String pw) {
		this.javaMailConnection = new JavaMailConnection(hostname, username, pw);
	}

	public static void main(String[] args) {
		String username;
		String pw;
		if (args.length == 2) {
			username = args[0];
			pw = args[1];
		} else {
			@SuppressWarnings("resource")
			Scanner input = new Scanner(System.in);
			System.out.println("Username: ");
			username = input.nextLine();
			System.out.println("Password:");
			pw = input.nextLine();
		}
		
		MailReceiver receiver = new MailReceiver("localhost", username, pw);
		receiver.connect();
		receiver.printAllMessages();
		receiver.close();
	}
	
	public void connect() {
		System.out.print("Connecting...");
		javaMailConnection.connectPOP3();
		System.out.println("Done");
	}
	
	public void close() {
		javaMailConnection.close();
	}

	public void printAllMessages() {
		int messageCount = javaMailConnection.getMessageCount();
		
		System.out.println("Number of messages: " + messageCount);

		for (int i = 1; i <= messageCount; i++) {
			System.out.println("==============================");
			System.out.println("Email #" + i);
			MimeMessage mimeMessage = javaMailConnection.getMimeMessage(i);
			JMailMessage jMailMessage;
			try {
				jMailMessage = new JMailMessage(mimeMessage);
				jMailMessage.print();
			} catch (ClassNotFoundException | MessagingException | IOException e) {
				System.out.println("Exception");
			}			
		}
	}
}
