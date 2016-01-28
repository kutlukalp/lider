package tr.org.liderahenk.lider.impl.rest;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IRestRequest;

/**
 * Default implementation for {@link IRequestFactory}
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class RequestFactoryImpl implements IRequestFactory {

	private static Logger logger = LoggerFactory.getLogger(RequestFactoryImpl.class);

	@Override
	public IRestRequest createRequest(String json) throws Exception {
		logger.debug("[RequestFactoryImpl] {}", json);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, RestRequestImpl.class);
	}

}
