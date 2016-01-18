package tr.org.liderahenk.lider.core.api.log;

import java.util.Date;

import tr.org.liderahenk.lider.core.api.IBaseObject;
import tr.org.liderahenk.lider.core.api.enums.CrudType;

public interface IOperationLog extends IBaseObject{
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
}
