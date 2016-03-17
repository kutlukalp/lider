package tr.org.liderahenk.lider.core.api.log;

import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;

public interface IOperationLogService {

	/**
	 * 
	 * @param userId
	 * @param crudType
	 * @param message
	 * @param requestData
	 * @param requestIp
	 * @return
	 * @throws Exception
	 */
	IOperationLog saveLog(String userId, CrudType crudType, String message, byte[] requestData, String requestIp)
			throws Exception;

	/**
	 * 
	 * @param userId
	 * @param crudType
	 * @param taskId
	 * @param message
	 * @param requestData
	 * @param requestIp
	 * @return
	 * @throws Exception
	 */
	IOperationLog saveTaskLog(String userId, CrudType crudType, Long taskId, String message, byte[] requestData,
			String requestIp) throws Exception;

	/**
	 * 
	 * @param userId
	 * @param crudType
	 * @param policyId
	 * @param message
	 * @param requestData
	 * @param requestIp
	 * @return
	 * @throws Exception
	 */
	IOperationLog savePolicyLog(String userId, CrudType crudType, Long policyId, String message, byte[] requestData,
			String requestIp) throws Exception;

	/**
	 * 
	 * @param userId
	 * @param crudType
	 * @param profileId
	 * @param message
	 * @param requestData
	 * @param requestIp
	 * @return
	 * @throws Exception
	 */
	IOperationLog saveProfileLog(String userId, CrudType crudType, Long profileId, String message, byte[] requestData,
			String requestIp) throws Exception;

}
