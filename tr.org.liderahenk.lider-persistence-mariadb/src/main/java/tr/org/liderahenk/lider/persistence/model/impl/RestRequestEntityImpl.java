package tr.org.liderahenk.lider.persistence.model.impl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
import tr.org.liderahenk.lider.core.api.rest.Priority;
import tr.org.liderahenk.lider.core.api.rest.RestDNType;

/**
 * Entity class for IRestRequest objects.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.impl.rest.RestRequestImpl
 *
 */
@Embeddable
public class RestRequestEntityImpl implements IRestRequest {

	private static final long serialVersionUID = -8172658877775426084L;

	@Lob
	@Column(name = "DN_LIST")
	private String dnListJsonString;

	@Enumerated(EnumType.STRING)
	@Column(name = "DN_TYPE")
	private RestDNType dnType;

	@Column(name = "PLUGIN_NAME")
	private String pluginName;

	@Column(name = "PLUGIN_VERSION")
	private String pluginVersion;

	@Column(name = "COMMAND_ID")
	private String commandId;

	@Lob
	@Column(name = "PARAMETER_MAP")
	private String parameterMapJsonString;

	@Column(name = "CRON_EXPRESSION")
	private String cronExpression;

	@Enumerated(EnumType.STRING)
	@Column(name = "PRIORITY")
	private Priority priority;

	public RestRequestEntityImpl() {
		super();
	}

	public RestRequestEntityImpl(List<String> dnList, RestDNType dnType, String pluginName, String pluginVersion,
			String commandId, Map<String, Object> parameterMap, String cronExpression, Priority priority)
					throws JsonGenerationException, JsonMappingException, IOException {
		super();
		ObjectMapper mapper = new ObjectMapper();
		this.dnListJsonString = mapper.writeValueAsString(dnList);
		this.dnType = dnType;
		this.pluginName = pluginName;
		this.pluginVersion = pluginVersion;
		this.commandId = commandId;
		this.parameterMapJsonString = mapper.writeValueAsString(parameterMap);
		this.cronExpression = cronExpression;
		this.priority = priority;
	}

	public RestRequestEntityImpl(IRestRequest request)
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		this.dnListJsonString = mapper.writeValueAsString(request.getDnList());
		this.dnType = request.getDnType();
		this.pluginName = request.getPluginName();
		this.pluginVersion = request.getPluginVersion();
		this.commandId = request.getCommandId();
		this.parameterMapJsonString = mapper.writeValueAsString(request.getParameterMap());
		this.cronExpression = request.getCronExpression();
		this.priority = request.getPriority();
	}

	@Transient
	public List<String> getDnList() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(dnListJsonString, new TypeReference<ArrayList<String>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public RestDNType getDnType() {
		return dnType;
	}

	public void setDnType(RestDNType dnType) {
		this.dnType = dnType;
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public String getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	@Transient
	public Map<String, Object> getParameterMap() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(parameterMapJsonString, new TypeReference<HashMap<String, Object>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public String getDnListJsonString() {
		return dnListJsonString;
	}

	public void setDnListJsonString(String dnListJsonString) {
		this.dnListJsonString = dnListJsonString;
	}

	public String getParameterMapJsonString() {
		return parameterMapJsonString;
	}

	public void setParameterMapJsonString(String parameterMapJsonString) {
		this.parameterMapJsonString = parameterMapJsonString;
	}

}