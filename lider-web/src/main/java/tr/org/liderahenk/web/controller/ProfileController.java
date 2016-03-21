package tr.org.liderahenk.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tr.org.liderahenk.lider.core.api.rest.processors.IProfileRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

@Controller
@RequestMapping("/lider/profile")
public class ProfileController {

	private static Logger logger = LoggerFactory.getLogger(ProfileController.class);

	@Autowired
	private IProfileRequestProcessor profileProcessor;
	
	@RequestMapping(value = "/execute", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse executeProfile(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/profile/execute' Body: {}", requestBody);
		IRestResponse restResponse = profileProcessor.execute(requestBody);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	@RequestMapping(value = "/add", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse addProfile(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/profile/add' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = profileProcessor.add(requestBodyDecoded);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse updateProfile(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/profile/update' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = profileProcessor.update(requestBodyDecoded);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse listProfiles(@RequestParam(value = "pluginName", required = true) String pluginName,
			@RequestParam(value = "pluginVersion", required = true) String pluginVersion,
			@RequestParam(value = "label", required = false) String label,
			@RequestParam(value = "active", required = false, defaultValue = "true") Boolean active,
			HttpServletRequest request) throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/profile/list?pluginName={}&pluginVersion={}&label={}&active={}'",
				new Object[] { pluginName, pluginVersion, label, active });
		IRestResponse restResponse = profileProcessor.list(pluginName, pluginVersion, label, active);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	@RequestMapping(value = "/{id:[\\d]+}/get", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse getProfile(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/profile/{}/get'", id);
		IRestResponse restResponse = profileProcessor.get(id);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	@RequestMapping(value = "/{id:[\\d]+}/delete", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse deleteProfile(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/profile/{}/delete'", id);
		IRestResponse restResponse = profileProcessor.delete(id);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
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
