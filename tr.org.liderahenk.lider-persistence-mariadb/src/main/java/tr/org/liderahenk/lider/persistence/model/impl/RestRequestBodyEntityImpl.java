package tr.org.liderahenk.lider.persistence.model.impl;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import tr.org.liderahenk.lider.core.api.rest.IRestRequestBody;
@Embeddable
public class RestRequestBodyEntityImpl implements IRestRequestBody{

	
	private String pluginId;
	private String pluginVersion;
	private String clientId;
	private String clientVersion;
	private Integer priority;
	
	@Column(length=8000)
	private String customParameterMapJSON;
	
	private String requestId;
	private boolean scheduled = false;
	
	private ScheduleRequestEntityImpl scheduleRequest;
	
	
	public RestRequestBodyEntityImpl() {
	}
	
	public RestRequestBodyEntityImpl(IRestRequestBody body) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		this.clientId = body.getClientId();
		this.clientVersion = body.getClientVersion();
		this.customParameterMapJSON = mapper.writeValueAsString(body.getCustomParameterMap());
//		this.pluginId = body.getPluginId();l
		this.pluginVersion = body.getPluginVersion();
		this.requestId = body.getRequestId();
		this.scheduled = body.isScheduled();
		this.scheduleRequest = (body.getScheduleRequest()==null) ? null : new ScheduleRequestEntityImpl( body.getScheduleRequest());
		this.priority = body.getPriority();
	}
	
	@Override
	@Transient
	public Map<String, Object> getCustomParameterMap() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(customParameterMapJSON, new TypeReference<HashMap<String,Object>>(){});
		} catch (Exception e) {
			// this should never happen
			//e.printStackTrace();
			return null;
		} 
	}
	
	public String getCustomParameterMapJSON() {
		return customParameterMapJSON;
	}
	
	public void setCustomParameterMapJSON(String customParameterMapJSON) {
		this.customParameterMapJSON = customParameterMapJSON;
	}
	public String getPluginId() {
		return pluginId;
	}
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
	public String getPluginVersion() {
		return pluginVersion;
	}
	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientVersion() {
		return clientVersion;
	}
	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public boolean isScheduled() {
		return scheduled;
	}
	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}
	public ScheduleRequestEntityImpl getScheduleRequest() {
		return scheduleRequest;
	}
	public void setScheduleRequest(ScheduleRequestEntityImpl scheduleRequest) {
		this.scheduleRequest = scheduleRequest;
	}
	
	@Override
	public Integer getPriority() {
		return this.priority;
	}
	
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
}
