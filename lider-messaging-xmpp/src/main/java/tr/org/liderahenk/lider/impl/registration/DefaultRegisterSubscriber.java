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
	private IConfigurationService config;

	@Override
	public IRegistrationInfo messageReceived(IRegisterMessage message) throws Exception {

		String uid = message.getFrom().split("@")[0];

		// Check if agent already exists!
		final List<LdapEntry> entry = ldapService.search(config.getAgentLdapJidAttribute(), uid,
				config.getAgentLdapJidAttribute());

		// Agent DN already exists
		if (entry != null && !entry.isEmpty()) {
			ldapService.updateEntry(entry.get(0).getDistinguishedName(), "userPassword", message.getPassword());
			logger.debug(
					"Agent DN {} already exists! Updated its password with the one you submitted and returning existing entry attributes.",
					entry.get(0).getDistinguishedName());
			return new RegistrationInfoImpl(RegistrationStatus.ALREADY_EXISTS,
					entry.get(0).getDistinguishedName()
							+ " already exists! Updated its password with the one you submitted and returning existing entry attributes.",
					entry.get(0).getDistinguishedName());
		} else {
			// Create new agent DN
			Map<String, String[]> attributes = new HashMap<String, String[]>();
			attributes.put("objectClass", config.getAgentLdapObjectClasses().split(","));
			attributes.put(config.getAgentLdapIdAttribute(), new String[] { uid });
			attributes.put(config.getAgentLdapJidAttribute(), new String[] { uid });
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
		entryDN.append(config.getAgentLdapIdAttribute());
		entryDN.append("=");
		entryDN.append(message.getFrom().split("@")[0]);
		// Append base DN
		entryDN.append(",");
		entryDN.append(config.getAgentLdapBaseDn() == null ? config.getAgentLdapBaseDn() : config.getAgentLdapBaseDn());
		return entryDN.toString();
	}

}
