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
import org.springframework.web.bind.annotation.ResponseBody;

import tr.org.liderahenk.lider.core.api.rest.IRestRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.IRestResponse;

/**
 * Main controller for all REST requests made by Lider Console.
 * 
 * @author Emre Akkaya <emre.akkaya@agem.com.tr>
 *
 */
@Controller
public class LiderRequestManager {

	private static Logger logger = LoggerFactory.getLogger(LiderRequestManager.class);

	@Autowired
	private IRestRequestProcessor processor;

	@RequestMapping(value = "/lider/rest", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse processRequest(@RequestBody String requestBody, HttpServletRequest request) throws UnsupportedEncodingException {

		String requestBodyDecoded = URLDecoder.decode(requestBody, "UTF-8");
		logger.debug("Request received. Body: {}", requestBodyDecoded);

		IRestResponse restResponse = processor.processRequest(requestBodyDecoded);

		logger.info("Completed processing request, returning result.");

		return restResponse;
	}

}
