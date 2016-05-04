package tr.org.liderahenk.lider.core.api.pluginmanager;

import java.util.HashMap;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.messaging.enums.Protocol;

/**
 * Abstract class for plugin distribution over HTTP
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public abstract class PluginDistroHTTP implements IPluginDistro {

	private static final long serialVersionUID = 4436696841657252164L;

	public Protocol getProtocol() {
		return Protocol.HTTP;
	}

	public Map<String, Object> getParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("url", getUrl());
		return params;
	}

	/**
	 * 
	 * @return URL
	 */
	public abstract String getUrl();

}
