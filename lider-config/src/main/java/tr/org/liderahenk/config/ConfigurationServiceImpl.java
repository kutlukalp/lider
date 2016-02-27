package tr.org.liderahenk.config;

import tr.org.liderahenk.lider.core.api.IConfigurationService;

/**
 * This class provides configurations throughout the system. Configurations are
 * read from <code>${KARAF_HOME}/etc/tr.org.liderahenk.cfg</code> file.
 * 
 * @author <a href="mailto:bm.volkansahin@gmail.com">volkansahin</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ConfigurationServiceImpl implements IConfigurationService {

	// Database configuration
	private String dbServer;
	private Integer dbPort;
	private String dbDatabase;
	private String dbUsername;
	private String dbPassword;

	// LDAP configuration
	private String ldapServer;
	private String ldapPort;
	private String ldapUsername;
	private String ldapPassword;
	private String ldapRootDn;
	private Boolean ldapUseSsl;

	// XMPP configuration
	private String xmppHost; // host name/server name
	private Integer xmppPort;
	private String xmppUsername;
	private String xmppPassword;
	private String xmppServiceName; // service name / XMPP domain
	private int xmppMaxRetryConnectionCount;
	private int xmppPacketReplayTimeout;
	private Integer xmppPingTimeout;
	private Boolean xmppUseSsl;

	// Agent configuration
	private String agentLdapBaseDn;
	private String agentLdapIdAttribute;
	private String agentLdapJidAttribute;
	private String agentLdapObjectClasses;

	// User configuration
	private String authLdapUserSearchBase;
	private String authLdapUserObjectClasses;
	private String authLdapUserAttribute;
	private Boolean authorizationEnabled;

	// Task manager configuration
	private Long taskManagerTaskTimeout;
	private Boolean taskManagerMulticastEnabled;
	private Boolean taskManagerLogXmppMessagesEnabled;

	public String getDbServer() {
		return dbServer;
	}

	public void setDbServer(String dbServer) {
		this.dbServer = dbServer;
	}

	public Integer getDbPort() {
		return dbPort;
	}

	public void setDbPort(Integer dbPort) {
		this.dbPort = dbPort;
	}

	public String getDbDatabase() {
		return dbDatabase;
	}

	public void setDbDatabase(String dbDatabase) {
		this.dbDatabase = dbDatabase;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getLdapServer() {
		return ldapServer;
	}

	public void setLdapServer(String ldapServer) {
		this.ldapServer = ldapServer;
	}

	public String getLdapPort() {
		return ldapPort;
	}

	public void setLdapPort(String ldapPort) {
		this.ldapPort = ldapPort;
	}

	public String getLdapUsername() {
		return ldapUsername;
	}

	public void setLdapUsername(String ldapUsername) {
		this.ldapUsername = ldapUsername;
	}

	public String getLdapPassword() {
		return ldapPassword;
	}

	public void setLdapPassword(String ldapPassword) {
		this.ldapPassword = ldapPassword;
	}

	public String getLdapRootDn() {
		return ldapRootDn;
	}

	public void setLdapRootDn(String ldapRootDn) {
		this.ldapRootDn = ldapRootDn;
	}

	public Boolean getLdapUseSsl() {
		return ldapUseSsl;
	}

	public void setLdapUseSsl(Boolean ldapUseSsl) {
		this.ldapUseSsl = ldapUseSsl;
	}

	public String getXmppHost() {
		return xmppHost;
	}

	public void setXmppHost(String xmppHost) {
		this.xmppHost = xmppHost;
	}

	public Integer getXmppPort() {
		return xmppPort;
	}

	public void setXmppPort(Integer xmppPort) {
		this.xmppPort = xmppPort;
	}

	public String getXmppUsername() {
		return xmppUsername;
	}

	public void setXmppUsername(String xmppUsername) {
		this.xmppUsername = xmppUsername;
	}

	public String getXmppPassword() {
		return xmppPassword;
	}

	public void setXmppPassword(String xmppPassword) {
		this.xmppPassword = xmppPassword;
	}

	public String getXmppServiceName() {
		return xmppServiceName;
	}

	public void setXmppServiceName(String xmppServiceName) {
		this.xmppServiceName = xmppServiceName;
	}

	public int getXmppMaxRetryConnectionCount() {
		return xmppMaxRetryConnectionCount;
	}

	public void setXmppMaxRetryConnectionCount(int xmppMaxRetryConnectionCount) {
		this.xmppMaxRetryConnectionCount = xmppMaxRetryConnectionCount;
	}

	public int getXmppPacketReplayTimeout() {
		return xmppPacketReplayTimeout;
	}

	public void setXmppPacketReplayTimeout(int xmppPacketReplayTimeout) {
		this.xmppPacketReplayTimeout = xmppPacketReplayTimeout;
	}

	public Integer getXmppPingTimeout() {
		return xmppPingTimeout;
	}

	public void setXmppPingTimeout(Integer xmppPingTimeout) {
		this.xmppPingTimeout = xmppPingTimeout;
	}

	public Boolean getXmppUseSsl() {
		return xmppUseSsl;
	}

	public void setXmppUseSsl(Boolean xmppUseSsl) {
		this.xmppUseSsl = xmppUseSsl;
	}

	public String getAgentLdapBaseDn() {
		return agentLdapBaseDn;
	}

	public void setAgentLdapBaseDn(String agentLdapBaseDn) {
		this.agentLdapBaseDn = agentLdapBaseDn;
	}

	public String getAgentLdapIdAttribute() {
		return agentLdapIdAttribute;
	}

	public void setAgentLdapIdAttribute(String agentLdapIdAttribute) {
		this.agentLdapIdAttribute = agentLdapIdAttribute;
	}

	public String getAgentLdapJidAttribute() {
		return agentLdapJidAttribute;
	}

	public void setAgentLdapJidAttribute(String agentLdapJidAttribute) {
		this.agentLdapJidAttribute = agentLdapJidAttribute;
	}

	public String getAgentLdapObjectClasses() {
		return agentLdapObjectClasses;
	}

	public void setAgentLdapObjectClasses(String agentLdapObjectClasses) {
		this.agentLdapObjectClasses = agentLdapObjectClasses;
	}

	public String getAuthLdapUserSearchBase() {
		return authLdapUserSearchBase;
	}

	public void setAuthLdapUserSearchBase(String authLdapUserSearchBase) {
		this.authLdapUserSearchBase = authLdapUserSearchBase;
	}

	public String getAuthLdapUserObjectClasses() {
		return authLdapUserObjectClasses;
	}

	public void setAuthLdapUserObjectClasses(String authLdapUserObjectClasses) {
		this.authLdapUserObjectClasses = authLdapUserObjectClasses;
	}

	public String getAuthLdapUserAttribute() {
		return authLdapUserAttribute;
	}

	public void setAuthLdapUserAttribute(String authLdapUserAttribute) {
		this.authLdapUserAttribute = authLdapUserAttribute;
	}

	public Boolean getAuthorizationEnabled() {
		return authorizationEnabled;
	}

	public void setAuthorizationEnabled(Boolean authorizationEnabled) {
		this.authorizationEnabled = authorizationEnabled;
	}

	public Long getTaskManagerTaskTimeout() {
		return taskManagerTaskTimeout;
	}

	public void setTaskManagerTaskTimeout(Long taskManagerTaskTimeout) {
		this.taskManagerTaskTimeout = taskManagerTaskTimeout;
	}

	public Boolean getTaskManagerMulticastEnabled() {
		return taskManagerMulticastEnabled;
	}

	public void setTaskManagerMulticastEnabled(Boolean taskManagerMulticastEnabled) {
		this.taskManagerMulticastEnabled = taskManagerMulticastEnabled;
	}

	public Boolean getTaskManagerLogXmppMessagesEnabled() {
		return taskManagerLogXmppMessagesEnabled;
	}

	public void setTaskManagerLogXmppMessagesEnabled(Boolean taskManagerLogXmppMessagesEnabled) {
		this.taskManagerLogXmppMessagesEnabled = taskManagerLogXmppMessagesEnabled;
	}

}
