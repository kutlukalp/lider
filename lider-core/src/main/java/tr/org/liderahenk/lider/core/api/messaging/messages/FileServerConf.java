package tr.org.liderahenk.lider.core.api.messaging.messages;

import java.io.Serializable;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.messaging.enums.Protocol;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;

/**
 * Optional parameter for file transfer. (If a plugin uses file transfer, which
 * can be determined by {@link IPluginInfo} implementation, this optional
 * parameter will be set before sending EXECUTE_TASK / EXECUTE_POLICY messages
 * to agents)
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class FileServerConf implements Serializable {

	private static final long serialVersionUID = 1039344020464416617L;

	private Map<String, Object> parameterMap;

	private Protocol protocol;

	public FileServerConf(Map<String, Object> parameterMap, Protocol protocol) {
		this.parameterMap = parameterMap;
		this.protocol = protocol;
	}

	public Map<String, Object> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<String, Object> parameterMap) {
		this.parameterMap = parameterMap;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

}
