package tr.org.liderahenk.lider.core.api.mail;

import java.util.List;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IMailService {

	/**
	 * Send mail to provided list.
	 * 
	 * @param toList
	 * @param subject
	 * @param body
	 */
	void sendMail(List<String> toList, String subject, String body);

}
