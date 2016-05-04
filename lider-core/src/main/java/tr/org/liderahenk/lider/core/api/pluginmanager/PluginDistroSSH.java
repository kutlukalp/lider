package tr.org.liderahenk.lider.core.api.pluginmanager;

import java.util.HashMap;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.messaging.enums.Protocol;

/**
 * Abstract class for plugin distribution over SSH
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public abstract class PluginDistroSSH implements IPluginDistro {

	private static final long serialVersionUID = 8688506063328626759L;

	public Protocol getProtocol() {
		return Protocol.SSH;
	}

	public Map<String, Object> getParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("host", getHost());
		params.put("username", getUsername());
		params.put("password", getPassword());
		params.put("path", getPath());
		params.put("port", getPort());
		params.put("publicKey", getPublicKey());
		return params;
	}

	/**
	 * 
	 * @return host
	 */
	public abstract String getHost();

	/**
	 * 
	 * @return username
	 */
	public abstract String getUsername();

	/**
	 * 
	 * @return password
	 */
	public abstract String getPassword();

	/**
	 * 
	 * @return path
	 */
	public abstract String getPath();

	/**
	 * 
	 * @return port (default 22)
	 */
	public Integer getPort() {
		return 22;
	}

	/**
	 * 
	 * @return public key (this can be used instead of password)
	 */
	public abstract String getPublicKey();

}
