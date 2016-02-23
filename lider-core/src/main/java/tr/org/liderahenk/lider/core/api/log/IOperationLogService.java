package tr.org.liderahenk.lider.core.api.log;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.enums.CrudType;

public interface IOperationLogService {

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
	 */
	IOperationLog createLog(Date date, String userId, String pluginId, String taskId, String action, String serverIp,
			String resultCode, String logText, CrudType crudType, String clientCN);

	List<? extends IOperationLog> getLogsByUserId(String userId, int maxResults);

	List<? extends IOperationLog> getLogsByCrudType(CrudType crudType, int maxResults);

	List<? extends IOperationLog> getLogsByClientCN(String clientCN, int maxResults);

	List<? extends IOperationLog> getLogsByResultCode(String resultCode, int maxResults);

	List<? extends IOperationLog> getLogsByPluginId(String pluginId, int maxResults);

	List<? extends IOperationLog> getLogsByTaskId(String taskId, int maxResults);

	List<? extends IOperationLog> getLogsByDate(Date startDate, Date finishDate, int maxResults);

	List<? extends IOperationLog> getLogsByText(String freeText, int maxResults);

	void fileToDB(String path) throws IOException;

	String getChecksum(IOperationLog log);

	List<? extends IOperationLog> getAllLogs(Integer maxResults);

	List<? extends IOperationLog> getLogsBy(Map<String, Object> params, Integer maxResults);

	void importAgentLogLines(List<String> lines);

	List<? extends IOperationLog> getLogsByDate(Long startDate, Long endDate);
}
