package tr.org.liderahenk.lider.impl.autherization;

import java.util.HashMap;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.autherization.IClientIdMatcher;

/**
* Default implementation of {@link IClientIdMatcher}.
*  
* @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
*/
public class ClientIdMatcherImpl implements IClientIdMatcher {
	
	private IConfigurationService config;
	
	public void setConfig(
			IConfigurationService configurationService) {
		this.config = configurationService;
	}
	
	@Override
	public String getAgentBaseDn() {
		return config.getAgentLdapBaseDn();//agentBaseDn;
	}
	
	@Override
	public String getAgentIdAttribute() {
		return config.getAgentLdapIdAttribute();//agentIdAttribute;
	}
	
	@Override
	public String getAgentJidAttribute() {
		return config.getAgentLdapJidAttribute();//agentJidAttribute;
	}
	
	@Override
	public String getXmppServer() {
		return config.getXmppServerPublicIP();//xmppServer;
	}
	
	@Override
	public String getXmppDomain() {
		return config.getXmppDomain();//xmppServer;
	}
	
	@Override
	public Map<String,String[]> match(String... identifiers) {
		HashMap<String, String[]> attributes = new HashMap<String, String[]>();
		attributes.put("DN", new String[]{
				getAgentIdAttribute() + "="+ identifiers[0] 
				+ "," + getAgentBaseDn() });
		attributes.put(getAgentIdAttribute(), new String[]{identifiers[0]} );
		
		return attributes;
	}

}
