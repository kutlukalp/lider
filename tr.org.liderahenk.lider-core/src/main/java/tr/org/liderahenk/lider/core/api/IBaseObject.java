package tr.org.liderahenk.lider.core.api;

import java.util.Date;

public interface IBaseObject {
	Long getId();

	String getName();

	Boolean getActive();

	Date getCreationDate();

	Date getChangedDate();

	Integer getVersion();
}
