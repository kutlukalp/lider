package tr.org.liderahenk.lider.persistence.model.impl;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

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

	@Column(name = "DN_LIST")
	private List<String> dnList;

	@Enumerated(EnumType.STRING)
	@Column(name = "DN_TYPE")
	private RestDNType dnType;

	@Column(name = "PLUGIN_NAME")
	private String pluginName;

	@Column(name = "PLUGIN_VERSION")
	private String pluginVersion;

	@Column(name = "COMMAND_ID")
	private String commandId;

	@Column(name = "PARAMETER_MAP")
	private Map<String, Object> parameterMap;

	@Column(name = "CRON_EXPRESSION")
	private String cronExpression;

	@Enumerated(EnumType.STRING)
	@Column(name = "PRIORITY")
	private Priority priority;

	public RestRequestEntityImpl() {
		super();
	}

	public RestRequestEntityImpl(List<String> dnList, RestDNType dnType, String pluginName, String pluginVersion,
			String commandId, Map<String, Object> parameterMap, String cronExpression, Priority priority) {
		super();
		this.dnList = dnList;
		this.dnType = dnType;
		this.pluginName = pluginName;
		this.pluginVersion = pluginVersion;
		this.commandId = commandId;
		this.parameterMap = parameterMap;
		this.cronExpression = cronExpression;
		this.priority = priority;
	}

	public RestRequestEntityImpl(IRestRequest request) {
		this.dnList = request.getDnList();
		this.dnType = request.getDnType();
		this.pluginName = request.getPluginName();
		this.pluginVersion = request.getPluginVersion();
		this.commandId = request.getCommandId();
		this.parameterMap = request.getParameterMap();
		this.cronExpression = request.getCronExpression();
		this.priority = request.getPriority();
	}

	public List<String> getDnList() {
		return dnList;
	}

	public void setDnList(List<String> dnList) {
		this.dnList = dnList;
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

	public Map<String, Object> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<String, Object> parameterMap) {
		this.parameterMap = parameterMap;
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

}
