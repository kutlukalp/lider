package tr.org.liderahenk.lider.core.api.messaging.messages;

/**
 * IAgreementStatusMessage is used to send agreement acceptance or decline
 * status to Lider.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IAgreementStatusMessage extends IAgentMessage {

	/**
	 * 
	 * @return true if the agreement is accepted, false otherwise.
	 */
	boolean isAccepted();

	/**
	 * 
	 * @return username who accepted/declined the agreement.
	 */
	String getUsername();

	/**
	 * 
	 * @return MD5 of the agreement document.
	 */
	String getMd5();

}
