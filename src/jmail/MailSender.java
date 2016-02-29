package jmail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Scanner;

import javax.mail.MessagingException;

public class MailSender {
	private final String emailAddress;
	private final JavaMailConnection javaMailConnection;

	public MailSender(String hostname, String username, String pw, String emailAddress) {
		this.emailAddress = emailAddress;
		this.javaMailConnection = new JavaMailConnection(hostname, username, pw);
	}

	/**
	 * 
	 * @param args
	 *            arg0=username, arg1=pw, arg2=mailaddress
	 */
	public static void main(String[] args) {
		String username, pw, emailAddress;
		if (args.length == 3) {
			username = args[0];
			pw = args[1];
			emailAddress = args[2];
		} else {
			@SuppressWarnings("resource")
			Scanner input = new Scanner(System.in);
			System.out.print("Username: ");
			username = input.nextLine();
			System.out.print("Password: ");
			pw = input.nextLine();
			System.out.print("Email-address: ");
			emailAddress = input.nextLine();
		}
		MailSender sender = new MailSender("localhost", username, pw, emailAddress); // init MailSender
		sender.connect(); // connect
		JMailMessage jMailMessage = sender.readMessage(); // read in JMailMessage
		sender.sendMessage(jMailMessage); // send JMailMessage
		sender.close(); // close
	}

	public void connect() {
		System.out.print("Connecting...");
		javaMailConnection.connectSMTP();
		System.out.println("Done");
	}

	public void close() {
		javaMailConnection.close();
	}

	public void sendMessage(JMailMessage jMailMessage) {
		System.out.print("Sending...");
		try {
			javaMailConnection.sendMessage(jMailMessage.toMimeMessage(javaMailConnection.getSession()));
			System.out.println("Done");
		} catch (MessagingException | IOException e) {
			System.out.println("Error!");
		}

	}

	public JMailMessage readMessage() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.print("Recipient: ");
			String recipient = br.readLine();
			System.out.print("Subject: ");
			String subject = br.readLine();
			StringBuffer buf = new StringBuffer();
			System.out.println("Text: (end with . in own line)");
			String line = "";
			do {
				buf.append(line + "\n");
				line = br.readLine();
			} while (line != null && line.compareTo(".") != 0);

			return new JMailMessage(recipient, emailAddress, new Date(System.currentTimeMillis()), subject,
					buf.toString());
		} catch (IOException e) {
			System.out.println("Error while reading in message");
			return null;
		}
	}
}