package tr.org.liderahenk.web.controller.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * Utility class for controllers.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author <a href="mailto:cemre.alpsoy@agem.com.tr">Cemre ALPSOY</a>
 *
 */
public class ControllerUtils {

	private static Logger logger = LoggerFactory.getLogger(ControllerUtils.class);
	/**
	 * Decode given request body as UTF-8 string.
	 * 
	 * @param requestBody
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String decodeRequestBody(String requestBody) throws UnsupportedEncodingException {
		return URLDecoder.decode(requestBody, "UTF-8");
	}

	/**
	 * Handle given exception by logging and creating error response.
	 * 
	 * @param e
	 * @param responseFactory
	 * @return
	 */
	public static IRestResponse handleAllException(Exception e, IResponseFactory responseFactory) {
		logger.error(e.getMessage(), e);
		IRestResponse restResponse = responseFactory.createResponse(RestResponseStatus.ERROR,
				"Error: " + e.getMessage());
		return restResponse;
	}
	
	public static boolean recordOperationLog(IOperationLogService operationLogService,CrudType crudType,String userId, String message,byte[] requestData,String requestIp, String logType,Long id){
		boolean result = false;
			try {
				if("log".equals(logType)){
					operationLogService.saveLog(userId, crudType, message, requestData, requestIp);
					result = true;
				}else if("taskLog".equals(logType)){
					operationLogService.saveTaskLog(userId, crudType, id, message, requestData, requestIp);
					result = true;
				}else if("profileLog".equals(logType)){
					operationLogService.saveProfileLog(userId, crudType, id, message, requestData, requestIp);
					result = true;
				}else if("policyLog".equals(logType)){
					operationLogService.savePolicyLog(userId, crudType, id, message, requestData, requestIp);
					result = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
			}
		return result;
	}
	
	public static boolean recordOperationLog(IOperationLogService operationLogService,IOperationLog operationLog, String logType){
		boolean result = false;
			try {
				String userId = operationLog.getUserId();
				CrudType crudType = operationLog.getCrudType();
				String message = operationLog.getLogMessage();
				byte[] requestData = operationLog.getRequestData();
				String requestIp = operationLog.getRequestIp();
				if("log".equals(logType)){
					operationLogService.saveLog(userId, crudType, message, requestData, requestIp);
					result = true;
				}else if("taskLog".equals(logType)){
					Long taskId = operationLog.getTaskId();
					operationLogService.saveProfileLog(userId, crudType, taskId, message, requestData, requestIp);
					result = true;
				}else if("profileLog".equals(logType)){
					Long profileId = operationLog.getProfileId();
					operationLogService.saveProfileLog(userId, crudType, profileId, message, requestData, requestIp);
					result = true;
				}else if("policyLog".equals(logType)){
					Long policyId = operationLog.getPolicyId();
					operationLogService.saveProfileLog(userId, crudType, policyId, message, requestData, requestIp);
					result = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
			}
		return result;
	}
}
