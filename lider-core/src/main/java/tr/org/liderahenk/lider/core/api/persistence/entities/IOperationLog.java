package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Date;

import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;

public interface IOperationLog extends IEntity {
	Date getDate();

	String getUserId();

	String getPluginId();

	String getTaskId();

	String getAction();

	String getClientCN();

	String getServerIp();

	String getResultCode();

	String getLogText();

	String getChecksum();

	CrudType getCrudType();

	Boolean getActive();

	Date getChangedDate();

	Date getCreationDate();

	String getName();

	Integer getVersion();
}
