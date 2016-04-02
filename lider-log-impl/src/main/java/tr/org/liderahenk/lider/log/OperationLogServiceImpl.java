package tr.org.liderahenk.lider.log;

import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.persistence.dao.IOperationLogDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class OperationLogServiceImpl implements IOperationLogService {

	private IOperationLogDao logDao;
	private IEntityFactory entityFactory;

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

}
