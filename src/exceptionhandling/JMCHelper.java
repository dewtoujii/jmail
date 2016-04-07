package exceptionhandling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.pop3.POP3Store;

public class JMCHelper {

	public static Result<Transport> getTransport(Session session) {
		Transport transport = null;
		try {
			transport = session.getTransport();
		} catch (NoSuchProviderException e) {
			return new Result<Transport>(false, null, e);
		}
		return new Result<Transport>(true, transport, null);
	}

	public static Result<Store> getPOP3Store(Session session) {
		Store pop3Store = null;
		try {
			pop3Store = session.getStore("pop3");
		} catch (NoSuchProviderException e) {
			return new Result<Store>(false, null, e);
		}
		return new Result<Store>(true, pop3Store, null);
	}

	public static Result<Folder> getFolder(POP3Store pop3Store) {
		Folder folder = null;
		try {
			folder = pop3Store.getFolder("INBOX");
		} catch (MessagingException e) {
			return new Result<Folder>(false, null, e);
		}
		return new Result<Folder>(true, folder, null);
	}

	public static Result<Integer> getMessageCount(Folder folder) {
		int count = 0;
		try {
			count = folder.getMessageCount();
		} catch (MessagingException e) {
			return new Result<Integer>(false, null, e);
		}
		return new Result<Integer>(true, count, null);
	}

	public static Result<Message> getMessage(Folder emailFolder, int index) {
		Message message = null;
		try {
			message = emailFolder.getMessage(index);
		} catch (MessagingException e) {
			return new Result<Message>(false, null, e);
		}
		return new Result<Message>(true, message, null);
	}
	
	public static Result<Object> getContent(MimeMessage mimeMessage) {
		Object content;
		try {
			content = mimeMessage.getContent();
		} catch (IOException | MessagingException e) {
			return new Result<Object>(false, null, e);
		}
		return new Result<Object>(true, content, null);
	}

	public static Result<InputStream> getInputStream(MimeMessage mm) {
		InputStream is;
		try {
			is = mm.getDataHandler().getDataSource().getInputStream();
		} catch (IOException | MessagingException e) {
			return new Result<InputStream>(false, null, e);
		}
		return new Result<InputStream>(true, is, null);
	}

	public static Result<Integer> readInputStream(InputStream is) {
		Integer integer;
		try {
			integer = is.read();
		} catch (IOException e) {
			return new Result<Integer>(false, null, e);
		}
		return new Result<Integer>(true, integer, null);
	}

	public static Result<ByteArrayOutputStream> convertISToByteArray(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			int byteRead;
			while ((byteRead = is.read()) != -1) {
				baos.write(byteRead);
			}
			baos.close();
		} catch (IOException e) {
			return new Result<ByteArrayOutputStream>(false, null, e);
		}
		return new Result<ByteArrayOutputStream>(true, baos, null);
	}

	public static Result<MimeMessage> createMimeMessage(Session session, DataHandler dataHandler, String fromAddress,
			String toAddress, String subject) {
		MimeMessage mimeMessage = new MimeMessage(session);
		try {
			mimeMessage.setDataHandler(dataHandler);
			mimeMessage.setFrom(new InternetAddress(fromAddress));
			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
			mimeMessage.setSubject(subject);
		} catch (MessagingException e) {
			return new Result<MimeMessage>(false, null, e);
		}
		return new Result<MimeMessage>(true, mimeMessage, null);
	}

	public static Result<MimeMessage> createMultipartMimeMessage(Session session, MimeMultipart multipart,
			String fromAddress, String toAddress, String subject, Date sendDate) {
		MimeMessage message = new MimeMessage(session);
		try {
			message.setContent(multipart);

			// message.setContent(body, "text/plain");

			message.setSubject(subject);
			message.setSentDate(sendDate);
			message.setFrom(new InternetAddress(fromAddress));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
		} catch (MessagingException e) {
			return new Result<MimeMessage>(false, null, e);
		}
		return new Result<MimeMessage>(true, message, null);
	}

	public static Result<MimeMultipart> createMimeMultipart(BodyPart... parts) {
		MimeMultipart mimeMultipart;
		try {
			mimeMultipart = new MimeMultipart(parts);
		} catch (MessagingException e) {
			return new Result<MimeMultipart>(false, null, e);
		}
		return new Result<MimeMultipart>(true, mimeMultipart, null);
	}

	public static Result<MimeBodyPart> createMimeBodyPart(DataHandler dataHandler) {
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		try {
			mimeBodyPart.setDataHandler(dataHandler);
		} catch (MessagingException e) {
			return new Result<MimeBodyPart>(false, null, e);
		}
		return new Result<MimeBodyPart>(true, mimeBodyPart, null);
	}

	// ==================================================
	// Void-Methoden

	public static Result<Void> openFolder(Folder folder, int arg0) {
		try {
			folder.open(arg0);
		} catch (MessagingException e) {
			return new Result<Void>(false, null, e);
		}
		return new Result<Void>(true, null, null);
	}

	public static Result<Void> closeByteArrayOutputStream(ByteArrayOutputStream baos) {
		try {
			baos.close();
		} catch (IOException e) {
			return new Result<Void>(false, null, e);
		}
		return new Result<Void>(true, null, null);
	}

	public static Result<Void> connectService(Service service, String username, String password) {
		try {
			service.connect(username, password);
		} catch (MessagingException e) {
			return new Result<Void>(false, null, e);
		}
		return new Result<Void>(true, null, null);
	}

	public static Result<Void> close(Service mailService, Folder emailFolder) {
		try {
			if (emailFolder != null && emailFolder.isOpen())
				emailFolder.close(false);
			if (mailService != null) {
				mailService.close();
				mailService = null;
			}
		} catch (MessagingException e) {
			return new Result<Void>(false, null, e);
		}
		return new Result<Void>(true, null, null);
	}

	public static Result<Void> sendMessage(Transport transport, MimeMessage mimeMessage) {
		try {
			transport.sendMessage(mimeMessage, mimeMessage.getRecipients(Message.RecipientType.TO));
		} catch (MessagingException e) {
			return new Result<Void>(true, null, e);
		}
		return new Result<Void>(true, null, null);
	}
}
