package tr.org.liderahenk.lider.log;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.persistence.dao.IOperationLogDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class OperationLogServiceImpl implements IOperationLogService {

	private IOperationLogDao logDao;
	private IEntityFactory entityFactory;
	private IResponseFactory responseFactory;
	
	@Override
	public IRestResponse list(String logMessage, String requestIp) {
		// Build search criteria
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		if (logMessage != null && !logMessage.isEmpty()) {
			propertiesMap.put("logMessage", logMessage);
		}
		if (requestIp != null && !requestIp.isEmpty()) {
			propertiesMap.put("requestIp", requestIp);
		}

		// Find desired agents
		List<? extends IOperationLog> operationLogs = logDao.findByProperties(IOperationLog.class, propertiesMap, null, null);

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap.put("operationLogs", operationLogs);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	@Override
	public IRestResponse get(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		IOperationLog operationLog = logDao.find(id);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("operationLog", operationLog);
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	@Override
	public IOperationLog saveLog(String userId, CrudType crudType, String message, byte[] requestData, String requestIp)
			throws Exception {
		IOperationLog log = entityFactory.createLog(userId, crudType, null, null, null, message, requestData,
				requestIp);
		log = logDao.save(log);
		return log;
	}

	@Override
	public IOperationLog saveTaskLog(String userId, CrudType crudType, Long taskId, String message, byte[] requestData,
			String requestIp) throws Exception {
		IOperationLog log = entityFactory.createLog(userId, crudType, taskId, null, null, message, requestData,
				requestIp);
		log = logDao.save(log);
		return log;
	}

	@Override
	public IOperationLog savePolicyLog(String userId, CrudType crudType, Long policyId, String message,
			byte[] requestData, String requestIp) throws Exception {
		IOperationLog log = entityFactory.createLog(userId, crudType, null, policyId, null, message, requestData,
				requestIp);
		log = logDao.save(log);
		return log;
	}

	@Override
	public IOperationLog saveProfileLog(String userId, CrudType crudType, Long profileId, String message,
			byte[] requestData, String requestIp) throws Exception {
		IOperationLog log = entityFactory.createLog(userId, crudType, null, null, profileId, message, requestData,
				requestIp);
		log = logDao.save(log);
		return log;
	}

	/*
	 * Service setters
	 */

	/**
	 * 
	 * @param logDao
	 */
	public void setLogDao(IOperationLogDao logDao) {
		this.logDao = logDao;
	}

	/**
	 * 
	 * @param entityFactory
	 */
	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	/**
	 * 
	 * @param responseFactory
	 */
	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

}
