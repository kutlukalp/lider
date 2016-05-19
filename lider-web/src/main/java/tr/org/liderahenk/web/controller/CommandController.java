package tr.org.liderahenk.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.processors.ICommandRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.web.controller.utils.ControllerUtils;

/**
 * Controller for command related operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Controller
@RequestMapping("/lider/command")
public class CommandController {

	private static Logger logger = LoggerFactory.getLogger(CommandController.class);

	@Autowired
	private IResponseFactory responseFactory;
	@Autowired
	private ICommandRequestProcessor commandProcessor;

	/**
	 * List executed tasks according to given parameters.
	 * 
	 * @param pluginName
	 * @param pluginVersion
	 * @param createDateRangeStart
	 * @param createDateRangeEnd
	 * @param status
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/task/list", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse listExecutedTasks(@RequestParam(value = "pluginName", required = false) String pluginName,
			@RequestParam(value = "pluginVersion", required = false) String pluginVersion,
			@RequestParam(value = "createDateRangeStart", required = false) Long createDateRangeStart,
			@RequestParam(value = "createDateRangeEnd", required = false) Long createDateRangeEnd,
			@RequestParam(value = "status", required = false) Integer status, HttpServletRequest request)
					throws UnsupportedEncodingException {
		logger.info(
				"Request received. URL: '/lider/command/task/list?pluginName={}&pluginVersion={}&createDateRangeStart={}&createDateRangeEnd={}&status={}'",
				new Object[] { pluginName, pluginVersion, createDateRangeStart, createDateRangeEnd, status });
		IRestResponse restResponse = commandProcessor.listExecutedTasks(pluginName, pluginVersion,
				createDateRangeStart != null ? new Date(createDateRangeStart) : null,
				createDateRangeEnd != null ? new Date(createDateRangeEnd) : null, status);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	/**
	 * Retrieve task command (with its child records) by specified task id.
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/task/{id:[\\d]+}/get", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse getTaskCommand(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/command/task/{}/get'", id);
		IRestResponse restResponse = commandProcessor.getTaskCommand(id);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	/**
	 * List executed policies according to given parameters.
	 * 
	 * @param pluginName
	 * @param pluginVersion
	 * @param createDateRangeStart
	 * @param createDateRangeEnd
	 * @param status
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/policy/list", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse listExecutedPolicies(@RequestParam(value = "label", required = false) String label,
			@RequestParam(value = "createDateRangeStart", required = false) Long createDateRangeStart,
			@RequestParam(value = "createDateRangeEnd", required = false) Long createDateRangeEnd,
			@RequestParam(value = "status", required = false) Integer status, HttpServletRequest request)
					throws UnsupportedEncodingException {
		logger.info(
				"Request received. URL: '/lider/command/policy/list?label={}&createDateRangeStart={}&createDateRangeEnd={}&status={}'",
				new Object[] { label, createDateRangeStart, createDateRangeEnd, status });
		IRestResponse restResponse = commandProcessor.listExecutedPolicies(label,
				createDateRangeStart != null ? new Date(createDateRangeStart) : null,
				createDateRangeEnd != null ? new Date(createDateRangeEnd) : null, status);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	/**
	 * Retrieve policy command (with its child records) by specified policy id.
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/policy/{id:[\\d]+}/get", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse getPolicyCommand(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/command/policy/{}/get'", id);
		IRestResponse restResponse = commandProcessor.getPolicyCommand(id);
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
		return ControllerUtils.handleAllException(e, responseFactory);
	}

}
