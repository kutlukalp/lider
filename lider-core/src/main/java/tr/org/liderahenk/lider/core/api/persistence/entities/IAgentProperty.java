package tr.org.liderahenk.lider.core.api.persistence.entities;

/**
 * IAgentProperty provides properties of a referenced agent.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IAgentProperty extends IEntity {

	/**
	 * 
	 * @return
	 */
	IAgent getAgent();

	/**
	 * 
	 * @return
	 */
	String getPropertyName();

	/**
	 * 
	 * @return
	 */
	String getPropertyValue();

}
