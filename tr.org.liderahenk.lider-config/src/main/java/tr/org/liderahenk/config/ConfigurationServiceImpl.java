package tr.org.liderahenk.config;

import tr.org.liderahenk.lider.core.api.IConfigurationService;


public class ConfigurationServiceImpl implements IConfigurationService{
	
	
	private String dbServer;
	private String dbDatabase;
	private Integer dbPort;
	private String dbUsername;
	private String dbPassword;
	/**
	 * 
	 */
	private String ldapServer;
	/**
	 * 
	 */
	private String ldapPort;
	/**
	 * 
	 */
	private String ldapUser;
	/**
	 * 
	 */
	private String ldapPassword;
	/**
	 * 
	 */
	private String ldapRootDn;
	
	/**
	 * 
	 */
	private Boolean ldapUseSsl;
	
	/**
	 * 
	 */
	private String xmppServer;//do we need internal/external xmpp server address/IP??
	/**
	 * 
	 */
	private String xmppServerPublicIP;// we will broadcast this xmpp server IP to external clients
	/**
	 * 
	 */
	private Integer xmppPort;
	/**
	 * 
	 */
	private String xmppJid;
	/**
	 * 
	 */
	private String xmppPassword;
	/**
	 * 
	 */
	private String xmppDomain;
	/**
	 * 
	 */
	private Integer xmppPingTimeout;
	/**
	 * 
	 */
	private String agentLdapBaseDn;
	/**
	 * 
	 */
	private String agentLdapIdAttribute;
	/**
	 * 
	 */
	private String agentLdapJidAttribute;
	
	/**
	 * 
	 */
	private String authLdapUserSearchBase;
	/**
	 * 
	 */
	private String authLdapUserFilter;
	/**
	 * 
	 */
	private String authLdapUserAttribute;
	/**
	 * 
	 */
	private String authLdapUserObjectClasses;
	
	private String userMailAttribute;
	
	/**
	 * 
	 */
	private Long taskManagerTaskTimeout;
	/**
	 * 
	 */
	private Boolean taskManagerMulticastEnabled;
	/**
	 * 
	 */
	private Boolean taskManagerLogXmppMessagesEnabled;
	
	/**
	 * 
	 */
	private Boolean authorizationEnabled;
	
	private String servers;
	
	private String privateKey;
	

	private Integer syslogRecordMaxQueryDay;
	
	private String mailAddress;
	
	private String mailPassword;
	
	private String mailHost;
	
	private Integer mailSmtpPort;
	
	private Boolean mailSmtpAuth;
	
	private Boolean mailSmtpStartTlsEnable;
	
	private Boolean mailSmtpSslEnable;
	
	private Integer mailSmtpConnTimeout;
	
	private Integer mailSmtpTimeout;
	
	private Integer mailSmtpWriteTimeout;
	
	@Override
	public String getLdapServer() {
		return ldapServer;
	}

	public void setLdapServer(String ldapServer) {
		this.ldapServer = ldapServer;
	}
	
	@Override
	public String getLdapPort() {
		return ldapPort;
	}
	
	public void setLdapPort(String ldapPort) {
		this.ldapPort = ldapPort;
	}

	@Override
	public String getLdapUser() {
		return ldapUser;
	}
	
	public void setLdapUser(String ldapUser) {
		this.ldapUser = ldapUser;
	}

	@Override
	public String getLdapPassword() {
		return ldapPassword;
	}
	
	public void setLdapPassword(String ldapPassword) {
		this.ldapPassword = ldapPassword;
	}
	
	@Override
	public String getLdapRootDn() {
		return ldapRootDn;
	}
	
	public void setLdapRootDn(String ldapRootDn) {
		this.ldapRootDn = ldapRootDn;
	}
	
	@Override
	public Boolean getLdapUseSsl() {
		return ldapUseSsl;
	}
	
	public void setLdapUseSsl(Boolean ldapUseSsl) {
		this.ldapUseSsl = ldapUseSsl;
	}
	
	@Override
	public String getXmppServer() {
		return xmppServer;
	}

	public void setXmppServer(String xmppServer) {
		this.xmppServer = xmppServer;
	}
	
	@Override
	public String getXmppServerPublicIP() {
		return xmppServerPublicIP;
	}
	
	public void setXmppServerPublicIP(String xmppServerPublic) {
		this.xmppServerPublicIP = xmppServerPublic;
	}
	
	@Override
	public String getXmppDomain() {
		return xmppDomain;
	}
	
	public void setXmppDomain(String xmppDomain) {
		this.xmppDomain = xmppDomain;
	}

	@Override
	public Integer getXmppPort() {
		return xmppPort;
	}

	public void setXmppPort(Integer xmppPort) {
		this.xmppPort = xmppPort;
	}

	@Override
	public String getXmppJid() {
		return xmppJid;
	}

	public void setXmppJid(String xmppJid) {
		this.xmppJid = xmppJid;
	}

	@Override
	public String getXmppPassword() {
		return xmppPassword;
	}

	public void setXmppPassword(String xmppPassword) {
		this.xmppPassword = xmppPassword;
	}
	
	@Override
	public Integer getXmppPingTimeout() {
		return xmppPingTimeout;
	}
	
	public void setXmppPingTimeout(Integer xmppPingTimeout) {
		this.xmppPingTimeout = xmppPingTimeout;
	}

	@Override
	public String getAgentLdapBaseDn() {
		return agentLdapBaseDn;
	}

	public void setAgentLdapBaseDn(String agentLdapBaseDn) {
		this.agentLdapBaseDn = agentLdapBaseDn;
	}

	@Override
	public String getAgentLdapIdAttribute() {
		return agentLdapIdAttribute;
	}

	public void setAgentLdapIdAttribute(String agentLdapIdAttribute) {
		this.agentLdapIdAttribute = agentLdapIdAttribute;
	}

	@Override
	public String getAgentLdapJidAttribute() {
		return agentLdapJidAttribute;
	}

	public void setAgentLdapJidAttribute(String agentLdapJidAttribute) {
		this.agentLdapJidAttribute = agentLdapJidAttribute;
	}

	@Override
	public String getAuthLdapUserSearchBase() {
		return authLdapUserSearchBase;
	}
	
	public void setAuthLdapUserSearchBase(String authLdapUserSearchBase) {
		this.authLdapUserSearchBase = authLdapUserSearchBase;
	}

	@Override
	public String getAuthLdapUserFilter() {
		return authLdapUserFilter;
	}

	public void setAuthLdapUserFilter(String authLdapUserFilter) {
		this.authLdapUserFilter = authLdapUserFilter;
	}

	@Override
	public String getAuthLdapUserAttribute() {
		return authLdapUserAttribute;
	}

	public void setAuthLdapUserAttribute(String authLdapUserAttribute) {
		this.authLdapUserAttribute = authLdapUserAttribute;
	}

	@Override
	public Long getTaskManagerTaskTimeout() {
		return taskManagerTaskTimeout;
	}
	
	public void setTaskManagerTaskTimeout(Long taskManagerTaskTimeout) {
		this.taskManagerTaskTimeout = taskManagerTaskTimeout;
	}

	@Override
	public Boolean getTaskManagerMulticastEnabled() {
		return taskManagerMulticastEnabled;
	}
	
	public void setTaskManagerMulticastEnabled(
			Boolean taskManagerMulticastEnabled) {
		this.taskManagerMulticastEnabled = taskManagerMulticastEnabled;
	}

	@Override
	public Boolean getTaskManagerLogXmppMessagesEnabled() {
		return taskManagerLogXmppMessagesEnabled;
	}
	
	public void setTaskManagerLogXmppMessagesEnabled(
			Boolean taskManagerLogXmppMessagesEnabled) {
		this.taskManagerLogXmppMessagesEnabled = taskManagerLogXmppMessagesEnabled;
	}
	
	@Override
	public Boolean getAuthorizationEnabled() {
		return authorizationEnabled;
	}
	
	public void setAuthorizationEnabled(Boolean authorizationEnabled) {
		this.authorizationEnabled = authorizationEnabled;
	}

	@Override
	public String getAuthLdapUserObjectClasses() {
		return authLdapUserObjectClasses;
	}
	
	public void setAuthLdapUserObjectClasses(String authLdapUserObjectClasses) {
		this.authLdapUserObjectClasses = authLdapUserObjectClasses;
	}

	@Override
	public String getServers() {
		return servers;
	}
	
	public void setServers(String servers) {
		this.servers = servers;
	}

	@Override
	public String getPrivateKey() {
		return privateKey;
	}
	
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}


	public Integer getSyslogRecordMaxQueryDay() {
		return this.syslogRecordMaxQueryDay;
	}

	public void setSyslogRecordMaxQueryDay(Integer syslogRecordMaxQueryDay) {
		this.syslogRecordMaxQueryDay = syslogRecordMaxQueryDay;
	}

	@Override
	public String getMailAddress() {
		return this.mailAddress;
	}

	@Override
	public Integer getMailSmtpPort() {
		return this.mailSmtpPort;
	}

	@Override
	public Boolean getMailSmtpAuth() {
		return this.mailSmtpAuth;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public void setMailSmtpPort(Integer mailSmtpPort) {
		this.mailSmtpPort = mailSmtpPort;
	}

	public void setMailSmtpAuth(Boolean mailSmtpAuth) {
		this.mailSmtpAuth = mailSmtpAuth;
	}

	@Override
	public String getMailPassword() {
		return this.mailPassword;
	}

	@Override
	public String getMailHost() {
		return this.mailHost;
	}

	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}

	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	@Override
	public String getUserMailAttribute() {
		return this.userMailAttribute;
	}

	public void setUserMailAttribute(String userMailAttribute) {
		this.userMailAttribute = userMailAttribute;
	}

	@Override
	public Boolean getMailSmtpStartTlsEnable() {
		return mailSmtpStartTlsEnable;
	}

	public void setMailSmtpStartTlsEnable(Boolean mailSmtpStartTlsEnable) {
		this.mailSmtpStartTlsEnable = mailSmtpStartTlsEnable;
	}

	@Override
	public Boolean getMailSmtpSslEnable() {
		return mailSmtpSslEnable;
	}

	public void setMailSmtpSslEnable(Boolean mailSmtpSslEnable) {
		this.mailSmtpSslEnable = mailSmtpSslEnable;
	}

	@Override
	public Integer getMailSmtpConnTimeout() {
		return mailSmtpConnTimeout;
	}

	public void setMailSmtpConnTimeout(Integer mailSmtpConnTimeout) {
		this.mailSmtpConnTimeout = mailSmtpConnTimeout;
	}

	@Override
	public Integer getMailSmtpTimeout() {
		return mailSmtpTimeout;
	}

	public void setMailSmtpTimeout(Integer mailSmtpTimeout) {
		this.mailSmtpTimeout = mailSmtpTimeout;
	}

	@Override
	public Integer getMailSmtpWriteTimeout() {
		return mailSmtpWriteTimeout;
	}

	public void setMailSmtpWriteTimeout(Integer mailSmtpWriteTimeout) {
		this.mailSmtpWriteTimeout = mailSmtpWriteTimeout;
	}

	public String getDbServer() {
		return dbServer;
	}

	public void setDbServer(String dbServer) {
		this.dbServer = dbServer;
	}

	public String getDbDatabase() {
		return dbDatabase;
	}

	public void setDbDatabase(String dbDatabase) {
		this.dbDatabase = dbDatabase;
	}

	public Integer getDbPort() {
		return dbPort;
	}

	public void setDbPort(Integer dbPort) {
		this.dbPort = dbPort;
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

}
