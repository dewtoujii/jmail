package jmail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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
	 *            arg0=hostname, arg1=username, arg2=pw, arg3=mailaddress
	 */
	public static void main(String[] args) {
		String hostname, username, pw, emailAddress;
		if (args.length == 4) {
			hostname = args[0];
			username = args[1];
			pw = args[2];
			emailAddress = args[3];
		} else {
			System.out.println("Falsche Parameter!");
			return;
		}
		MailSender sender = new MailSender(hostname, username, pw, emailAddress); // init
																					// MailSender
		sender.connect(); // connect
		JMailMessage jMailMessage = sender.readMessage(); // read in
															// JMailMessage
		try {
			sender.sendMessage(jMailMessage);
		} catch (MessagingException | IOException e) {
			System.out.println("Fehler beim Senden!");
		} // send JMailMessage
		sender.close(); // close
	}

	public void connect() {
		System.out.print("Connecting...");
		if (javaMailConnection != null) {
			javaMailConnection.connectSMTP();
			System.out.println("Done");
		} else {
			System.out.println("Error");
		}
	}

	public void close() {
		if (javaMailConnection != null)
			javaMailConnection.close();
	}

	public void sendMessage(JMailMessage jMailMessage) throws MessagingException, IOException {
		System.out.print("Sending...");
		if (javaMailConnection != null) {
			MimeMessage mimeMessage = jMailMessage.toMimeMessage(javaMailConnection.getSession());
			javaMailConnection.sendMessage(mimeMessage);
			System.out.println("Done");
		} else {
			System.out.println("Error");
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
			System.out.println("Fehler beim Einlesen der Nachricht!");
			return null;
		}
	}
}