package tr.org.liderahenk.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tr.org.liderahenk.lider.core.api.auth.IRegistrationInfo;
import tr.org.liderahenk.lider.core.api.authorization.IRegistrationService;

/**
 * Controller class which is used to register new agents.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Controller
@RequestMapping("/lider/agent")
public class AgentRegistrationController {

	private static Logger logger = LoggerFactory.getLogger(AgentRegistrationController.class);

	@Autowired
	private IRegistrationService registrationService;

	@RequestMapping(value = "/register", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRegistrationInfo register(@RequestParam(value = "agentId", required = true) String agentIdentifier,
			@RequestParam(value = "generatedPassword", required = true) String generatedPassword,
			@RequestParam(value = "useExistingEntry", defaultValue = "false", required = false) Boolean useExistingEntry,
			HttpServletRequest request) throws Exception {
		logger.info("agentId => {}, generatedPassword => {}, useExistingEntry => {}",
				new Object[] { agentIdentifier, generatedPassword, useExistingEntry });
		return registrationService.register(agentIdentifier, generatedPassword, useExistingEntry,
				request.getRemoteHost(), request.getRemoteAddr());
	}

	@RequestMapping(value = "/validate/credentials", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public void validateCredentials() throws Exception {
		return;
	}

}
