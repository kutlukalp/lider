package tr.org.liderahenk.web.controller;

import java.io.UnsupportedEncodingException;

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
import tr.org.liderahenk.lider.core.api.rest.processors.IReportRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.web.controller.utils.ControllerUtils;

/**
 * Controller for report related operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Controller
@RequestMapping("/lider/report")
public class ReportController {

	private static Logger logger = LoggerFactory.getLogger(ReportController.class);

	@Autowired
	private IResponseFactory responseFactory;
	@Autowired
	private IReportRequestProcessor reportProcessor;

	/**
	 * Generate report JSON from provided template ID.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/generate", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse generateReport(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/report/generate' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = reportProcessor.generate(requestBodyDecoded);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	/**
	 * Validate provided template.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/validate", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse validateTemplate(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/report/validate' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = reportProcessor.validate(requestBodyDecoded);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	/**
	 * Create new template.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/add", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse addTemplate(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/report/add' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = reportProcessor.add(requestBodyDecoded);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	/**
	 * Update given template.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse updateTemplate(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/report/update' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = reportProcessor.update(requestBodyDecoded);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	/**
	 * List policies according to given parameters.
	 * 
	 * @param label
	 * @param active
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/list", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse listPolicies(@RequestParam(value = "name", required = false) String name,
			HttpServletRequest request) throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/report/list?name={}'", name);
		IRestResponse restResponse = reportProcessor.list(name);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	/**
	 * Retrieve template specified by id
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/{id:[\\d]+}/get", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse getTemplate(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/report/{}/get'", id);
		IRestResponse restResponse = reportProcessor.get(id);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	/**
	 * Delete template specified by id.
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/{id:[\\d]+}/delete", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse deleteTemplate(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/report/{}/delete'", id);
		IRestResponse restResponse = reportProcessor.delete(id);
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
