package tr.org.liderahenk.lider.mail;

import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.mail.IMailService;

/**
 * This class works as a service providing convenience method for sending
 * e-mails. Any bundle/plugin can use it by including its property in the
 * blueprint.xml
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class MailServiceImpl implements IMailService {

	private Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

	private IConfigurationService configurationService;

	@Override
	public void sendMail(List<String> toList, String subject, String body) {
		if (toList == null || toList.isEmpty()) {
			throw new IllegalArgumentException("toList was null!");
		}

		logger.info("Sending mail to: {}, with subject: {} and body: {}", new Object[] { toList, subject, body });

		Properties properties;
		Session session;
		MimeMessage message;

		try {
			// Setup mail properties
			properties = System.getProperties();
			String mailPropertyPrefix = configurationService.getMailSmtpSslEnable() ? "mail.smtps." : "mail.smtp.";
			properties.put(mailPropertyPrefix + "port", configurationService.getMailSmtpPort().toString());
			properties.put(mailPropertyPrefix + "auth", configurationService.getMailSmtpAuth() ? "true" : "false");
			properties.put(mailPropertyPrefix + "starttls.enable",
					configurationService.getMailSmtpStartTlsEnable() ? "true" : "false");
			properties.put(mailPropertyPrefix + "connectiontimeout", configurationService.getMailSmtpConnTimeout());
			properties.put(mailPropertyPrefix + "timeout", configurationService.getMailSmtpTimeout());
			properties.put(mailPropertyPrefix + "writetimeout", configurationService.getMailSmtpWriteTimeout());
			properties.put(mailPropertyPrefix + "ssl.enable", configurationService.getMailSmtpSslEnable());
			logger.debug("Mail service properties have been setup.");

			// Setup mail sender & recipients
			session = Session.getDefaultInstance(properties, null);
			message = new MimeMessage(session);
			message.setFrom(new InternetAddress(configurationService.getMailAddress(), "Lider Ahenk Platform"));
			for (String recipient : toList) {
				message.addRecipient(Message.RecipientType.TO,
						new InternetAddress(recipient.toLowerCase(Locale.ENGLISH).trim()));
			}

			message.setSubject(subject);
			message.setContent(body, "text/plain; charset=ISO-8859-9");

			// Get SMTP transport
			Transport transport = session.getTransport("smtp");
			// Enter your correct GMail UserID and Password
			transport.connect(configurationService.getMailHost(), configurationService.getMailAddress(),
					configurationService.getMailPassword());
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
