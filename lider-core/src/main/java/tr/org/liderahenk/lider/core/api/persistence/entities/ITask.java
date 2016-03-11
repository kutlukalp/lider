package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * ITask entity class is responsible for storing task records.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface ITask extends Serializable {

	Long getId();

	IPlugin getPlugin();

	String getCommandClsId();

	byte[] getParameterMap();

	boolean isDeleted();

	Date getCreateDate();

	Date getModifyDate();

	String toJson();

}
