package tr.org.liderahenk.lider.impl.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import tr.org.pardus.mys.auth.api.IRegistrationStrategy;

import tr.org.liderahenk.lider.core.api.auth.IRegistrationInfo;
import tr.org.liderahenk.lider.core.api.auth.RegistrationStatus;
import tr.org.liderahenk.lider.core.api.authorization.IClientIdMatcher;
import tr.org.liderahenk.lider.core.api.authorization.IRegistrationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapException;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
 * Default implementation of {@link IRegistrationService}.
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class RegistrationServiceImpl implements IRegistrationService {

	private static Logger log = LoggerFactory.getLogger(RegistrationServiceImpl.class);

	private IClientIdMatcher idMatcher;
	private ILDAPService ldapService;

	public void setIdMatcher(IClientIdMatcher idMatcher) {
		this.idMatcher = idMatcher;
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	@Override
	public IRegistrationInfo register(String identifier, String member, String generatedPassword,
			Boolean useExistingEntry, String remoteHost, String remoteIP) throws Exception {

		Map<String, String[]> entryAttributes = idMatcher.match(identifier);
		String entryDN = entryAttributes.get("DN")[0];// (String)
														// idMatcher.match(identifier);
		String[] attrs = { "uid" };
		final List<LdapEntry> entry = ldapService.search(idMatcher.getAgentJidAttribute(), identifier, attrs);

		if (!entry.isEmpty()) {
			if (!useExistingEntry) {
				return new RegistrationInfoImpl(RegistrationStatus.ALREADY_EXISTS, null, null, null, null, entry.get(0)
						.getDistinguishedName()
						+ " already exists! Set useExistingEntry flag to true to retrieve & use that information");
			} else {
				ldapService.updateEntry(entry.get(0).getDistinguishedName(), "userPassword", generatedPassword);
				return new RegistrationInfoImpl(RegistrationStatus.PASSWORD_UPDATED,
						entry.get(0).getAttributes().get(idMatcher.getAgentJidAttribute()).toString(),
						entry.get(0).getDistinguishedName(),
						// entry.getAttributes().get("userPassword"),
						idMatcher.getXmppServer(), idMatcher.getXmppDomain(),
						// "http://localhost:8181/",
						entry.get(0).getDistinguishedName()
								+ " already exists! Updated its password with the one you submitted and returning existing entry's attributes");
			}
		} else /* if( entry == null ) */ {
			Map<String, String[]> attributes = new HashMap<String, String[]>();
			attributes.put("objectClass", new String[] { "pardusDevice", "device" });
			attributes.put("cn", new String[] { identifier });
			attributes.put("owner", new String[] { member });
			attributes.put("uid", new String[] { identifier });
			attributes.put("userPassword", new String[] { generatedPassword });

			try {
				ldapService.addEntry(entryDN, attributes);
				LdapEntry newEntry = ldapService.getEntry(entryDN,
						new String[] { idMatcher.getAgentJidAttribute(), "userPassword" });
				return new RegistrationInfoImpl(RegistrationStatus.REGISTERED,
						newEntry.getAttributes().get(idMatcher.getAgentJidAttribute()), entryDN,
						idMatcher.getXmppServer(), idMatcher.getXmppDomain(), entryDN + " created successfully!");

			} catch (final LdapException e) {
				return new RegistrationInfoImpl(RegistrationStatus.REGISTRATION_ERROR, null, null, null, null,
						e.getMessage());
			}
		}

	}

}
