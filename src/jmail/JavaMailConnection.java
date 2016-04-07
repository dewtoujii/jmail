package jmail;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import com.sun.mail.pop3.POP3Store;

import de.unifreiburg.cs.proglang.jgs.support.Constraints;
import de.unifreiburg.cs.proglang.jgs.support.Effects;
import de.unifreiburg.cs.proglang.jgs.support.Sec;
import exceptionhandling.JMCHelper;
import exceptionhandling.Result;

public class JavaMailConnection {

	@Sec("pub")
	private final String hostname;
	@Sec("pub")
	private final String username;
	@Sec("Owner")
	private final String password;
	@Sec("pub")
	private Session session;
	@Sec("Owner")
	private Service mailService;
	@Sec("Owner")
	private Folder emailFolder;

	@Constraints({ "@0 <= pub", "@1 <= pub", "@2 <= Owner" })
	public JavaMailConnection(String hostname, String username, String password) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
	}

	@Effects({ "pub" })
	public void connectSMTP() {
		if (getConnectionType() != 0) {
			System.err.println("Es besteht bereits eine Verbindung!");
			return;
		}
		// Owner
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", hostname);
		props.put("mail.smtp.auth", "true");

		/*
		 * Authenticator auth = new Authenticator() {
		 * 
		 * @Override public PasswordAuthentication getPasswordAuthentication() {
		 * // constraints angeben für Construktor return new
		 * PasswordAuthentication(username, password); } };
		 */

		session = Session.getInstance(props);

		Result<Transport> transportResult = JMCHelper.getTransport(session);
		if (transportResult.isSuccess())
			mailService = transportResult.getObject();
		else {
			throw new RuntimeException("getTransport für Session nicht möglich!", transportResult.getException());
		}

		Result<Void> connectResult = JMCHelper.connectService(mailService, username, password);
		if (!connectResult.isSuccess())
			throw new RuntimeException("Verbinden nicht möglich!", connectResult.getException());

		/*
		 * try { // public effect, da Server mailService.connect(username,
		 * password); } catch (MessagingException e) { throw new
		 * RuntimeException("Verbinden nicht möglich!", e); }
		 */
	}

	@Effects({ "pub" })
	public void connectPOP3() {
		if (getConnectionType() != 0) {
			System.err.println("Es besteht bereits eine Verbindung!");
			return;
		}
		Properties props = new Properties();
		props.put("mail.pop3.host", hostname);
		session = Session.getInstance(props);

		Result<Store> storeResult = JMCHelper.getPOP3Store(session);
		if (storeResult.isSuccess())
			mailService = (POP3Store) storeResult.getObject();
		else {
			throw new RuntimeException("getStore für Session nicht möglich!", storeResult.getException());
		}

		Result<Void> connectResult = JMCHelper.connectService(mailService, username, password);
		if (!connectResult.isSuccess())
			throw new RuntimeException("Verbinden nicht möglich!", connectResult.getException());

		/*
		 * try { mailService = (POP3Store) session.getStore("pop3");
		 * mailService.connect(username, password); } catch
		 * (NoSuchProviderException e) { } catch (MessagingException e) { }
		 */

	}

	@Effects({ "pub" })
	public void close() {
		Result<Void> closeResult = JMCHelper.close(mailService, emailFolder);
		if (!closeResult.isSuccess())
			throw new RuntimeException("Close nicht möglich!", closeResult.getException());
	}

	@Constraints({ "Owner <= @ret" })
	private int getConnectionType() {
		if (mailService != null && mailService.isConnected()) {
			if (mailService instanceof Transport)
				return 1;
			else if (mailService instanceof POP3Store)
				return 2;
		}
		return 0;
	}

	// pub
	public Session getSession() {
		return session;
	}

	@Constraints({ "@0 <= pub" })
	public void /* boolean */ sendMessage(MimeMessage mimeMessage) {
		if (getConnectionType() == 1) {
			Transport transport = (Transport) mailService;
			Result<Void> sendResult = JMCHelper.sendMessage(transport, mimeMessage);
			if (!sendResult.isSuccess())
				throw new RuntimeException("Senden fehlgeschlagen!", sendResult.getException());
		} else {
			System.err.println("Keine SMTP-Verbindung aufgebaut!");
		}
	}

	@Constraints({ "@0 <= pub", "@1 <= pub", "@2 <= pub", "@3 <= pub" })
	public void /* boolean */ sendMessage(String fromAddress, String toAddress, String subject, byte[] messageBytes) {
		Result<MimeMessage> mmResult = JMCHelper.createMimeMessage(getSession(),
				new DataHandler(new ByteArrayDataSource(messageBytes, "application/octet-stream")), fromAddress,
				toAddress, subject);
		if (mmResult.isSuccess())
			sendMessage(mmResult.getObject());
		else
			throw new RuntimeException("MessagingException!", mmResult.getException());
	}

	// pub
	public int getMessageCount() {
		int messageCount = 0;
		POP3Store emailStore = (POP3Store) mailService;
		prepareEmailFolder(emailStore);
		Result<Integer> countResult = JMCHelper.getMessageCount(emailFolder);
		if (countResult.isSuccess())
			messageCount = countResult.getObject();
		else {
			throw new RuntimeException("getMessageCount für Folder nicht möglich!", countResult.getException());
		}
		return messageCount;
	}

	/*
	 * public int getMessageCount() { int messageCount = 0; try { POP3Store
	 * emailStore = (POP3Store) mailService; if(emailFolder == null) {
	 * emailFolder = emailStore.getFolder("INBOX"); } if(!emailFolder.isOpen())
	 * { emailFolder.open(Folder.READ_ONLY); } messageCount =
	 * emailFolder.getMessageCount(); } catch (MessagingException e) {
	 * System.out.println("MessagingException!"); }
	 * 
	 * return messageCount; }
	 */

	@Effects({ "Owner" })
	private void prepareEmailFolder(POP3Store emailStore) {
		if (emailFolder == null) {
			Result<Folder> folderResult = JMCHelper.getFolder(emailStore);
			if (folderResult.isSuccess())
				emailFolder = folderResult.getObject();
			else {
				/* System.out.println("MessagingException!"); */
				throw new RuntimeException("getFolder für POP3Store nicht möglich!", folderResult.getException());
			}
		}
		if (!emailFolder.isOpen()) {
			Result<Void> openFolderResult = JMCHelper.openFolder(emailFolder, Folder.READ_ONLY);
			if (!openFolderResult.isSuccess())
				throw new RuntimeException("open für Folder nicht möglich!", openFolderResult.getException());
		}
	}

	@Constraints({ "@0 <= pub", "@ret <= pub" })
	public byte[] getMessage(int index) {
		MimeMessage mm = getMimeMessage(index);
		Result<InputStream> inputStreamResult = JMCHelper.getInputStream(mm);
		InputStream is;
		if (inputStreamResult.isSuccess())
			is = inputStreamResult.getObject();
		else
			throw new RuntimeException("Fehler beim Lesen des Inputstreams!", inputStreamResult.getException());

		byte[] messageBytes = inputStreamToBytes(is);
		return messageBytes;
	}

	@Constraints({ "@0 <= pub", "@ret <= pub" })
	public MimeMessage getMimeMessage(int index) {
		MimeMessage mimeMessage = null;
		POP3Store emailStore = (POP3Store) mailService;
		prepareEmailFolder(emailStore);

		Result<Message> messageResult = JMCHelper.getMessage(emailFolder, index);
		if (messageResult.isSuccess())
			mimeMessage = (MimeMessage) messageResult.getObject();
		else {
			throw new RuntimeException("Fehler beim Abrufen der Nachricht!", messageResult.getException());
		}

		return mimeMessage;
	}

	/*
	 * public MimeMessage getMimeMessage(int index) { MimeMessage mimeMessage =
	 * null; try {
	 * 
	 * POP3Store emailStore = (POP3Store) mailService; if (emailFolder == null)
	 * { emailFolder = emailStore.getFolder("INBOX"); } if
	 * (!emailFolder.isOpen()) { emailFolder.open(Folder.READ_ONLY); }
	 * mimeMessage = (MimeMessage) emailFolder.getMessage(index); } catch
	 * (MessagingException e) { System.out.println("MessagingException!"); }
	 * 
	 * return mimeMessage; }
	 */

	@Constraints({ "@0 <= @ret", "@ret <= @0" }) // geht das?
	protected static byte[] inputStreamToBytes(InputStream is) {
		Result<ByteArrayOutputStream> convertResult = JMCHelper.convertISToByteArray(is);
		if (convertResult.isSuccess())
			return convertResult.getObject().toByteArray();
		else
			throw new RuntimeException("Fehler beim Konvertieren des Inputstreams!", convertResult.getException());
	}
}
