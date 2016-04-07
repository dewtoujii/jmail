package jmail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import javax.mail.internet.MimeMessage;

import de.unifreiburg.cs.proglang.jgs.support.Constraints;
import de.unifreiburg.cs.proglang.jgs.support.Effects;
import de.unifreiburg.cs.proglang.jgs.support.Sec;

public class MailSender {
	@Sec("pub")
	private final String emailAddress;
	@Sec("Owner")
	private final JavaMailConnection javaMailConnection;

	@Constraints({ "@0 <= pub", "@1 <= pub", "Owner <= @2", "@3 <= pub" })
	public MailSender(String hostname, String username, String pw, String emailAddress) {
		this.emailAddress = emailAddress;
		this.javaMailConnection = new JavaMailConnection(hostname, username, pw);
	}

	/**
	 * 
	 * @param args
	 *            arg0=hostname, arg1=username, arg2=pw, arg3=mailaddress
	 */
	@Constraints({ "@0 <= pub"}) // ?
	@Effects({"pub, Owner"})
	public static void main(String[] args) {
		String hostname, username, pw, emailAddress;
		if (args.length == 4) {
			hostname = args[0];
			username = args[1];
			pw = args[2];
			emailAddress = args[3];
		} else {
			throw new RuntimeException("Falsche Parameter!");
		}
		MailSender sender = new MailSender(hostname, username, pw, emailAddress); // init
																					// MailSender
		sender.connect(); // connect
		JMailMessage jMailMessage = sender.readMessage(); // read in
															// JMailMessage
		sender.sendMessage(jMailMessage); // send JMailMessage
		sender.close(); // close
	}

	@Effects({"pub"})
	public void connect() {
		System.out.print("Connecting...");
		if (javaMailConnection != null) {
			javaMailConnection.connectSMTP();
			System.out.println("Done");
		} else {
			throw new RuntimeException("JavaMailConnection ist null!");
		}
	}
	
	@Effects({"pub"})
	public void close() {
		if (javaMailConnection != null)
			javaMailConnection.close();
		else
			throw new RuntimeException("JavaMailConnection ist null!");
	}

	@Constraints({"@0 <= pub"}) //=>  Nachricht kann auch restriktiver als pub sein?
	public void sendMessage(JMailMessage jMailMessage) {
		System.out.print("Sending...");
		if (javaMailConnection != null) {
			MimeMessage mimeMessage = jMailMessage.toMimeMessage(javaMailConnection.getSession());
			javaMailConnection.sendMessage(mimeMessage);
			System.out.println("Done");
		} else {
			throw new RuntimeException("JavaMailConnection ist null!");
		}
	}

	@Constraints({ "pub <= @ret" })
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
			throw new RuntimeException("Nachricht einlesen fehlgeschlagen!", e);
		}
	}
}