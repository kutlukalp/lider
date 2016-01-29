package tr.org.liderahenk.lider.impl.ldap;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.model.message.AddRequest;
import org.apache.directory.api.ldap.model.message.AddRequestImpl;
import org.apache.directory.api.ldap.model.message.AddResponse;
import org.apache.directory.api.ldap.model.message.LdapResult;
import org.apache.directory.api.ldap.model.message.ModifyRequest;
import org.apache.directory.api.ldap.model.message.ModifyRequestImpl;
import org.apache.directory.api.ldap.model.message.Response;
import org.apache.directory.api.ldap.model.message.ResultCodeEnum;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchResultEntry;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.schemamanager.impl.DefaultSchemaManager;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.apache.directory.ldap.client.api.PoolableLdapConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.IUser;
import tr.org.liderahenk.lider.core.api.auth.IUserPrivilege;
import tr.org.liderahenk.lider.core.api.caching.ICacheService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapException;
import tr.org.liderahenk.lider.core.api.ldap.LdapSearchFilterAttribute;
import tr.org.liderahenk.lider.core.api.plugin.ILdapExtension;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
 * Default implementation for {@link ILDAPService}
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * 
 */
public class LDAPServiceImpl implements ILDAPService {

	private final static Logger logger = LoggerFactory.getLogger(LDAPServiceImpl.class);

	/**
	 * 
	 */
	private LdapConnectionPool pool;

	/**
	 * 
	 */
	private String rootDn;
	/**
	 * 
	 */
	private String authLdapUserSearchBase;
	/**
	 * 
	 */
	private String ldapUserJidAttribute;

	/**
	 * 
	 */
	private IConfigurationService configurationService;
	/**
	 * 
	 */
	private ICacheService cacheService;

	/**
	 * 
	 */
	private String regex = "\\[(.+):(.+):(true|false)\\]";
	/**
	 * 
	 */
	private Pattern pattern = Pattern.compile(regex);

	public LDAPServiceImpl() {

	}

	public void init() throws Exception {
		logger.info("Initializing LDAP service");
		logger.info("ldap.server => {}", configurationService.getLdapServer());
		logger.info("ldap.port => {}", configurationService.getLdapPort());
		logger.info("ldap.user => {}", configurationService.getLdapUser());
		logger.info("ldap.password => {}", configurationService.getLdapPassword());
		logger.info("ldap.root.dn => {}", configurationService.getLdapRootDn());

		rootDn = configurationService.getLdapRootDn();
		authLdapUserSearchBase = configurationService.getAuthLdapUserSearchBase();
		ldapUserJidAttribute = configurationService.getAgentLdapJidAttribute();

		LdapConnectionConfig lconfig = new LdapConnectionConfig();
		lconfig.setLdapHost(configurationService.getLdapServer());
		lconfig.setLdapPort(Integer.parseInt(configurationService.getLdapPort()));
		lconfig.setName(configurationService.getLdapUser());
		lconfig.setCredentials(configurationService.getLdapPassword());
		// lconfig.setUseTls(true);
		lconfig.setUseSsl(configurationService.getLdapUseSsl());

		PoolableLdapConnectionFactory factory = new PoolableLdapConnectionFactory(lconfig);
		pool = new LdapConnectionPool(factory);
		pool.setTestOnBorrow(true);
		pool.setMaxActive(100);
		pool.setMaxWait(3000);
		pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		logger.info("finished initializing LDAP service");

	}

	public void destroy() {
		logger.info("Destroying LDAP service...");
		try {
			pool.close();
		} catch (Exception e) {
			logger.warn("error destroying LDAP service", e);
		}
	}

	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public void setCacheService(ICacheService cacheService) {
		this.cacheService = cacheService;
	}

	public LdapConnection getConnection() throws LdapException {
		LdapConnection connection = null;
		try {
			connection = pool.getConnection();
		} catch (Exception e) {
			throw new LdapException(e);
		}
		return connection;
	}

	public void releaseConnection(LdapConnection ldapConnection) {
		try {
			pool.releaseConnection(ldapConnection);
		} catch (Exception e) {
			logger.error(e.getMessage());
			// throw new LdapException(e);
		}
	}

	@Override
	public IUser getUser(String userDN) throws LdapException {

		LdapConnection connection = null;
		UserImpl user = null;

		user = (UserImpl) cacheService.get("ldap:getuser:" + userDN);

		if (user != null) {
			logger.debug("cache hit: user dn => {}", userDN);
			return user;
		}
		logger.debug("cache miss: user dn => {}, doing ldap search", userDN);
		try {
			connection = getConnection();
			Entry resultEntry = connection.lookup(userDN);
			if (null != resultEntry) {
				user = new UserImpl();

				if (null != resultEntry.get("liderPrivilege")) {
					user.setTargetDnPrivileges(new ArrayList<IUserPrivilege>());

					Iterator<Value<?>> iter = resultEntry.get("liderPrivilege").iterator();
					while (iter.hasNext()) {
						String privilege = iter.next().getValue().toString();
						logger.debug("found user privilege => {}", privilege);
						Matcher matcher = pattern.matcher(privilege);
						if (matcher.matches()) {
							user.getTargetDnPrivileges().add(new UserPrivilegeImpl(matcher.group(1), matcher.group(2),
									Boolean.valueOf(matcher.group(3))));

						} else {
							logger.warn("invalid pattern in privilege => {}, pattern => {}", privilege, pattern);
						}
					}
					// adding privileges from user's groups
					user.getTargetDnPrivileges().addAll(getGroupPrivileges(userDN));
				}
				logger.debug("putting user to cache: user dn => {}", userDN);
				cacheService.put("ldap:getuser:" + userDN, user);
				return user;
			}
			return null;
		} catch (Exception e) {
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}

	}

	private List<IUserPrivilege> getGroupPrivileges(String userDn) throws LdapException {
		logger.debug("will search group privileges for user {}", userDn);
		LdapConnection connection = null;
		List<IUserPrivilege> groupPrivileges = new ArrayList<IUserPrivilege>();

		try {
			connection = getConnection();

			String filter = "(&(objectClass=pardusLider)(member=$1))".replace("$1", userDn);

			logger.debug("search filter => {}", filter);
			logger.debug("search base dn => {}", this.rootDn);

			EntryCursor cursor = connection.search(this.rootDn, filter, SearchScope.SUBTREE);

			while (cursor.next()) {

				Entry entry = cursor.get();
				logger.debug("found group => {}", entry.getDn());

				if (null != entry) {

					if (null != entry.get("liderPrivilege")) {

						Iterator<Value<?>> iter = entry.get("liderPrivilege").iterator();
						while (iter.hasNext()) {

							String privilege = iter.next().getValue().toString();
							logger.debug("found privilege => {}", privilege);
							Matcher matcher = pattern.matcher(privilege);
							if (matcher.matches()) {
								groupPrivileges.add(new UserPrivilegeImpl(matcher.group(1), matcher.group(2),
										Boolean.valueOf(matcher.group(3))));
							} else {
								logger.warn("invalid pattern in privilege => {}, pattern => {}", privilege, pattern);
							}
						}
					} else {
						logger.debug("no privilege found in group => {}", entry.getDn());
					}
				}
			}
			logger.debug("finished processing group privileges for user {}", userDn);
		} catch (Exception e) {
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
		return groupPrivileges;
	}

	@Override
	public String getUserUID(String userDN) throws LdapException {

		String uidAttrName = configurationService.getAgentLdapIdAttribute();
		LdapConnection connection = null;
		Attribute uidAttr = null;
		String uid = null;
		try {
			connection = getConnection();
			Entry resultEntry = connection.lookup(userDN);
			if (null != resultEntry) {

				if (null != resultEntry.get(uidAttrName)) {
					uidAttr = resultEntry.get(uidAttrName);
					uid = uidAttr.getString();
				}
			}
		} catch (Exception e) {
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
		return uid;
	}

	@Override
	// FIXME SO SLOW IN PERFORMANCE TESTS!!
	public List<String> getChilds(String baseDn, String attributeName, String attributeValue, boolean recursive)
			throws LdapException {
		logger.debug("processing getChilds");
		LdapConnection connection = getConnection();
		List<String> childNodes = new ArrayList<String>();
		String filter = "";
		if (recursive) {
			filter = "(&(" + attributeName + "=" + attributeValue + ")(agentDnAhenk= " + baseDn + "))";
		} else {

			filter = "(" + attributeName + "=" + attributeValue + ")";
		}

		String agentDn = "dc=agents,dc=mys,dc=pardus,dc=org";
		EntryCursor cursor = null;

		try {

			SearchScope searchScope = SearchScope.SUBTREE;
			if (recursive) {
				cursor = connection.search(agentDn, filter, searchScope);
			} else {
				cursor = connection.search(baseDn, filter, searchScope);
			}

			while (cursor.next()) {
				childNodes.add(cursor.get().getDn().getName());
			}

		} catch (Exception e) {
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
		logger.debug("processed getChilds");
		return childNodes;

	}

	@Override
	public void addEntry(String newDn, Map<String, String[]> attributes) throws LdapException {
		LdapConnection connection = getConnection();
		try {
			Dn dn = new Dn(newDn);
			Entry entry = new DefaultEntry(dn);

			for (Map.Entry<String, String[]> Entry : attributes.entrySet()) {
				String[] entryValues = Entry.getValue();
				for (String value : entryValues) {
					entry.add(Entry.getKey(), value);
				}
			}

			AddRequest addRequest = new AddRequestImpl();
			addRequest.setEntry(entry);

			AddResponse addResponse = connection.add(addRequest);
			LdapResult ldapResult = addResponse.getLdapResult();

			if (ResultCodeEnum.SUCCESS.equals(ldapResult.getResultCode())) {
				return;
			} else {
				logger.error("could not create LDAP entry: {}", ldapResult.getDiagnosticMessage());
				throw new LdapException(ldapResult.getDiagnosticMessage());
			}
		} catch (Exception e) {
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
	}

	@Override
	public Entry getRootDSE() throws LdapException {
		LdapConnection connection = getConnection();
		Entry entry = null;
		try {
			entry = connection.getRootDse();
		} catch (org.apache.directory.api.ldap.model.exception.LdapException e) {
			logger.error(e.getMessage());
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
		return entry;
	}

	@Override
	public LdapEntry getEntry(String entryDn, String... requestedAttributes) {
		LdapConnection conn = null;
		try {
			conn = pool.getConnection();
			EntryCursor cursor = conn.search(entryDn, "(objectClass=*)", SearchScope.OBJECT, requestedAttributes);
			if (cursor.next()) {
				Entry entry = cursor.get();
				Map<String, String> attributes = new HashMap<String, String>();
				for (String attr : requestedAttributes) {
					try {
						attributes.put(attr, entry.get(attr).getString());
					} catch (Exception e) {
						logger.warn("cannot read attribute value: ", e.getMessage());
					}
				}
				return new LdapEntry(entryDn, attributes);
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			try {
				pool.releaseConnection(conn);
			} catch (Exception e) {
				logger.warn("", e);
			}
		}
		return null;
	}

	@Override
	public void updateEntry(String entryDn, String attribute, String value) throws LdapException {
		logger.info("Replacing attribute " + attribute + " value " + value);
		LdapConnection connection = null;

		connection = getConnection();
		Entry entry = null;
		try {
			entry = connection.lookup(entryDn);
			if (entry != null) {
				if (entry.get(attribute) != null) {
					Value<?> oldValue = entry.get(attribute).get();
					entry.remove(attribute, oldValue);
				}
				entry.add(attribute, value);
				connection.modify(entry, ModificationOperation.REPLACE_ATTRIBUTE);
			}
		} catch (Exception e) {
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
	}

	@Override
	public void updateEntryAddAtribute(String entryDn, String attribute, String value) throws LdapException {
		logger.info("Adding attribute " + attribute + " value " + value);
		LdapConnection connection = null;

		connection = getConnection();
		Entry entry = null;
		try {
			entry = connection.lookup(entryDn);
			if (entry != null) {
				entry.put(attribute, value);

				ModifyRequest mr = new ModifyRequestImpl();
				mr.setName(new Dn(entryDn));
				mr.add(attribute, value);

				connection.modify(mr);
			}
		} catch (Exception e) {
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}

	}

	@Override
	public void updateEntryRemoveAttribute(String entryDn, String attribute) throws LdapException {
		logger.info("Removing attribute " + attribute);
		LdapConnection connection = null;

		connection = getConnection();
		Entry entry = null;
		try {
			entry = connection.lookup(entryDn);
			if (entry != null) {

				for (Attribute a : entry.getAttributes()) {

					if (a.getAttributeType().getName().equalsIgnoreCase("owner")) {
						entry.remove(a);
					}
				}

				connection.modify(entry, ModificationOperation.REMOVE_ATTRIBUTE);
			}
		} catch (Exception e) {
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}

	}

	@Override
	public void updateEntryRemoveAttributeWithValue(String entryDn, String attribute, String value)
			throws LdapException {
		logger.info("Removing attribute " + attribute);
		LdapConnection connection = null;

		connection = getConnection();
		Entry entry = null;
		try {
			entry = connection.lookup(entryDn);
			if (entry != null) {

				for (Attribute a : entry.getAttributes()) {
					if (a.contains(value)) {
						a.remove(value);
					}
				}

				connection.modify(entry, ModificationOperation.REPLACE_ATTRIBUTE);
			}
		} catch (Exception e) {
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}

	}

	@Override
	public String getDN(String baseDn, String attributeName, String attributeValue) throws LdapException {

		LdapConnection connection = getConnection();
		String filter = "(" + attributeName + "=" + attributeValue + ")";
		SearchScope searchScope = SearchScope.OBJECT;

		try {
			EntryCursor cursor = connection.search(baseDn, filter, searchScope);
			while (cursor.next()) {
				logger.error("cursor:" + cursor.get().getDn().getName());
				return cursor.get().getDn().getName();
			}

		} catch (Exception e) {
			logger.error("Filter:" + filter + " baseDn: " + baseDn);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}

		return null;
	}

	@Override
	public String getDN(String jidAttributeValue) throws LdapException {
		LdapConnection connection = getConnection();
		String filter = "(&(" + this.ldapUserJidAttribute + "=" + jidAttributeValue + ")(objectClass=pardusDevice))";
		SearchScope searchScope = SearchScope.SUBTREE;

		try {
			EntryCursor cursor = connection.search(this.authLdapUserSearchBase, filter, searchScope);
			while (cursor.next()) {
				logger.error("cursor:" + cursor.get().getDn().getName());
				return cursor.get().getDn().getName();
			}

		} catch (Exception e) {
			logger.error("Filter:" + filter + " baseDn: " + this.authLdapUserSearchBase);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}

		return null;
	}

	@Override
	public List<LdapEntry> search(String baseDn, String attributeName, String attributeValue, String[] attributes)
			throws LdapException {
		List<LdapEntry> result = new ArrayList<LdapEntry>();
		Map<String, String> attrs = null;

		LdapConnection connection = null;

		try {
			connection = getConnection();
			SearchRequest req = new SearchRequestImpl();
			req.setScope(SearchScope.SUBTREE);
			for (String attr : attributes) {
				req.addAttributes(attr);
			}

			req.setTimeLimit(0);
			req.setBase(new Dn(baseDn));
			req.setFilter("(" + attributeName + "=" + attributeValue + ")");
			SearchCursor searchCursor = connection.search(req);
			while (searchCursor.next()) {
				Response response = searchCursor.get();
				attrs = new HashMap<String, String>();
				if (response instanceof SearchResultEntry) {
					Entry resultEntry = ((SearchResultEntry) response).getEntry();
					for (String attr : attributes) {

						attrs.put(attr, resultEntry.get(attr) != null ? resultEntry.get(attr).getString() : "");
					}

					result.add(new LdapEntry(resultEntry.getDn().toString(), attrs));

				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);

		}
		return result;
	}

	@Override
	public List<LdapEntry> search(String attributeName, String attributeValue, String... attributes)
			throws LdapException {
		return this.search(configurationService.getLdapRootDn(), attributeName, attributeValue, attributes);
	}

	public List<LdapEntry> search(List<LdapSearchFilterAttribute> filterAttributes, String... attributes)
			throws LdapException {
		List<LdapEntry> result = new ArrayList<LdapEntry>();
		Map<String, String> attrs = null;

		LdapConnection connection = null;

		try {
			connection = getConnection();
			SearchRequest req = new SearchRequestImpl();
			req.setScope(SearchScope.SUBTREE);
			for (String attr : attributes) {
				req.addAttributes(attr);
			}

			String searchFilterStr = "(&";

			for (LdapSearchFilterAttribute filterAttr : filterAttributes) {
				searchFilterStr = searchFilterStr + "(" + filterAttr.getAttributeName()
						+ filterAttr.getOperator().getOperator() + filterAttr.getAttributeValue() + ")";
			}
			searchFilterStr = searchFilterStr + ")";
			req.setTimeLimit(0);
			req.setBase(new Dn(configurationService.getLdapRootDn()));
			req.setFilter(searchFilterStr);
			SearchCursor searchCursor = connection.search(req);
			while (searchCursor.next()) {
				Response response = searchCursor.get();
				attrs = new HashMap<String, String>();
				if (response instanceof SearchResultEntry) {
					Entry resultEntry = ((SearchResultEntry) response).getEntry();
					for (String attr : attributes) {

						attrs.put(attr, resultEntry.get(attr) != null ? resultEntry.get(attr).getString() : "");
					}

					result.add(new LdapEntry(resultEntry.getDn().toString(), attrs));

				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);

		}
		return result;

	}

	@Override
	public List<String> getLyaUserJids() throws LdapException {

		logger.debug("searching for lya users");
		LdapConnection connection = getConnection();
		List<String> lyaUsers = new ArrayList<String>();

		String filter = "(objectClass=pardusUser)"; // TODO make it configurable
		String baseDn = this.authLdapUserSearchBase;
		String jidAttribute = this.ldapUserJidAttribute;

		EntryCursor cursor = null;

		try {

			SearchScope searchScope = SearchScope.SUBTREE;
			cursor = connection.search(baseDn, filter, searchScope, jidAttribute);

			while (cursor.next()) {
				lyaUsers.add(cursor.get().get(jidAttribute).getString());
			}

		} catch (Exception e) {
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
		logger.debug("returned results for lya users search: {}", lyaUsers.toString());
		return lyaUsers;

	}

	public void bindExtension(ILdapExtension extension) throws LdapException {
		logger.info("bind extension => {}", extension);
		LdapConnection connection = null;

		try {
			connection = getConnection();
			execute(connection, extension);
		} catch (Exception e) {
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
	}

	public void unbindExtension(ILdapExtension extension) throws LdapException {
		logger.info("unbind extension => {}", extension);
	}

	/**
	 * Opens the LDIF file and loads the entries into the context.
	 * 
	 * @return The count of entries created.
	 */
	public int execute(LdapConnection conn, ILdapExtension extension) {
		InputStream in = null;

		int count = 0;

		try {
			in = extension.getLdifFile();// getLdifStream();

			try {
				for (LdifEntry ldifEntry : new LdifReader(in)) {
					Dn dn = ldifEntry.getDn();

					if (ldifEntry.isEntry()) {
						Entry entry = ldifEntry.getEntry();

						try {
							if (conn.lookup(dn) != null) {
								logger.info("Found {}, will not create.", dn);
							} else {
								throw new Exception();
							}
						} catch (Exception e) {
							try {
								conn.add(new DefaultEntry(new DefaultSchemaManager(), entry));
								count++;
								logger.info("Created {}.", dn);
							} catch (org.apache.directory.api.ldap.model.exception.LdapException e1) {
								logger.info("Could not create entry " + entry, e1);
							}
						}
					} else {
						// modify
						List<Modification> items = ldifEntry.getModifications();

						try {
							conn.modify(dn, items.toArray(new Modification[items.size()]));
							logger.info("Modified: " + dn + " with modificationItems: " + items);
						} catch (org.apache.directory.api.ldap.model.exception.LdapException e) {
							logger.info("Could not modify: " + dn + " with modificationItems: " + items, e);
						}
					}
				}
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (Exception e) {
						logger.error("could not close ldif input stream: ", e);
					}
				}
			}
		} catch (FileNotFoundException fnfe) {
			logger.error("could not find ldif file: ", fnfe);
		} catch (Exception ioe) {
			logger.error("unexpected error reading ldif file: ", ioe);
		}

		return count;
	}

	public void deleteEntry(String dn) throws LdapException {
		logger.debug("processing deleteEntry(String dn)  delete Ldap entry...");
		LdapConnection connection = getConnection();
		try {
			connection.delete(new Dn(dn));
		} catch (Exception e) {
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
		logger.debug("processing deleteEntry(String dn)  delete Ldap entry...");
	}

	public List<String> getChilds(String baseDn) throws LdapException {
		logger.debug("processing getChilds returns dn objects");
		LdapConnection connection = getConnection();
		List<String> childNodes = new ArrayList<String>();
		String filter = "(objectClass=*)";
		EntryCursor cursor = null;

		try {

			SearchScope searchScope = SearchScope.ONELEVEL;
			cursor = connection.search(baseDn, filter, searchScope);

			while (cursor.next()) {
				childNodes.add(cursor.get().getDn().getName());
			}

		} catch (Exception e) {
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
		logger.debug("processed getChilds over basedn " + baseDn);
		return childNodes;
	}

	public void deleteEntry(Dn dn) throws LdapException {
		logger.debug("processing deleteEntry(Dn dn)  delete Ldap entry...");
		LdapConnection connection = getConnection();
		try {
			connection.delete(dn);
		} catch (Exception e) {
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
		logger.debug("Successfully processed deleteEntry(Dn dn)  delete Ldap entry...");

	}

	@Override
	public List<LdapEntry> findEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAhenk(LdapEntry entry) {
		// TODO Auto-generated method stub
		return false;
	}

}
