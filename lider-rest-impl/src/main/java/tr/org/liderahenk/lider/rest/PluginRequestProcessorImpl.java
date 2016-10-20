package tr.org.liderahenk.lider.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.dao.IPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.IPluginRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * Processor class for handling/processing plugin data.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class PluginRequestProcessorImpl implements IPluginRequestProcessor {

	private static Logger logger = LoggerFactory.getLogger(PluginRequestProcessorImpl.class);

	private IPluginDao pluginDao;
	private IResponseFactory responseFactory;

	@Override
	public IRestResponse list(String name, String version) {
		// Build search criteria
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("deleted", false);
		if (name != null && !name.isEmpty()) {
			propertiesMap.put("name", name);
		}
		if (version != null && !version.isEmpty()) {
			propertiesMap.put("version", version);
		}

		// Find desired policies
		List<? extends IPlugin> plugins = pluginDao.findByProperties(IPlugin.class, propertiesMap, null, null);

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap.put("plugins", plugins);
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
		IPlugin plugin = pluginDao.find(new Long(id));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("plugin", plugin);
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	public void setPluginDao(IPluginDao pluginDao) {
		this.pluginDao = pluginDao;
	}

	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

}
