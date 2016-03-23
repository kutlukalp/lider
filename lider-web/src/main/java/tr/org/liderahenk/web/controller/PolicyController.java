package tr.org.liderahenk.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.IPolicyRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

@Controller
@RequestMapping("/lider/policy")
public class PolicyController {

	private static Logger logger = LoggerFactory.getLogger(PolicyController.class);

	@Autowired
	private IResponseFactory responseFactory;
	@Autowired
	private IPolicyRequestProcessor policyProcessor;

	@RequestMapping(value = "/execute", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse executePolicy(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/policy/execute' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = policyProcessor.execute(requestBodyDecoded);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	@RequestMapping(value = "/add", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse addPolicy(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/policy/add' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = policyProcessor.add(requestBodyDecoded);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse updatePolicy(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/policy/update' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = policyProcessor.update(requestBodyDecoded);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse listPolicies(@RequestParam(value = "label", required = false) String label,
			@RequestParam(value = "active", required = false, defaultValue = "true") Boolean active,
			HttpServletRequest request) throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/policy/list?label={}&active={}'", new Object[] { label, active });
		IRestResponse restResponse = policyProcessor.list(label, active);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	@RequestMapping(value = "/{id:[\\d]+}/get", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse getPolicy(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/policy/{}/get'", id);
		IRestResponse restResponse = policyProcessor.get(id);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	@RequestMapping(value = "/{id:[\\d]+}/delete", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse deletePolicy(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/policy/{}/delete'", id);
		IRestResponse restResponse = policyProcessor.delete(id);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	/**
	 * Handle predefined exceptions that we did not write and did not throw.
	 * 
	 * @param e
	 * @return IRestResponse instance which holds exception message with ERROR
	 *         status
	 */
	@ExceptionHandler(Exception.class)
	public IRestResponse handleAllException(Exception e) {
		logger.error(e.getMessage(), e);
		IRestResponse restResponse = responseFactory.createResponse(RestResponseStatus.ERROR,
				"Error: " + e.getMessage());
		return restResponse;
	}

	/**
	 * Decode given request body as UTF-8 string.
	 * 
	 * @param requestBody
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String decodeRequestBody(String requestBody) throws UnsupportedEncodingException {
		return URLDecoder.decode(requestBody, "UTF-8");
	}

}
