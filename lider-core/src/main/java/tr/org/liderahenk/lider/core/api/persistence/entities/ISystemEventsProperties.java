package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.io.Serializable;

public interface ISystemEventsProperties extends Serializable {

	Long getSystemEventPropertyId();

	/**
	 * 
	 * @return
	 */
	String getParamName();

	/**
	 * 
	 * @return
	 */
	String getParamValue();

	Integer getSystemEventsId();
}
