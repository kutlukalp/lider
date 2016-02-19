package tr.org.liderahenk.lider.impl.authorization;

import java.util.HashMap;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.authorization.IClientIdMatcher;

/**
 * Default implementation of {@link IClientIdMatcher}.
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class ClientIdMatcherImpl implements IClientIdMatcher {

	private IConfigurationService config;

	@Override
	public String getAgentBaseDn() {
		return config.getAgentLdapBaseDn();
	}

	@Override
	public String getAgentIdAttribute() {
		return config.getAgentLdapIdAttribute();
	}

	@Override
	public String getAgentJidAttribute() {
		return config.getAgentLdapJidAttribute();
	}

	@Override
	public String getXmppServer() {
		return config.getXmppHost();
	}

	@Override
	public String getXmppDomain() {
		return config.getXmppServiceName();
	}

	@Override
	public Map<String, String[]> match(String... identifiers) {
		HashMap<String, String[]> attributes = new HashMap<String, String[]>();
		attributes.put("DN", new String[] { getAgentIdAttribute() + "=" + identifiers[0] + "," + getAgentBaseDn() });
		attributes.put(getAgentIdAttribute(), new String[] { identifiers[0] });

		return attributes;
	}

	public void setConfig(IConfigurationService configurationService) {
		this.config = configurationService;
	}

}
