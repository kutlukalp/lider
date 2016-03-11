package tr.org.liderahenk.lider.messaging.subscribers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
import tr.org.liderahenk.lider.core.api.messaging.enums.AgentMessageType;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRegistrationMessage;
import tr.org.liderahenk.lider.core.api.messaging.responses.IRegistrationMessageResponse;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IRegistrationSubscriber;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgentProperty;
import tr.org.liderahenk.lider.core.api.persistence.entities.IUserSession;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;
import tr.org.liderahenk.lider.messaging.responses.RegistrationMessageResponseImpl;

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
public class DefaultRegistrationSubscriber implements IRegistrationSubscriber, EventHandler {

	private static Logger logger = LoggerFactory.getLogger(DefaultRegistrationSubscriber.class);

	private ILDAPService ldapService;
	private IConfigurationService configurationService;
	private IAgentDao agentDao;
	private IMessagingService messagingService;
	private BundleContext context;

	/**
	 * Check if agent defined in the received message is already registered, if
	 * it is, update its values and properties. Otherwise create new agent LDAP
	 * entry and new agent database record.
	 */
	@Override
	public IRegistrationMessageResponse messageReceived(IRegistrationMessage message) throws Exception {

		String uid = message.getFrom().split("@")[0];

		logger.error("Message: {}", message);

		//
		// Register agent
		//
		if (AgentMessageType.REGISTER == message.getType()) {

			// Check if agent LDAP entry already exists
			final List<LdapEntry> entry = ldapService.search(configurationService.getAgentLdapJidAttribute(), uid,
					configurationService.getAgentLdapJidAttribute());

			if (entry != null && !entry.isEmpty()) {

				// Update agent LDAP entry.
				ldapService.updateEntry(entry.get(0).getDistinguishedName(), "userPassword", message.getPassword());

				// Find related agent database record.
				List<? extends IAgent> agentList = agentDao.findByProperty(IAgent.class, "jid", uid, 1);
				IAgent agent = agentList.get(0);

				// Add new properties
				List<? extends IAgentProperty> properties = createProperties(message);
				if (properties != null) {
					for (IAgentProperty property : properties) {
						agent.addProperty(property);
					}
				}

				// Merge records
				agentDao.update(agent);

				logger.error(
						"Agent DN {} already exists! Updated its password and database properties with the values submitted.",
						entry.get(0).getDistinguishedName());
				return new RegistrationMessageResponseImpl(StatusCode.ALREADY_EXISTS,
						entry.get(0).getDistinguishedName()
								+ " already exists! Updated its password and database properties with the values submitted.",
						entry.get(0).getDistinguishedName());
			} else {

				logger.error("Creating account: {} with password: {}",
						new Object[] { message.getFrom(), message.getPassword() });

				// Create new XMPP account
				messagingService.createAccount(message.getFrom(), message.getPassword());

				logger.error("Created account!");

				// Create new agent LDAP entry.
				Map<String, String[]> attributes = new HashMap<String, String[]>();
				attributes.put("objectClass", configurationService.getAgentLdapObjectClasses().split(","));
				attributes.put(configurationService.getAgentLdapIdAttribute(), new String[] { uid });
				attributes.put(configurationService.getAgentLdapJidAttribute(), new String[] { uid });
				attributes.put("userPassword", new String[] { message.getPassword() });

				// FIXME remove this line, after correcting LDAP schema!
				attributes.put("owner", new String[] { "ou=Uncategorized,dc=mys,dc=pardus,dc=org" });
				//

				logger.error("Creating DN");

				String entryDN = createEntryDN(message);
				ldapService.addEntry(entryDN, attributes);
				logger.debug("Agent DN {} created successfully!", entryDN);

				// Create new agent database record
				IAgent agent = createAgent(message, entryDN, uid);

				// Add new properties
				List<? extends IAgentProperty> properties = createProperties(message);
				if (properties != null) {
					for (IAgentProperty property : properties) {
						agent.addProperty(property);
					}
				}

				// Persist record
				agentDao.save(agent);

				logger.error("Creating DB records!");

				logger.error("Sending file to agent");

				// Send script file to agent to gather more system info
				// messagingService.sendFile(getFileAsByteArray(),
				// message.getFrom());

				logger.error("Sent file to agent");
				logger.error("Instruct agent to execute script");

				// Force agent to execute script and return result
				messagingService.executeScript("/opt/ahenk/received-files/lider/test.sh", message.getFrom());

				logger.error("Script executed");
				logger.error("Requesting script result");

				// Request script result
				messagingService.requestFile("/tmp/hosts", message.getFrom());

				logger.error("Script result file requested");

				logger.error("{} and its related database record created successfully!", entryDN);
				return new RegistrationMessageResponseImpl(StatusCode.REGISTERED,
						entryDN + " and its related database record created successfully!", entryDN);
			}

		}
		//
		// Unregister agent
		//
		else {

			// Check if agent LDAP entry already exists
			final List<LdapEntry> entry = ldapService.search(configurationService.getAgentLdapJidAttribute(), uid,
					configurationService.getAgentLdapJidAttribute());

			// Delete agent LDAP entry
			if (entry != null && !entry.isEmpty()) {
				ldapService.deleteEntry(entry.get(0).getDistinguishedName());
			}

			// Find related agent database record.
			List<? extends IAgent> agentList = agentDao.findByProperty(IAgent.class, "jid", uid, 1);
			IAgent agent = agentList.get(0);

			// Mark the record as deleted.
			agentDao.delete(agent.getId());

			return null;
		}

	}

	private byte[] getFileAsByteArray() throws IOException {

		logger.error("Trying to locate file test.sh");
		InputStream inputStream = context.getBundle().getEntry("/test.sh").openStream();
		logger.error("test.sh found!");

		StringBuilder sb = new StringBuilder("");
		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String read;
			while ((read = br.readLine()) != null) {
				sb.append(read);
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}

		logger.error("test.sh file's been read!");

		return sb.toString().getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * Create list of IAgentProperty instances from provided message.
	 * 
	 * @param message
	 * @param uid
	 * @return
	 */
	private List<? extends IAgentProperty> createProperties(final IRegistrationMessage message) {

		Map<String, Object> propertyMap = message.getData();
		List<IAgentProperty> properties = null;

		if (propertyMap != null) {
			properties = new ArrayList<IAgentProperty>();

			Set<Entry<String, Object>> entrySet = propertyMap.entrySet();
			for (Entry<String, Object> entry : entrySet) {

				final String propName = entry.getKey();
				final String propValue = entry.getValue() != null ? entry.getValue().toString() : null;

				if (propName != null && propValue != null) {
					properties.add(new IAgentProperty() {

						private static final long serialVersionUID = -1109853197778751021L;

						@Override
						public Long getId() {
							return null;
						}

						@Override
						public IAgent getAgent() {
							return null;
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

		return properties;
	}

	/**
	 * Create new agent with provided DN and agent UID.
	 * 
	 * @param message
	 * @param entryDN
	 * @return
	 */
	private IAgent createAgent(final IRegistrationMessage message, final String dn, final String uid) {
		if (message != null && dn != null) {

			final IAgent agent = new IAgent() {

				private static final long serialVersionUID = 577470808998737417L;

				List<IAgentProperty> properties = null;

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
				public Date getCreateDate() {
					return new Date();
				}

				@Override
				public Date getModifyDate() {
					return new Date();
				}

				@Override
				public Boolean getDeleted() {
					return false;
				}

				@Override
				public List<? extends IAgentProperty> getProperties() {
					return this.properties;
				}

				@Override
				public void addProperty(IAgentProperty property) {
					if (properties == null) {
						properties = new ArrayList<IAgentProperty>();
					}
					properties.add(property);
				}

				@Override
				public List<? extends IUserSession> getSessions() {
					return null;
				}

				@Override
				public void addUserSession(IUserSession userSession) {
				}

			};

			return agent;
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

	@Override
	public void handleEvent(Event event) {

		logger.error("Requested file received");

		// TODO
		// TODO
		// TODO
		// TODO
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
	 * @param messagingService
	 */
	public void setMessagingService(IMessagingService messagingService) {
		this.messagingService = messagingService;
	}

	/**
	 * 
	 * @param context
	 */
	public void setContext(BundleContext context) {
		this.context = context;
	}

}
