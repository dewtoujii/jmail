package jmail;

import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
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
        System.out.println("Username: ");
        String username = input.nextLine();
        System.out.println("Password:");
        String pw = input.nextLine();
        MailSender sender = new MailSender("localhost", username, pw, username);
        
        System.out.println("Recipient: ");
        String recipient = input.nextLine();
        System.out.println("Subject: ");
        String subject = input.nextLine();
        System.out.println("Text: ");
        String content = input.nextLine();
        System.out.println("Sending...");
        try {
            sender.send(recipient, subject, content);
        } catch (MessagingException ex) {
            Logger.getLogger(MailSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Sent");
    }

    public void send(String recipient, String subject, String content) throws NoSuchProviderException, MessagingException {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", hostname);
        props.put("mail.smtp.auth", "true");

        Authenticator auth = new SMTPAuthenticator();
        
  
                
        
        
        Session mailSession = Session.getDefaultInstance(props, auth);
//        Session mailSession = Session.getDefaultInstance(props);
        
        // uncomment for debugging infos to stdout
        // mailSession.setDebug(true);
        Transport transport = mailSession.getTransport();

        MimeMessage message = new MimeMessage(mailSession);
        message.setContent(content, "text/plain");
        message.setSubject(subject);
        message.setFrom(new InternetAddress(emailAddress));
        message.addRecipient(Message.RecipientType.TO,
             new InternetAddress(recipient));

        transport.connect();
        transport.sendMessage(message,
            message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    private class SMTPAuthenticator extends Authenticator {
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
           return new PasswordAuthentication(username, pw);
        }
    }
}