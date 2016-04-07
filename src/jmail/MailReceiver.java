package jmail;

import javax.mail.internet.MimeMessage;

import de.unifreiburg.cs.proglang.jgs.support.Constraints;
import de.unifreiburg.cs.proglang.jgs.support.Effects;
import de.unifreiburg.cs.proglang.jgs.support.Sec;

public class MailReceiver {

	@Sec("Owner")
	private final JavaMailConnection javaMailConnection;

	@Constraints({ "@0 <= pub", "@1 <= pub", "Owner <= @2" })
	public MailReceiver(String hostname, String username, String pw) {
		this.javaMailConnection = new JavaMailConnection(hostname, username, pw);
	}

	@Constraints({ "@0 <= pub"}) // ?
	@Effects({"Owner"})
	public static void main(String[] args) {
		String hostname, username, pw;
		if (args.length == 3) {
			hostname = args[0];
			username = args[1];
			pw = args[2];
		} else {
			throw new RuntimeException("Falsche Parameter!");
		}

		MailReceiver receiver = new MailReceiver(hostname, username, pw);
		receiver.connect();
		receiver.printAllMessages();
		receiver.close();
	}

	@Effects({"Owner"})
	public void connect() {
		System.out.print("Connecting...");
		if (javaMailConnection != null) {
			javaMailConnection.connectPOP3();
			System.out.println("Done");
		} else {
			throw new RuntimeException("JavaMailConnection ist null!");
		}

	}

	@Effects({"Owner"})
	public void close() {
		if (javaMailConnection != null)
			javaMailConnection.close();
		else
			throw new RuntimeException("JavaMailConnection ist null!");
	}

	@Constraints({ "@0 <= pub", "@ret <= pub" })
	public JMailMessage getJMailMessage(int index) {
		if (javaMailConnection == null)
			throw new RuntimeException("JavaMailConnection ist null!");
		MimeMessage mimeMessage = javaMailConnection.getMimeMessage(index);
		JMailMessage jMailMessage = null;
		jMailMessage = new JMailMessage(mimeMessage);
		return jMailMessage;
	}

	public void printAllMessages() {
		if (javaMailConnection == null)
			throw new RuntimeException("JavaMailConnection ist null!");
		
		int messageCount = javaMailConnection.getMessageCount();

		System.out.println("Number of messages: " + messageCount);

		for (int i = 1; i <= messageCount; i++) {
			System.out.println("==============================");
			System.out.println("Email #" + i);
			JMailMessage jMailMessage = getJMailMessage(i);
			jMailMessage.print();
		}
	}
}
