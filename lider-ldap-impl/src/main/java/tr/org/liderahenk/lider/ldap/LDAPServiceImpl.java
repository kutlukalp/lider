package tr.org.liderahenk.lider.ldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.entry.Value;
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
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.apache.directory.ldap.client.api.PoolableLdapConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.caching.ICacheService;
import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapSearchFilterAttribute;
import tr.org.liderahenk.lider.core.api.ldap.enums.LdapSearchFilterEnum;
import tr.org.liderahenk.lider.core.api.ldap.exception.LdapException;
import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;
import tr.org.liderahenk.lider.core.model.ldap.IUser;
import tr.org.liderahenk.lider.core.model.ldap.IUserPrivilege;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
 * Default implementation for {@link ILDAPService}
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class LDAPServiceImpl implements ILDAPService {

	private final static Logger logger = LoggerFactory.getLogger(LDAPServiceImpl.class);

	private IConfigurationService configurationService;
	private ICacheService cacheService;

	private LdapConnectionPool pool;
	private Pattern pattern = Pattern.compile("\\[(.+):(.+):(true|false)\\]");

	public void init() throws Exception {

		LdapConnectionConfig lconfig = new LdapConnectionConfig();
		lconfig.setLdapHost(configurationService.getLdapServer());
		lconfig.setLdapPort(Integer.parseInt(configurationService.getLdapPort()));
		lconfig.setName(configurationService.getLdapUsername());
		lconfig.setCredentials(configurationService.getLdapPassword());
		// lconfig.setUseTls(true);
		lconfig.setUseSsl(configurationService.getLdapUseSsl());

		// Create connection pool
		PoolableLdapConnectionFactory factory = new PoolableLdapConnectionFactory(lconfig);
		pool = new LdapConnectionPool(factory);
		pool.setTestOnBorrow(true);
		pool.setMaxActive(100);
		pool.setMaxWait(3000);
		pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);

		logger.debug(this.toString());
	}

	public void destroy() {
		logger.info("Destroying LDAP service...");
		try {
			pool.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @return new LDAP connection
	 * @throws LdapException
	 */
	@Override
	public LdapConnection getConnection() throws LdapException {
		LdapConnection connection = null;
		try {
			connection = pool.getConnection();
		} catch (Exception e) {
			throw new LdapException(e);
		}
		return connection;
	}

	/**
	 * Try to release specified connection
	 * 
	 * @param ldapConnection
	 */
	@Override
	public void releaseConnection(LdapConnection ldapConnection) {
		try {
			pool.releaseConnection(ldapConnection);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public IUser getUser(String userDN) throws LdapException {

		LdapConnection connection = null;
		UserImpl user = null;

		user = (UserImpl) cacheService.get("ldap:getuser:" + userDN);

		if (user != null) {
			logger.debug("Cache hit. User DN: {}", userDN);
			return user;
		}

		logger.debug("Cache miss: user DN: {}, doing ldap search", userDN);
		try {
			connection = getConnection();
			Entry resultEntry = connection.lookup(userDN);
			if (null != resultEntry) {
				user = new UserImpl();

				if (null != resultEntry.get(configurationService.getUserLdapUidAttribute())) {
					// Set user's UID/JID
					user.setUid(resultEntry.get(configurationService.getUserLdapUidAttribute()).getString());
				}

				if (null != resultEntry.get(configurationService.getUserLdapPrivilegeAttribute())) {
					// Set target DN Privileges
					user.setTargetDnPrivileges(new ArrayList<IUserPrivilege>());
					Iterator<Value<?>> iter = resultEntry.get(configurationService.getUserLdapPrivilegeAttribute())
							.iterator();
					while (iter.hasNext()) {
						String privilege = iter.next().getValue().toString();
						logger.debug("Found user privilege: {}", privilege);
						Matcher matcher = pattern.matcher(privilege);
						if (matcher.matches()) {
							user.getTargetDnPrivileges().add(new UserPrivilegeImpl(matcher.group(1), matcher.group(2),
									Boolean.valueOf(matcher.group(3))));
						} else {
							logger.warn("Invalid pattern in privilege => {}, pattern => {}", privilege, pattern);
						}
					}
					// adding privileges from user's groups
					user.getTargetDnPrivileges().addAll(getGroupPrivileges(userDN));
				}
				logger.debug("Putting user to cache: user DN: {}", userDN);
				cacheService.put("ldap:getuser:" + userDN, user);
				return user;
			}
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}

	}

	// TODO
	// TODO
	// TODO
	private List<IUserPrivilege> getGroupPrivileges(String userDn) throws LdapException {

		List<IUserPrivilege> groupPrivileges = new ArrayList<IUserPrivilege>();
		LdapConnection connection = null;
		EntryCursor cursor = null;

		try {
			connection = getConnection();

			String filter = "(&(objectClass=pardusLider)(member=$1))".replace("$1", userDn);
			cursor = connection.search(configurationService.getLdapRootDn(), filter, SearchScope.SUBTREE);

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
								logger.warn("Invalid pattern in privilege => {}, pattern => {}", privilege, pattern);
							}
						}
					} else {
						logger.debug("No privilege found in group => {}", entry.getDn());
					}
				}
			}
			logger.debug("Finished processing group privileges for user {}", userDn);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			releaseConnection(connection);
		}

		return groupPrivileges;
	}

	/**
	 * Create new LDAP entry
	 */
	@Override
	public void addEntry(String newDn, Map<String, String[]> attributes) throws LdapException {

		LdapConnection connection = null;

		try {
			connection = getConnection();

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
				logger.error("Could not create LDAP entry: {}", ldapResult.getDiagnosticMessage());
				throw new LdapException(ldapResult.getDiagnosticMessage());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
	}

	/**
	 * Delete specified LDAP entry
	 * 
	 * @param dn
	 * @throws LdapException
	 */
	@Override
	public void deleteEntry(String dn) throws LdapException {
		LdapConnection connection = getConnection();
		try {
			connection.delete(new Dn(dn));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
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
			logger.error(e.getMessage(), e);
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
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
	}

	@Override
	public void updateEntryRemoveAttribute(String entryDn, String attribute) throws LdapException {

		logger.info("Removing attribute: {}", attribute);
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
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
	}

	@Override
	public void updateEntryRemoveAttributeWithValue(String entryDn, String attribute, String value)
			throws LdapException {

		logger.info("Removing attribute: {}", attribute);
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
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}

	}

	/**
	 * @return LDAP root DN
	 */
	@Override
	public Entry getRootDSE() throws LdapException {
		LdapConnection connection = getConnection();
		Entry entry = null;
		try {
			entry = connection.getRootDse();
		} catch (org.apache.directory.api.ldap.model.exception.LdapException e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
		return entry;
	}

	@Override
	public LdapEntry getEntry(String entryDn, String[] returningAttributes) throws LdapException {

		LdapConnection conn = null;
		EntryCursor cursor = null;

		try {
			conn = getConnection();

			// Add 'objectClass' to requested attributes to determine entry type
			Set<String> requestAttributeSet = new HashSet<String>();
			requestAttributeSet.add("objectClass");
			if (returningAttributes != null) {
				requestAttributeSet.addAll(Arrays.asList(returningAttributes));
			}

			// Search for entries
			cursor = conn.search(entryDn, "(objectClass=*)", SearchScope.OBJECT,
					requestAttributeSet.toArray(new String[requestAttributeSet.size()]));
			if (cursor.next()) {
				Entry entry = cursor.get();
				Map<String, String> attributes = new HashMap<String, String>();
				for (String attr : returningAttributes) {
					try {
						attributes.put(attr, entry.get(attr).getString());
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
				return new LdapEntry(entryDn, attributes, convertObjectClass2DNType(entry.get("objectClass")));
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			releaseConnection(conn);
		}
	}

	@Override
	public String getDN(String baseDn, String attributeName, String attributeValue) throws LdapException {

		LdapConnection connection = null;
		EntryCursor cursor = null;

		String filter = "(" + attributeName + "=" + attributeValue + ")";

		try {
			connection = getConnection();
			cursor = connection.search(baseDn, filter, SearchScope.SUBTREE);
			while (cursor.next()) {
				return cursor.get().getDn().getName();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			releaseConnection(connection);
		}

		return null;
	}

	/**
	 * Main search method for LDAP entries.
	 * 
	 * @param baseDn
	 *            search base DN
	 * @param filterAttributes
	 *            filtering attributes used to construct query condition
	 * @param returningAttributes
	 *            returning attributes
	 * @return list of LDAP entries
	 * @throws LdapException
	 */
	@Override
	public List<LdapEntry> search(String baseDn, List<LdapSearchFilterAttribute> filterAttributes,
			String[] returningAttributes) throws LdapException {

		List<LdapEntry> result = new ArrayList<LdapEntry>();
		LdapConnection connection = null;

		Map<String, String> attrs = null;

		try {
			connection = getConnection();

			SearchRequest req = new SearchRequestImpl();
			req.setScope(SearchScope.SUBTREE);

			// Add 'objectClass' to requested attributes to determine entry type
			Set<String> requestAttributeSet = new HashSet<String>();
			requestAttributeSet.add("objectClass");
			if (returningAttributes != null) {
				requestAttributeSet.addAll(Arrays.asList(returningAttributes));
			}
			req.addAttributes(requestAttributeSet.toArray(new String[requestAttributeSet.size()]));

			// Construct filter expression
			String searchFilterStr = "(&";
			for (LdapSearchFilterAttribute filterAttr : filterAttributes) {
				searchFilterStr = searchFilterStr + "(" + filterAttr.getAttributeName()
						+ filterAttr.getOperator().getOperator() + filterAttr.getAttributeValue() + ")";
			}
			searchFilterStr = searchFilterStr + ")";
			req.setFilter(searchFilterStr);

			req.setTimeLimit(0);
			req.setBase(new Dn(baseDn));

			SearchCursor searchCursor = connection.search(req);
			while (searchCursor.next()) {
				Response response = searchCursor.get();
				attrs = new HashMap<String, String>();
				if (response instanceof SearchResultEntry) {
					Entry entry = ((SearchResultEntry) response).getEntry();
					if (returningAttributes != null) {
						for (String attr : returningAttributes) {
							attrs.put(attr, entry.get(attr) != null ? entry.get(attr).getString() : "");
						}
					}
					result.add(new LdapEntry(entry.getDn().toString(), attrs,
							convertObjectClass2DNType(entry.get("objectClass"))));
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

	/**
	 * Convenience method for main search method
	 */
	@Override
	public List<LdapEntry> search(List<LdapSearchFilterAttribute> filterAttributes, String[] returningAttributes)
			throws LdapException {
		return search(configurationService.getLdapRootDn(), filterAttributes, returningAttributes);
	}

	/**
	 * Yet another convenience method for main search method
	 */
	@Override
	public List<LdapEntry> search(String attributeName, String attributeValue, String[] returningAttributes)
			throws LdapException {
		List<LdapSearchFilterAttribute> filterAttributes = new ArrayList<LdapSearchFilterAttribute>();
		filterAttributes.add(new LdapSearchFilterAttribute(attributeName, attributeValue, LdapSearchFilterEnum.EQ));
		return search(configurationService.getLdapRootDn(), filterAttributes, returningAttributes);
	}

	// TODO handle situation where entry.getType is null or invalid
	@Override
	public boolean isAhenk(LdapEntry entry) {
		return entry.getType() == RestDNType.AHENK;
	}

	// TODO handle situation where entry.getType is null or invalid
	@Override
	public boolean isUser(LdapEntry entry) {
		return entry.getType() == RestDNType.USER;
	}

	/**
	 * Find target entries which subject to command execution from provided DN
	 * list.
	 * 
	 * @param dnList
	 *            a collection of DN strings. Each DN may point to AGENT, USER,
	 *            GROUP or ORGANIZATIONAL_UNIT
	 * @param dnType
	 *            indicates which types to search for. (possible values: AGENT,
	 *            USER, GROUP, ALL)
	 * @return
	 */
	@Override
	public List<LdapEntry> findTargetEntries(List<String> dnList, RestDNType dnType) {
		List<LdapEntry> entries = null;
		if (dnList != null && !dnList.isEmpty() && dnType != null) {

			// TODO improvement returning attributes should be a parameter!

			// Determine returning attributes
			// User LDAP privilege is used during authorization and agent JID
			// attribute is used during task execution
			String[] returningAttributes = new String[] { configurationService.getUserLdapPrivilegeAttribute(),
					configurationService.getAgentLdapJidAttribute() };

			// Construct filtering attributes
			String objectClasses = convertDNType2ObjectClass(dnType);
			logger.debug("Object classes: {}", objectClasses);
			List<LdapSearchFilterAttribute> filterAttributes = new ArrayList<LdapSearchFilterAttribute>();
			// There may be multiple object classes
			String[] objectClsArr = objectClasses.split(",");
			for (String objectClass : objectClsArr) {
				LdapSearchFilterAttribute fAttr = new LdapSearchFilterAttribute("objectClass", objectClass,
						LdapSearchFilterEnum.EQ);
				filterAttributes.add(fAttr);
			}
			logger.debug("Filtering attributes: {}", filterAttributes);

			entries = new ArrayList<LdapEntry>();

			// For each DN, find its target (child) entries according to desired
			// DN type:
			for (String dn : dnList) {
				try {
					List<LdapEntry> result = this.search(dn, filterAttributes, returningAttributes);
					if (result != null && !result.isEmpty()) {
						for (LdapEntry entry : result) {
							if (isValidType(entry.getType(), dnType)) {
								entries.add(entry);
							}
						}
					}
				} catch (LdapException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		logger.debug("Target entries: {}", entries);
		return entries;
	}

	/**
	 * 
	 * @param type
	 * @param desiredType
	 *            possible values: AGENT, USER, GROUP, ALL
	 * @return true if provided type is desired type (or its child), false
	 *         otherwise.
	 */
	private boolean isValidType(RestDNType type, RestDNType desiredType) {
		return type == desiredType || (desiredType == RestDNType.ALL
				&& (type == RestDNType.AHENK || type == RestDNType.USER || type == RestDNType.GROUP));
	}

	/**
	 * Determine and return object classes to be used according to provided DN
	 * type.
	 * 
	 * @param dnType
	 * @return
	 */
	private String convertDNType2ObjectClass(RestDNType dnType) {
		if (RestDNType.AHENK == dnType) {
			return configurationService.getAgentLdapObjectClasses();
		} else if (RestDNType.USER == dnType) {
			return configurationService.getUserLdapObjectClasses();
		} else if (RestDNType.GROUP == dnType) {
			return configurationService.getGroupLdapObjectClasses();
		} else if (RestDNType.ALL == dnType) {
			return "*";
		} else {
			throw new IllegalArgumentException("DN type was invalid.");
		}
	}

	/**
	 * Determine DN type for given objectClass attribute
	 * 
	 * @param attribute
	 * @return
	 */
	private RestDNType convertObjectClass2DNType(Attribute objectClass) {
		// Check if agent
		String agentObjectClasses = configurationService.getAgentLdapObjectClasses();
		boolean isAgent = objectClass.contains(agentObjectClasses.split(","));
		if (isAgent) {
			return RestDNType.AHENK;
		}
		// Check if user
		String userObjectClasses = configurationService.getUserLdapObjectClasses();
		boolean isUser = objectClass.contains(userObjectClasses.split(","));
		if (isUser) {
			return RestDNType.USER;
		}
		// Check if group
		String groupObjectClasses = configurationService.getGroupLdapObjectClasses();
		boolean isGroup = objectClass.contains(groupObjectClasses.split(","));
		if (isGroup) {
			return RestDNType.GROUP;
		}
		return null;
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
	 * @param cacheService
	 */
	public void setCacheService(ICacheService cacheService) {
		this.cacheService = cacheService;
	}

}
