package tr.org.liderahenk.lider.core.api.persistence.entities;

/**
 * IAgreementStatus entity class is responsible for storing user's
 * acceptance/decline of agreement document.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IAgreementStatus extends IEntity {

	IAgent getAgent();

	String getUsername();

	String getMd5();

	boolean isAccepted();

}
