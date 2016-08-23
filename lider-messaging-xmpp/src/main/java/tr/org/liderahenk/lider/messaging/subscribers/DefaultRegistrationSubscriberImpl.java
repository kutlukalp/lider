package tr.org.liderahenk.lider.messaging.subscribers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.messaging.enums.AgentMessageType;
import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRegistrationMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IRegistrationSubscriber;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.messaging.messages.RegistrationResponseMessageImpl;

/**
 * <p>
 * Provides default agent registration (and unregistration) in case no other
 * bundle provides its registration subscriber.
 * </p>
 * 
 * <p>
 * During agent registration, agent DN with the following format will be
 * created: <br/>
 * cn=${JID},ou=Uncategorized,dc=mys,dc=pardus,dc=org<br/>
 * Also, agent record and its properties will be persisted in the database.
 * </p>
 * 
 * <p>
 * After successful registration, agent DN will be returned to the sender agent.
 * Otherwise error code and error message will be returned.
 * </p>
 * 
 * <p>
 * Similarly, during agent unregistration, agent record will be removed from the
 * database and its LDAP entry will also be deleted.
 * </p>
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.messaging.IRegistrationSubscriber
 * @see tr.org.liderahenk.lider.core.api.messaging.IRegistrationMessage
 *
 */
public class DefaultRegistrationSubscriberImpl implements IRegistrationSubscriber {

	private static Logger logger = LoggerFactory.getLogger(DefaultRegistrationSubscriberImpl.class);

	private ILDAPService ldapService;
	private IConfigurationService configurationService;
	private IAgentDao agentDao;
	private IEntityFactory entityFactory;

	/**
	 * Check if agent defined in the received message is already registered, if
	 * it is, update its values and properties. Otherwise create new agent LDAP
	 * entry and new agent database record.
	 */
	@Override
	public ILiderMessage messageReceived(IRegistrationMessage message) throws Exception {

		String jid = message.getFrom().split("@")[0];

		// Register agent
		if (AgentMessageType.REGISTER == message.getType()) {

			boolean alreadyExists = false;
			String dn = null;

			// Try to find agent LDAP entry
			final List<LdapEntry> entries = ldapService.search(configurationService.getAgentLdapJidAttribute(), jid,
					new String[] { configurationService.getAgentLdapJidAttribute() });
			LdapEntry entry = entries != null && !entries.isEmpty() ? entries.get(0) : null;

			if (entry != null) {
				alreadyExists = true;
				dn = entry.getDistinguishedName();
				logger.info("Updating LDAP entry: {} with password: {}",
						new Object[] { message.getFrom(), message.getPassword() });
				// Update agent LDAP entry.
				ldapService.updateEntry(dn, "userPassword", message.getPassword());
				logger.info("Agent LDAP entry {} updated successfully!", dn);
			} else {
				dn = createEntryDN(message);
				logger.info("Creating LDAP entry: {} with password: {}",
						new Object[] { message.getFrom(), message.getPassword() });
				// Create new agent LDAP entry.
				ldapService.addEntry(dn, computeAttributes(jid, message.getPassword()));
				logger.info("Agent LDAP entry {} created successfully!", dn);
			}

			// Try to find related agent database record
			List<? extends IAgent> agents = agentDao.findByProperty(IAgent.class, "jid", jid, 1);
			IAgent agent = agents != null && !agents.isEmpty() ? agents.get(0) : null;

			if (agent != null) {
				alreadyExists = true;
				// Update the record
				agent = entityFactory.createAgent(agent, message.getPassword(), message.getHostname(),
						message.getIpAddresses(), message.getMacAddresses(), message.getData());
				agentDao.update(agent);
			} else {
				// Create new agent database record
				agent = entityFactory.createAgent(null, jid, dn, message.getPassword(), message.getHostname(),
						message.getIpAddresses(), message.getMacAddresses(), message.getData());
				agentDao.save(agent);
			}

			if (alreadyExists) {
				logger.warn(
						"Agent {} already exists! Updated its password and database properties with the values submitted.",
						dn);
				return new RegistrationResponseMessageImpl(StatusCode.ALREADY_EXISTS,
						dn + " already exists! Updated its password and database properties with the values submitted.",
						dn, null, new Date());
			} else {
				logger.info("Agent {} and its related database record created successfully!", dn);
				return new RegistrationResponseMessageImpl(StatusCode.REGISTERED,
						dn + " and its related database record created successfully!", dn, null, new Date());
			}
		} else if (AgentMessageType.UNREGISTER == message.getType()) {
			// Check if agent LDAP entry already exists
			final List<LdapEntry> entry = ldapService.search(configurationService.getAgentLdapJidAttribute(), jid,
					new String[] { configurationService.getAgentLdapJidAttribute() });

			// Delete agent LDAP entry
			if (entry != null && !entry.isEmpty()) {
				ldapService.deleteEntry(entry.get(0).getDistinguishedName());
			}

			// Find related agent database record.
			List<? extends IAgent> agents = agentDao.findByProperty(IAgent.class, "jid", jid, 1);
			IAgent agent = agents != null && !agents.isEmpty() ? agents.get(0) : null;

			// Mark the record as deleted.
			if (agent != null) {
				agentDao.delete(agent.getId());
			}

			return null;
		} else if (AgentMessageType.REGISTER_LDAP == message.getType()) {
			logger.info("REGISTER_LDAP");
			return null;
		}

		return null;
	}

	@Override
	public ILiderMessage postRegistration() throws Exception {
		return null;
	}

	/**
	 * Create agent DN in the following format:<br/>
	 * cn=${JID},ou=Uncategorized,dc=mys,dc=pardus,dc=org<br/>
	 * 
	 * @param message
	 *            register message
	 * @return created agent DN
	 */
	private String createEntryDN(IRegistrationMessage message) {
		StringBuilder entryDN = new StringBuilder();
		// Generate agent ID attribute
		entryDN.append(configurationService.getAgentLdapIdAttribute());
		entryDN.append("=");
		entryDN.append(message.getFrom().split("@")[0]);
		// Append base DN
		entryDN.append(",");
		entryDN.append(configurationService.getAgentLdapBaseDn() == null ? configurationService.getAgentLdapBaseDn()
				: configurationService.getAgentLdapBaseDn());
		return entryDN.toString();
	}

	/**
	 * 
	 * @param jid
	 * @param password
	 * @return
	 */
	private Map<String, String[]> computeAttributes(final String jid, final String password) {
		Map<String, String[]> attributes = new HashMap<String, String[]>();
		attributes.put("objectClass", configurationService.getAgentLdapObjectClasses().split(","));
		attributes.put(configurationService.getAgentLdapIdAttribute(), new String[] { jid });
		attributes.put(configurationService.getAgentLdapJidAttribute(), new String[] { jid });
		attributes.put("userPassword", new String[] { password });
		// FIXME remove this line, after correcting LDAP schema!
		attributes.put("owner", new String[] { "ou=Uncategorized,dc=mys,dc=pardus,dc=org" });
		return attributes;
	}

	/**
	 * 
	 * @param ldapService
	 */
	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	/**
	 * 
	 * @param configurationService
	 */
	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	/**
	 * 
	 * @param agentDao
	 */
	public void setAgentDao(IAgentDao agentDao) {
		this.agentDao = agentDao;
	}

	/**
	 * 
	 * @param entityFactory
	 */
	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

}
