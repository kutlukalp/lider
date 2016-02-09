package tr.org.liderahenk.lider.persistence.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
import tr.org.liderahenk.lider.core.api.rest.Priority;
import tr.org.liderahenk.lider.core.api.rest.RestDNType;

@Embeddable
//@Entity
public class RestRequestEntityImpl implements IRestRequest{
	
	
	@ElementCollection
	private List<String> dnList;
	
	@Enumerated(EnumType.STRING)
	private RestDNType dnType;
	
	private String pluginName;
	
	@Column(length=8000)
	private String customParameterMapJSON;
	
	private String pluginVersion;
	
	private String commandId;
	
	private String cronExpression;
	
	private Priority priority;
	
	public RestRequestEntityImpl() {
	}

	public RestRequestEntityImpl(IRestRequest request) {
	}

	@Override
	public List<String> getDnList() {
		return dnList;
	}

	@Override
	public RestDNType getDnType() {
		return dnType;
	}

	@Override
	public String getPluginName() {
		return pluginName;
	}

	@Override
	public String getPluginVersion() {
		return pluginVersion;
	}

	@Override
	public String getCommandId() {
		// TODO Auto-generated method stub
		return commandId;
	}

	@Override
	@Transient
	public Map<String, Object> getParameterMap() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(customParameterMapJSON, new TypeReference<HashMap<String,Object>>(){});
		} catch (Exception e) {
			// this should never happen
			//e.printStackTrace();
			return null;
		} 
	}
	@Override
	public String getCronExpression() {
		return cronExpression;
	}

	@Override
	public Priority getPriority() {
		return priority;
	}

	public void setDnList(List<String> dnList) {
		this.dnList = dnList;
	}

	public void setDnType(RestDNType dnType) {
		this.dnType = dnType;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public String getCustomParameterMapJSON() {
		return customParameterMapJSON;
	}

	public void setCustomParameterMapJSON(String customParameterMapJSON) {
		this.customParameterMapJSON = customParameterMapJSON;
	}

	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -4355421110535971298L;
//	private String resource;
//	private String access;
//	private String attribute;
//	private String command;
//	private String action;
//	private String user;
//	
//	
//	@Embedded
//	 @AttributeOverrides( {
//	       @AttributeOverride(name = "pluginId", column = @Column(name = "plugin_id")),
//	       @AttributeOverride(name = "pluginVersion", column = @Column(name = "plugin_version")),
//	       @AttributeOverride(name = "priority", column = @Column(name = "task_priority"))
//	    })
////	@AssociationOverrides( { @AssociationOverride(name = "pluginId", joinColumns = @JoinColumn(name = "plugin_Id")),
////		@AssociationOverride(name = "pluginVersion", joinColumns = @JoinColumn(name = "plugin_version"))})
//	private RestRequestBodyEntityImpl body;
//	
//	public RestRequestEntityImpl() {
//	}
//	
//	public RestRequestEntityImpl(IRestRequest req) throws Exception {
//		this.access = req.getAccess();
//		this.action = req.getAction();
//		this.attribute = req.getAttribute();
//		this.body = (req.getBody()== null) ? null : new RestRequestBodyEntityImpl( req.getBody());
//		this.command = req.getCommand();
//		this.resource = req.getResource();
//		this.user = req.getUser();
//	}
//	
//	public String getResource() {
//		return resource;
//	}
//	public void setResource(String resource) {
//		this.resource = resource;
//	}
//	public String getAccess() {
//		return access;
//	}
//	public void setAccess(String access) {
//		this.access = access;
//	}
//	public String getAttribute() {
//		return attribute;
//	}
//	public void setAttribute(String attribute) {
//		this.attribute = attribute;
//	}
//	public String getCommand() {
//		return command;
//	}
//	public void setCommand(String command) {
//		this.command = command;
//	}
//	public String getAction() {
//		return action;
//	}
//	public void setAction(String action) {
//		this.action = action;
//	}
//	public RestRequestBodyEntityImpl getBody() {
//		return body;
//	}
//	public void setBody(RestRequestBodyEntityImpl body) {
//		this.body = body;
//	}
//	
//	public String getUser() {
//		return user;
//	}
//	
//	public void setUser(String user) {
//		this.user = user;
//	}
//	
//	@Transient
//	public String getURL(){
//		return null;
//	}

	
}
