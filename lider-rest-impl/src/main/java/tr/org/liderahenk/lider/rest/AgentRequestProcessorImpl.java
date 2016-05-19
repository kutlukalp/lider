package tr.org.liderahenk.lider.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.IAgentRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * Processor class for handling/processing agent data.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class AgentRequestProcessorImpl implements IAgentRequestProcessor {

	private static Logger logger = LoggerFactory.getLogger(AgentRequestProcessorImpl.class);

	private IAgentDao agentDao;
	private IResponseFactory responseFactory;

	@Override
	public IRestResponse list(String hostname, String dn) {
		// Build search criteria
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		if (hostname != null && !hostname.isEmpty()) {
			propertiesMap.put("hostname", hostname);
		}
		if (dn != null && !dn.isEmpty()) {
			propertiesMap.put("version", dn);
		}

		// Find desired agents
		List<? extends IAgent> agents = agentDao.findByProperties(IAgent.class, propertiesMap, null, null);

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			resultMap.put("agents", mapper.writeValueAsString(agents));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	@Override
	public IRestResponse get(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		IAgent agent = agentDao.find(id);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("agent", agent.toJson());
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	public void setAgentDao(IAgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

}
