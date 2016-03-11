package tr.org.liderahenk.lider.service.responses;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;

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
	 * Response messages can be used along with status to notify result.
	 */
	private List<String> messages;

	/**
	 * Contains result parameters which can be used by the plugin (e.g.
	 * displaying results)
	 */
	private Map<String, Object> resultMap;

	public RestResponseImpl() {
	}

	public RestResponseImpl(RestResponseStatus status, List<String> messages, Map<String, Object> resultMap) {
		this.status = status;
		this.messages = messages;
		this.resultMap = resultMap;
	}

	public RestResponseImpl(ICommandResult result) {
		this.messages = result.getMessages();
		this.resultMap = result.getResultMap();

		switch (result.getStatus()) {
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
