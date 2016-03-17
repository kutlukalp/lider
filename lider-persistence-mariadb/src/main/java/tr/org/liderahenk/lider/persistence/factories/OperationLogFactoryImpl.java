package tr.org.liderahenk.lider.persistence.factories;

import java.util.Date;

import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;
import tr.org.liderahenk.lider.core.api.persistence.factories.IOperationLogFactory;
import tr.org.liderahenk.lider.persistence.entities.OperationLogImpl;

public class OperationLogFactoryImpl implements IOperationLogFactory {

	@Override
	public IOperationLog createLog(String userId, CrudType crudType, Long taskId, Long policyId, Long profileId, String message,
			byte[] requestData, String requestIp) {
		return new OperationLogImpl(null, userId, crudType, taskId, policyId, profileId, message, requestData, requestIp,
				new Date());
	}

}
