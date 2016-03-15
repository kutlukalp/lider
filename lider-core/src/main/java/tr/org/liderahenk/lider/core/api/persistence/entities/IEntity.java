package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Base entity class for all entities.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IEntity extends Serializable {

	/**
	 * 
	 * @return record ID
	 */
	Long getId();

	/**
	 * 
	 * @return record creation date
	 */
	Date getCreateDate();

}
