package tr.org.liderahenk.lider.persistence.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

public class GetLdapSearchAttrCommand extends BaseCommand {

	private static Logger logger = LoggerFactory.getLogger(GetLdapSearchAttrCommand.class);

	private ICommandResultFactory resultFactory;
	private IConfigurationService configurationService;
	private IAgentDao agentDao;

	@Override
	public ICommandResult execute(ICommandContext context) throws Exception {
		List<String> attributes = new ArrayList<String>();
		// Read LDAP search attributes
		String searchAttributes = configurationService.getLdapSearchAttributes();
		if (searchAttributes != null) {
			attributes.addAll(Arrays.asList(searchAttributes.split(",")));
		}
		// Append agent properties
		List<String> propertyNames = agentDao.getPropertyNames();
		attributes.addAll(propertyNames);

		logger.debug("LDAP search attributes: {}", attributes);

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("attributes", attributes);
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this, resultMap);
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getCommandId() {
		return "GET-LDAP-SEARCH-ATTR";
	}

	@Override
	public Boolean executeOnAgent() {
		return false;
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public void setAgentDao(IAgentDao agentDao) {
		this.agentDao = agentDao;
	}

}
