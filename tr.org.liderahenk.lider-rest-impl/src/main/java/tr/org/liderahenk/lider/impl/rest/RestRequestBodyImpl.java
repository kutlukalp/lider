package tr.org.liderahenk.lider.impl.rest;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.rest.IRestRequestBody;

/**
 * Default implementation for {@link IRestRequestBody}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class RestRequestBodyImpl implements IRestRequestBody {
	
	private static transient Logger log = LoggerFactory.getLogger(RestRequestBodyImpl.class);
	
	private String pluginId;
	private String pluginVersion;
	private String clientId;
	private String clientVersion;
	private Integer priority;
	
	private Map<String, Object> customParameterMap = new HashMap<String, Object>(0);
	private String requestId;
	private boolean scheduled;
	private ScheduleRequestImpl scheduleRequest;
	
	
	public RestRequestBodyImpl( ) {
	}

	public RestRequestBodyImpl(IRestRequestBody body) {
		this.clientId = body.getClientId();
		this.clientVersion = body.getClientVersion();
		this.customParameterMap = body.getCustomParameterMap();
		this.pluginId = body.getPluginId();
		this.pluginVersion = body.getPluginVersion();
		this.requestId = body.getRequestId();
		this.scheduled = body.isScheduled();
		this.scheduleRequest = (body.getScheduleRequest()==null) ? null : new ScheduleRequestImpl( body.getScheduleRequest());
		this.priority = body.getPriority();
	}

	@Override
	public String getPluginId() {
		return pluginId;
	}

	@Override
	public String getPluginVersion() {
		return pluginVersion;
	}

	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public String getClientVersion() {
		return clientVersion;
	}

	@Override
	public Map<String, Object> getCustomParameterMap() {
		return customParameterMap;
	}
	
	@Override
	public String getRequestId() {
		return requestId;
	}
	
	@Override
	public ScheduleRequestImpl getScheduleRequest() {
		return scheduleRequest;
	}
	
	public RestRequestBodyImpl fromJson( String json ){
		
		ObjectMapper mapper = new ObjectMapper(); 
		try {
			return mapper.readValue(json, RestRequestBodyImpl.class);
		} catch (Exception e) {
			log.error("error parsing request body, will assume empty: {}", json);
		}
		return new RestRequestBodyImpl();
	}

	@Override
	public boolean isScheduled() {
		return scheduled;
	}
	
	
	
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public void setCustomParameterMap(Map<String, Object> customParameterMap) {
		this.customParameterMap = customParameterMap;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}

	public void setScheduleRequest(ScheduleRequestImpl scheduleRequest) {
		this.scheduleRequest =  scheduleRequest;
	}
	
	@Override
	public Integer getPriority() {
		return priority;
	}
	
	public void setPriority(Integer priority) {
		this.priority = priority;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 3329435461350839639L;

}
