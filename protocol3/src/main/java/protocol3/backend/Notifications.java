package protocol3.backend;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Notifications
{
	public static void send(String sub, String msg)
	{
		// Get properties object
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator()
		{
			@Override
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication("avas.mod@gmail.com", "Gizmoandpepper1");
			}
		});
		try
		{
			MimeMessage message = new MimeMessage(session);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("5867039701@tmomail.net"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("4056338347@tmomail.net"));
			message.setSubject(sub);
			message.setText(msg);
			Transport.send(message);
		} catch (MessagingException e)
		{
			throw new RuntimeException(e);
		}

	}
}
