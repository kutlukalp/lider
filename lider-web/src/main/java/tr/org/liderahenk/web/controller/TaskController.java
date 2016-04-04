package tr.org.liderahenk.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

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
import tr.org.liderahenk.lider.core.api.rest.processors.ITaskRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * Controller for task related operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Controller
@RequestMapping("/lider/task")
public class TaskController {

	private static Logger logger = LoggerFactory.getLogger(TaskController.class);

	@Autowired
	private IResponseFactory responseFactory;
	@Autowired
	private ITaskRequestProcessor taskProcessor;

	@RequestMapping(value = "/execute", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse executeTask(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/task/execute' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = taskProcessor.execute(requestBodyDecoded);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse listTasks(@RequestParam(value = "pluginName", required = false) String pluginName,
			@RequestParam(value = "pluginVersion", required = false) String pluginVersion,
			@RequestParam(value = "createDateRangeStart", required = false) Date createDateRangeStart,
			@RequestParam(value = "createDateRangeEnd", required = false) Date createDateRangeEnd,
			@RequestParam(value = "status", required = false) Integer status, HttpServletRequest request)
					throws UnsupportedEncodingException {
		logger.info(
				"Request received. URL: '/lider/task/list?pluginName={}&pluginVersion={}&createDateRangeStart={}&createDateRangeEnd={}&status={}'",
				new Object[] { pluginName, pluginVersion, createDateRangeStart, createDateRangeEnd, status });
		IRestResponse restResponse = taskProcessor.list(pluginName, pluginVersion, createDateRangeStart,
				createDateRangeEnd, status);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	@RequestMapping(value = "/{id:[\\d]+}/get", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse getTask(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/task/{}/get'", id);
		IRestResponse restResponse = taskProcessor.get(id);
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
