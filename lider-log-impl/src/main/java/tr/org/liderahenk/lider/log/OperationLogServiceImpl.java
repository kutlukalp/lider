package tr.org.liderahenk.lider.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.persistence.dao.IOperationLogDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;
import tr.org.liderahenk.lider.core.api.persistence.factories.IOperationLogFactory;

public class OperationLogServiceImpl implements IOperationLogService {

	private static Logger logger = LoggerFactory.getLogger(OperationLogServiceImpl.class);

	private IOperationLogDao logDao;
	private IOperationLogFactory logFactory;

	@Override
	public IOperationLog saveLog(String userId, CrudType crudType, String message, byte[] requestData, String requestIp)
			throws Exception {
		IOperationLog log = logFactory.createLog(userId, crudType, null, null, null, message, requestData, requestIp);
		log = logDao.save(log);
		return log;
	}

	@Override
	public IOperationLog saveTaskLog(String userId, CrudType crudType, Long taskId, String message, byte[] requestData,
			String requestIp) throws Exception {
		IOperationLog log = logFactory.createLog(userId, crudType, taskId, null, null, message, requestData, requestIp);
		log = logDao.save(log);
		return log;
	}

	@Override
	public IOperationLog savePolicyLog(String userId, CrudType crudType, Long policyId, String message,
			byte[] requestData, String requestIp) throws Exception {
		IOperationLog log = logFactory.createLog(userId, crudType, null, policyId, null, message, requestData,
				requestIp);
		log = logDao.save(log);
		return log;
	}

	@Override
	public IOperationLog saveProfileLog(String userId, CrudType crudType, Long profileId, String message,
			byte[] requestData, String requestIp) throws Exception {
		IOperationLog log = logFactory.createLog(userId, crudType, null, null, profileId, message, requestData,
				requestIp);
		log = logDao.save(log);
		return log;
	}

	/**
	 * 
	 * @param logDao
	 */
	public void setLogDao(IOperationLogDao logDao) {
		this.logDao = logDao;
	}

	/**
	 * 
	 * @param logFactory
	 */
	public void setLogFactory(IOperationLogFactory logFactory) {
		this.logFactory = logFactory;
	}

}
