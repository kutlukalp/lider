package tr.org.liderahenk.lider.messaging.subscribers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.messaging.enums.AgentMessageType;
import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRegistrationMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRegistrationResponseMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IRegistrationSubscriber;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;
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
	public IRegistrationResponseMessage messageReceived(IRegistrationMessage message) throws Exception {

		String uid = message.getFrom().split("@")[0];

		// Register agent
		if (AgentMessageType.REGISTER == message.getType()) {

			// Check if agent LDAP entry already exists
			final List<LdapEntry> entry = ldapService.search(configurationService.getAgentLdapJidAttribute(), uid,
					new String[] { configurationService.getAgentLdapJidAttribute() });
			if (entry != null && !entry.isEmpty()) {
				// Update agent LDAP entry.
				ldapService.updateEntry(entry.get(0).getDistinguishedName(), "userPassword", message.getPassword());

				// Find related agent database record.
				List<? extends IAgent> agents = agentDao.findByProperty(IAgent.class, "jid", uid, 1);
				// We found the agent, update its properties!
				if (agents != null && !agents.isEmpty()) {
					IAgent agent = agents.get(0);
					agent = entityFactory.createAgent(agent, message.getPassword(), message.getHostname(),
							message.getIpAddresses(), message.getMacAddresses(), message.getData());
					// Merge records
					agentDao.update(agent);
				} else {
					String entryDN = createEntryDN(message);
					IAgent agent = entityFactory.createAgent(null, uid, entryDN, message.getPassword(),
							message.getHostname(), message.getIpAddresses(), message.getMacAddresses(),
							message.getData());
					// Persist record
					agentDao.save(agent);
				}

				logger.warn(
						"Agent DN {} already exists! Updated its password and database properties with the values submitted.",
						entry.get(0).getDistinguishedName());
				return new RegistrationResponseMessageImpl(StatusCode.ALREADY_EXISTS,
						entry.get(0).getDistinguishedName()
								+ " already exists! Updated its password and database properties with the values submitted.",
						entry.get(0).getDistinguishedName(), null, new Date());
			} else {
				logger.debug("Creating account: {} with password: {}",
						new Object[] { message.getFrom(), message.getPassword() });

				// Create new agent LDAP entry.
				Map<String, String[]> attributes = new HashMap<String, String[]>();
				attributes.put("objectClass", configurationService.getAgentLdapObjectClasses().split(","));
				attributes.put(configurationService.getAgentLdapIdAttribute(), new String[] { uid });
				attributes.put(configurationService.getAgentLdapJidAttribute(), new String[] { uid });
				attributes.put("userPassword", new String[] { message.getPassword() });

				// FIXME remove this line, after correcting LDAP schema!
				attributes.put("owner", new String[] { "ou=Uncategorized,dc=mys,dc=pardus,dc=org" });
				//

				String entryDN = createEntryDN(message);
				ldapService.addEntry(entryDN, attributes);
				logger.debug("Agent DN {} created successfully!", entryDN);

				// Create new agent database record
				IAgent agent = entityFactory.createAgent(null, uid, entryDN, message.getPassword(),
						message.getHostname(), message.getIpAddresses(), message.getMacAddresses(), message.getData());
				// Persist record
				agentDao.save(agent);

				logger.error("{} and its related database record created successfully!", entryDN);
				return new RegistrationResponseMessageImpl(StatusCode.REGISTERED,
						entryDN + " and its related database record created successfully!", entryDN, null, new Date());
			}
		} else if (AgentMessageType.UNREGISTER == message.getType()) {
			// Check if agent LDAP entry already exists
			final List<LdapEntry> entry = ldapService.search(configurationService.getAgentLdapJidAttribute(), uid,
					new String[] { configurationService.getAgentLdapJidAttribute() });

			// Delete agent LDAP entry
			if (entry != null && !entry.isEmpty()) {
				ldapService.deleteEntry(entry.get(0).getDistinguishedName());
			}

			// Find related agent database record.
			List<? extends IAgent> agents = agentDao.findByProperty(IAgent.class, "jid", uid, 1);
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
