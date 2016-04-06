package tr.org.liderahenk.web.controller.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * Utility class for controllers.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
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

}
