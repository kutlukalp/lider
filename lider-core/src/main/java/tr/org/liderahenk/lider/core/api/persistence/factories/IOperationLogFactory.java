package tr.org.liderahenk.lider.core.api.persistence.factories;

import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;

public interface IOperationLogFactory {

	IOperationLog createLog(String userId, CrudType crudType, Long taskId, Long policyId, Long profileId,
			String message, byte[] requestData, String requestIp);

}
