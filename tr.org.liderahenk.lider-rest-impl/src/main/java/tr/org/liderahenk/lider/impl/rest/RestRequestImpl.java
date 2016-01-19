package tr.org.liderahenk.lider.impl.rest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
/**
 * Default implementation for {@link IRestRequest}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestRequestImpl implements IRestRequest{
	
	private String resource;
	private String access;
	private String attribute;
	private String command;
	private String action;
	private String user;
	private RestRequestBodyImpl body;
	
	
	public RestRequestImpl() {
		// TODO Auto-generated constructor stub
	}
	
	public RestRequestImpl(String resource, String access, String attribute,
			String command, String action, RestRequestBodyImpl body, String user) {
		this(resource, access, attribute, command, action, body);
		this.user = user;
	}
	
	public RestRequestImpl(String resource, String access, String attribute,
			String command, String action,  RestRequestBodyImpl body) {
		this.resource = resource;
		this.access = access;
		this.attribute = attribute;
		this.command = command;
		this.action = action;
		this.body = body;
	}

	public RestRequestImpl(IRestRequest req) {
		this.access = req.getAccess();
		this.action = req.getAction();
		this.attribute = req.getAttribute();
		this.body = (req.getBody()== null) ? null : new RestRequestBodyImpl( req.getBody());
		this.command = req.getCommand();
		this.resource = req.getResource();
		this.user = req.getUser();
	}

	@Override
	public String getResource() {
		return resource;
	}

	@Override
	public String getAccess() {
		return access;
	}

	@Override
	public String getAttribute() {
		return attribute;
	}

	@Override
	public String getCommand() {
		return command;
	}
	
	@Override
	public String getAction() {
		return action;
	}
	
	@Override
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public RestRequestBodyImpl getBody() {
		return body;
	}
	
	@Override
	public String getURL() {
		//FIXME temporary workaround to get full request url
		return "/"+getResource()+"/"+getAccess()+"/"+getAttribute()+"/"+getCommand()+"/"+getAction();
	}
	
	public void setURL(String URL){
		//FIXME @JsonIgnoreProperties(ignoreUnknown = true) doesn't work. need to cheat jackson 
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2894564680974204428L;
}
