package tr.org.liderahenk.lider.impl.registration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.auth.IRegistrationInfo;
import tr.org.liderahenk.lider.core.api.auth.RegistrationStatus;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.messaging.IRegisterMessage;
import tr.org.liderahenk.lider.core.api.messaging.IRegisterSubscriber;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

public class DefaultRegisterSubscriber implements IRegisterSubscriber {

	private static Logger logger = LoggerFactory.getLogger(DefaultRegisterSubscriber.class);

	private ILDAPService ldapService;
	private IConfigurationService configurationService;
	
	@Override
	public IRegistrationInfo messageReceived(IRegisterMessage message) throws Exception {

		String uid = message.getFrom().split("@")[0];
		
		logger.error("1111: " + uid);
		logger.error("LDAP SERVICE: " + ldapService);
		logger.error("CONFIG: " + configurationService);
		logger.error("CONFIG_VAL: " + configurationService.toString());

		// Check if agent already exists!
		final List<LdapEntry> entry = ldapService.search(configurationService.getAgentLdapJidAttribute(), uid,
				configurationService.getAgentLdapJidAttribute());

		// Agent DN already exists
		if (entry != null && !entry.isEmpty()) {
			// Update agent entry
			ldapService.updateEntry(entry.get(0).getDistinguishedName(), "userPassword", message.getPassword());

			// Update agent info in the database
			// TODO

			logger.debug(
					"Agent DN {} already exists! Updated its password with the one you submitted and returning existing entry attributes.",
					entry.get(0).getDistinguishedName());
			return new RegistrationInfoImpl(RegistrationStatus.ALREADY_EXISTS,
					entry.get(0).getDistinguishedName()
							+ " already exists! Updated its password with the one you submitted and returning existing entry attributes.",
					entry.get(0).getDistinguishedName());
		} else {

			logger.error("333");

			// Create new agent entry
			Map<String, String[]> attributes = new HashMap<String, String[]>();
			attributes.put("objectClass", configurationService.getAgentLdapObjectClasses().split(","));
			attributes.put(configurationService.getAgentLdapIdAttribute(), new String[] { uid });
			attributes.put(configurationService.getAgentLdapJidAttribute(), new String[] { uid });
			attributes.put("userPassword", new String[] { message.getPassword() });

			String entryDN = createEntryDN(message);
			ldapService.addEntry(entryDN, attributes);
			logger.debug("Agent DN {} created successfully!", entryDN);

			// Insert agent info into the database
			// TODO

			return new RegistrationInfoImpl(RegistrationStatus.REGISTERED, entryDN + " created successfully!", entryDN);
		}

	}

	private String createEntryDN(IRegisterMessage message) {
		StringBuilder entryDN = new StringBuilder();
		// Generate agent ID attribute
		entryDN.append(configurationService.getAgentLdapIdAttribute());
		entryDN.append("=");
		entryDN.append(message.getFrom().split("@")[0]);
		// Append base DN
		entryDN.append(",");
		entryDN.append(configurationService.getAgentLdapBaseDn() == null ? configurationService.getAgentLdapBaseDn()
				: configurationService.getAgentLdapBaseDn());

		logger.error("4444" + entryDN.toString());
		return entryDN.toString();
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
