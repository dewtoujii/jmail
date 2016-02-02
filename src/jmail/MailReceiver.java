package jmail;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import com.sun.mail.pop3.POP3Store;

public class MailReceiver {

    private final String hostname;
    private final String username;
    private final String pw;

    public MailReceiver(String hostname, String username, String pw) {
        this.hostname = hostname;
        this.username = username;
        this.pw = pw;
    }

    public static void main(String[] args) {
    	String username;
    	String pw;
    	if(args.length == 2) {
    		username = args[0];
    		pw = args[1];
    	}
    	else {
    		Scanner input = new Scanner(System.in);
            System.out.println("Username: ");
            username = input.nextLine();
            System.out.println("Password:");
            pw = input.nextLine();
    	}
    	
        try {
            new MailReceiver("localhost", username, pw).receive();
        } catch (IOException | MessagingException | ClassNotFoundException ex) {
            Logger.getLogger(MailReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void receive() throws IOException, MessagingException, ClassNotFoundException {

        Properties properties = new Properties();
        properties.put("mail.pop3.host", hostname);
        Session emailSession = Session.getDefaultInstance(properties);

        POP3Store emailStore = (POP3Store) emailSession.getStore("pop3");
        emailStore.connect(username, pw);

        Folder emailFolder = emailStore.getFolder("INBOX");
        emailFolder.open(Folder.READ_ONLY);

        Message[] messages = emailFolder.getMessages();
        for (int i = 0; i < messages.length; i++) {
            Message message = messages[i];
            
            JMailMessage jMailMessage = new JMailMessage((MimeMessage)message);
            
            System.out.println("==============================");
            System.out.println("Email #" + (i + 1));
            jMailMessage.print();
        }

        emailFolder.close(false);
        emailStore.close();

    }
}
