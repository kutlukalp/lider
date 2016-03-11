package tr.org.liderahenk.lider.log;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.persistence.dao.IOperationLogDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;

public class OperationLogServiceImpl implements IOperationLogService {

	private static Logger LOG = LoggerFactory.getLogger(OperationLogServiceImpl.class);

	private IOperationLogDao logDao;

	public void setLogDao(IOperationLogDao logDao) {
		this.logDao = logDao;
	}

	@Override
	public void importAgentLogLines(List<String> lines) {
	}

	@Override
	public IOperationLog createLog(Date date, String userId, String pluginId, String taskId, String action,
			String serverIp, String resultCode, String logText, CrudType crudType, String clientCN) {

		try {
			IOperationLog log = logDao.createLog(null, date, userId, pluginId, taskId, action, serverIp, resultCode,
					logText, "", crudType, clientCN);

			IOperationLog logWithChecksum = logDao.createLog(log.getId(), date, userId, pluginId, taskId, action,
					serverIp, resultCode, logText, null, crudType, clientCN);

			return logWithChecksum;
		} catch (Exception e) {
			LOG.error("error creating mys log: ", e.getMessage());
			LOG.debug("error creating mys log: ", e);
			return null;
		}
	}

	@Override
	public List<? extends IOperationLog> getLogsByUserId(String userId, int maxResults) {
		return logDao.getLogsByUserId(userId, maxResults);
	}

	@Override
	public List<? extends IOperationLog> getLogsByCrudType(CrudType crudType, int maxResults) {
		return logDao.getLogsByCrudType(crudType, maxResults);
	}

	@Override
	public void fileToDB(String path) throws IOException {
		logDao.fileToDB(path);
	}

	@Override
	public List<? extends IOperationLog> getLogsByClientCN(String clientCN, int maxResults) {
		return logDao.getLogsByClientCN(clientCN, maxResults);
	}

	@Override
	public List<? extends IOperationLog> getLogsByResultCode(String resultCode, int maxResults) {
		return logDao.getLogsByResultCode(resultCode, maxResults);
	}

	@Override
	public List<? extends IOperationLog> getLogsByPluginId(String pluginId, int maxResults) {
		return logDao.getLogsByPluginId(pluginId, maxResults);
	}

	@Override
	public List<? extends IOperationLog> getLogsByTaskId(String taskId, int maxResults) {
		return logDao.getLogsByTaskId(taskId, maxResults);
	}

	@Override
	public List<? extends IOperationLog> getLogsByDate(Date startDate, Date finishDate, int maxResults) {
		return logDao.getLogsByDate(startDate, finishDate, maxResults);
	}

	@Override
	public List<? extends IOperationLog> getLogsByText(String freeText, int maxResults) {
		return logDao.getLogsByText(freeText, maxResults);
	}

	@Override
	public String getChecksum(IOperationLog log) {
		String checksum = "";
		return checksum;
	}

	@Override
	public List<? extends IOperationLog> getAllLogs(Integer maxResults) {
		return logDao.getAllLogs(maxResults);
	}

	@Override
	public List<? extends IOperationLog> getLogsBy(Map<String, Object> params, Integer maxResults) {
		return logDao.getLogsBy(params, maxResults);
	}

	@Override
	public List<? extends IOperationLog> getLogsByDate(Long startDate, Long endDate) {
		return logDao.getLogsByDate(startDate, endDate);
	}
}
