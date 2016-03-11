package tr.org.liderahenk.lider.core.api.persistence.dao;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;

/**
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface IOperationLogDao {
	
	/**
	 * 
	 * @param date
	 * @param userId
	 * @param pluginId
	 * @param taskId
	 * @param action
	 * @param serverIp
	 * @param resultCode
	 * @param logText
	 * @throws IOException 
	 * @throws SecurityException 
	 */
 	IOperationLog createLog(Long id, Date date, String  userId, String  pluginId, String  taskId, String action, String serverIp, String resultCode,
			String logText, String checksum, CrudType crudType, String clientCN) throws SecurityException, IOException;
	
 	/**
 	 * 
 	 * @param userId
 	 * @param maxResults
 	 * @return
 	 */
	List<? extends IOperationLog> getLogsByUserId ( String userId, int maxResults );
	
	/**
	 * 
	 * @param crudType
	 * @param maxResults
	 * @return
	 */
	List<? extends IOperationLog> getLogsByCrudType(CrudType crudType, int maxResults );
	
	/**
	 * 
	 * @param clientCN
	 * @param maxResults
	 * @return
	 */
	List<? extends IOperationLog> getLogsByClientCN (String clientCN, int maxResults );
	
	/**
	 * 
	 * @param resultCode
	 * @param maxResults
	 * @return
	 */
	List<? extends IOperationLog> getLogsByResultCode (String resultCode, int maxResults );
	
	/**
	 * 
	 * @param pluginId
	 * @param maxResults
	 * @return
	 */
	List<? extends IOperationLog> getLogsByPluginId (String pluginId, int maxResults );
	
	/**
	 * 
	 * @param taskId
	 * @param maxResults
	 * @return
	 */
	List<? extends IOperationLog> getLogsByTaskId (String taskId, int maxResults );
	
	/**
	 * 
	 * @param startDate
	 * @param finishDate
	 * @param maxResults
	 * @return
	 */
	List<? extends IOperationLog> getLogsByDate (Date startDate, Date finishDate, int maxResults );
	
	/**
	 * 
	 * @param freeText
	 * @param maxResults
	 * @return
	 */
	List<? extends IOperationLog> getLogsByText (String freeText, int maxResults );
	
	/**
	 * 
	 * @param path
	 * @throws IOException
	 */
	void fileToDB(String path) throws IOException;

	/**
	 * 
	 * @param log
	 * @return
	 */
	IOperationLog update(IOperationLog log);
	
	/**
	 * 
	 * @param maxResults
	 * @return
	 */
	List<? extends IOperationLog> getAllLogs( Integer maxResults );
	
	/**
	 * 
	 * @param params
	 * @param maxResults
	 * @return
	 */
	List<? extends IOperationLog> getLogsBy(Map<String, Object> params, Integer maxResults);
	
	/**
	 * 
	 * @param logCriterias
	 * @param offset
	 * @param maxResults
	 * @param check
	 * @return
	 * @throws Exception
	 */
//	List<? extends IOperationLog> find(IQueryCriteria[] logCriterias, int offset, int maxResults, boolean check) throws Exception;
	
	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<? extends IOperationLog> getLogsByDate(Long startDate, Long endDate);
		
}
