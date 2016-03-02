package tr.org.liderahenk.lider.impl.rest.service;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.plugin.ICommandResult;
import tr.org.liderahenk.lider.core.api.rest.IRestResponse;
import tr.org.liderahenk.lider.core.api.rest.RestResponseStatus;

/**
 * Default implementation for {@link IRestResponse}. Response object which is
 * used to deliver executed command result back to Lider Console.
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestResponseImpl implements IRestResponse {

	private static final long serialVersionUID = -5095818044483623056L;

	/**
	 * Contains result status. This is the only status code that can be used for
	 * handling responses.
	 */
	private RestResponseStatus status;

	/**
	 * Name of the plugin which REST request/response belongs to
	 */
	private String pluginName;

	/**
	 * Plugin version number is used to distinguish plugins with multiple
	 * bundles running on Lider Server
	 */
	private String pluginVersion;

	/**
	 * ID of the executed command of the plugin.
	 */
	private String commandId;

	/**
	 * Response messages can be used along with status to notify result.
	 */
	private List<String> messages;

	/**
	 * Contains result parameters which can be used by the plugin (e.g.
	 * displaying results)
	 */
	private Map<String, Object> resultMap;

	/**
	 * array of task ID
	 */
	private String[] tasks;

	public RestResponseImpl() {
	}

	public RestResponseImpl(RestResponseStatus status, List<String> messages) {
		this.status = status;
		this.messages = messages;
	}

	public RestResponseImpl(ICommandResult commandResult, String[] tasks) {

		this.pluginName = commandResult.getCommand().getPluginName();
		this.pluginVersion = commandResult.getCommand().getPluginVersion();
		this.commandId = commandResult.getCommand().getCommandId();
		this.messages = commandResult.getMessages();
		this.resultMap = commandResult.getResultMap();
		this.tasks = tasks;

		switch (commandResult.getStatus()) {
		case OK:
			this.status = RestResponseStatus.OK;
			break;
		case ERROR:
			this.status = RestResponseStatus.ERROR;
			break;
		case WARNING:
			this.status = RestResponseStatus.WARNING;
			break;
		}
	}

	@Override
	public RestResponseStatus getStatus() {
		return status;
	}

	public void setStatus(RestResponseStatus status) {
		this.status = status;
	}

	@Override
	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		// Ensure there is no whitespace characters in the string
		this.pluginName = pluginName != null ? pluginName.replace("\\s+", "") : null;
	}

	@Override
	public String getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(String pluginVersion) {
		// Ensure there is no whitespace characters in the string
		this.pluginVersion = pluginVersion != null ? pluginVersion.replace("\\s+", "") : null;
	}

	@Override
	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		// Ensure there is no whitespace characters in the string
		this.commandId = commandId != null ? commandId.replace("\\s+", "") : null;
	}

	@Override
	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	@Override
	public Map<String, Object> getResultMap() {
		return resultMap;
	}

	public void setResultMap(Map<String, Object> resultMap) {
		this.resultMap = resultMap;
	}

	@Override
	public String[] getTasks() {
		return tasks;
	}

	public void setTasks(String[] tasks) {
		this.tasks = tasks;
	}

	@Override
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
