package tr.org.liderahenk.lider.impl.registration;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.agent.IAgent;
import tr.org.liderahenk.lider.core.api.agent.IAgentProperty;
import tr.org.liderahenk.lider.core.api.agent.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.auth.IRegistrationInfo;
import tr.org.liderahenk.lider.core.api.auth.RegistrationStatus;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.messaging.IRegisterMessage;
import tr.org.liderahenk.lider.core.api.messaging.IRegisterSubscriber;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
 * <p>
 * Provides default agent registration in case no other bundle provides its
 * registration service.
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
 * Otherwise error enum and error message will be returned.
 * </p>
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.messaging.IRegisterSubscriber
 * @see tr.org.liderahenk.lider.core.api.messaging.IRegisterMessage
 *
 */
public class DefaultRegisterSubscriber implements IRegisterSubscriber {

	private static Logger logger = LoggerFactory.getLogger(DefaultRegisterSubscriber.class);

	private ILDAPService ldapService;
	private IConfigurationService configurationService;
	private IAgentDao agentDao;

	/**
	 * Check if agent defined in the received message is already registered, if
	 * it is, update its values and properties. Otherwise create new agent LDAP
	 * entry and new agent database record.
	 */
	@Override
	public IRegistrationInfo messageReceived(IRegisterMessage message) throws Exception {

		String uid = message.getFrom().split("@")[0];

		// Check if agent LDAP entry already exists
		final List<LdapEntry> entry = ldapService.search(configurationService.getAgentLdapJidAttribute(), uid,
				configurationService.getAgentLdapJidAttribute());

		// Agent entry already exists.
		if (entry != null && !entry.isEmpty()) {

			// Update agent LDAP entry.
			ldapService.updateEntry(entry.get(0).getDistinguishedName(), "userPassword", message.getPassword());

			// Update agent database record.
			List<? extends IAgent> agentList = agentDao.findByProperty("JID", uid, 1);
			agentDao.update(createAgent(message, agentList.get(0), uid));

			logger.debug(
					"Agent DN {} already exists! Updated its password with the one you submitted and returning existing entry attributes.",
					entry.get(0).getDistinguishedName());
			return new RegistrationInfoImpl(RegistrationStatus.ALREADY_EXISTS,
					entry.get(0).getDistinguishedName()
							+ " already exists! Updated its password with the one you submitted and returning existing entry attributes.",
					entry.get(0).getDistinguishedName());
		} else {

			// Create new agent LDAP entry.
			Map<String, String[]> attributes = new HashMap<String, String[]>();
			attributes.put("objectClass", configurationService.getAgentLdapObjectClasses().split(","));
			attributes.put(configurationService.getAgentLdapIdAttribute(), new String[] { uid });
			attributes.put(configurationService.getAgentLdapJidAttribute(), new String[] { uid });
			attributes.put("userPassword", new String[] { message.getPassword() });

			String entryDN = createEntryDN(message);
			ldapService.addEntry(entryDN, attributes);
			logger.debug("Agent DN {} created successfully!", entryDN);

			// Create new agent database record
			agentDao.save(createAgent(message, entryDN, uid));

			return new RegistrationInfoImpl(RegistrationStatus.REGISTERED, entryDN + " created successfully!", entryDN);
		}

	}

	/**
	 * Create new agent with provided DN and agent UID.
	 * 
	 * @param message
	 * @param entryDN
	 * @return
	 */
	private IAgent createAgent(final IRegisterMessage message, final String dn, final String uid) {
		if (message != null && dn != null) {
			final IAgent agent = new IAgent() {

				private static final long serialVersionUID = 577470808998737417L;

				@Override
				public Long getId() {
					return null;
				}

				@Override
				public String getJid() {
					return uid; // XMPP JID = LDAP UID
				}

				@Override
				public String getPassword() {
					return message.getPassword();
				}

				@Override
				public String getHostname() {
					return message.getHostname();
				}

				@Override
				public String getIpAddresses() {
					return message.getIpAddresses();
				}

				@Override
				public String getMacAddresses() {
					return message.getMacAddresses();
				}

				@Override
				public String getDn() {
					return dn;
				}

				@Override
				public Date getCreationDate() {
					return new Date();
				}

				@Override
				public List<? extends IAgentProperty> getProperties() {
					return null;
				}

				@Override
				public void setProperties(List<? extends IAgentProperty> properties) {
				}

			};

			List<IAgentProperty> properties = createAgentProperties(message, agent);
			agent.setProperties(properties);

			return agent;
		}

		return null;
	}

	/**
	 * Create new agent by updating necessary fields of the current IAgent
	 * object.
	 * 
	 * @param message
	 * @param currAgent
	 * @return
	 */
	private IAgent createAgent(final IRegisterMessage message, final IAgent currAgent, final String uid) {
		if (message != null) {
			final IAgent agent = new IAgent() {

				private static final long serialVersionUID = -7378280501667962519L;

				@Override
				public List<? extends IAgentProperty> getProperties() {
					return null;
				}

				@Override
				public String getPassword() {
					return message.getPassword();
				}

				@Override
				public String getMacAddresses() {
					return message.getMacAddresses();
				}

				@Override
				public String getJid() {
					return uid; // XMPP JID = LDAP UID
				}

				@Override
				public String getIpAddresses() {
					return message.getIpAddresses();
				}

				@Override
				public Long getId() {
					return currAgent.getId();
				}

				@Override
				public String getHostname() {
					return message.getHostname();
				}

				@Override
				public String getDn() {
					return currAgent.getDn();
				}

				@Override
				public Date getCreationDate() {
					return currAgent.getCreationDate();
				}

				@Override
				public void setProperties(List<? extends IAgentProperty> properties) {
				}
			};

			// Create agent properties
			List<IAgentProperty> properties = createAgentProperties(message, agent);
			agent.setProperties(properties);

			return agent;

		}

		return null;
	}

	/**
	 * Create agent property list for provided IAgent object.
	 * 
	 * @param message
	 * @param agent
	 * @return
	 */
	private List<IAgentProperty> createAgentProperties(final IRegisterMessage message, final IAgent agent) {
		Map<String, Object> properties = message.getData();
		List<IAgentProperty> propList = null;
		if (properties != null) {
			propList = new ArrayList<IAgentProperty>();
			Set<Entry<String, Object>> entrySet = properties.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				final String propName = entry.getKey();
				final String propValue = entry.getValue() != null ? entry.getValue().toString() : null;
				if (propName != null && propValue != null) {
					propList.add(new IAgentProperty() {

						private static final long serialVersionUID = -1109853197778751021L;

						@Override
						public Long getId() {
							return null;
						}

						@Override
						public IAgent getAgent() {
							return agent;
						}

						@Override
						public String getPropertyName() {
							return propName;
						}

						@Override
						public String getPropertyValue() {
							return propValue;
						}

					});
				}
			}
		}
		return propList;
	}

	/**
	 * Create agent DN in the following format:<br/>
	 * cn=${JID},ou=Uncategorized,dc=mys,dc=pardus,dc=org<br/>
	 * 
	 * @param message
	 *            register message
	 * @return created agent DN
	 */
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

}
