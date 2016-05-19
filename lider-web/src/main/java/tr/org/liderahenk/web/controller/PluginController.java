package tr.org.liderahenk.web.controller;

import java.io.UnsupportedEncodingException;

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
import tr.org.liderahenk.lider.core.api.rest.processors.IPluginRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.web.controller.utils.ControllerUtils;

/**
 * Controller for plugin related operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Controller
@RequestMapping("/lider/plugin")
public class PluginController {

	private static Logger logger = LoggerFactory.getLogger(PluginController.class);

	@Autowired
	private IResponseFactory responseFactory;
	@Autowired
	private IPluginRequestProcessor pluginProcessor;

	/**
	 * List plugins according to given parameters.
	 * 
	 * @param label
	 * @param active
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/list", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse listPlugins(@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "version", required = false) String version, HttpServletRequest request)
					throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/plugin/list?name={}&version={}'", new Object[] { name, version });
		IRestResponse restResponse = pluginProcessor.list(name, version);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	/**
	 * Retrieve plugin specified by id
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/{id:[\\d]+}/get", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse getPlugin(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/plugin/{}/get'", id);
		IRestResponse restResponse = pluginProcessor.get(id);
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
