package tr.org.liderahenk.lider.persistence.model.impl;

import java.util.List;
import java.util.Map;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
import tr.org.liderahenk.lider.core.api.rest.Priority;
import tr.org.liderahenk.lider.core.api.rest.RestDNType;

@Embeddable
public class RestRequestEntityImpl implements IRestRequest{
	
	public RestRequestEntityImpl() {
	}

	public RestRequestEntityImpl(IRestRequest request) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<String> getDnList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RestDNType getDnType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPluginName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPluginVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getParameterMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCronExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Priority getPriority() {
		// TODO Auto-generated method stub
		return null;
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
