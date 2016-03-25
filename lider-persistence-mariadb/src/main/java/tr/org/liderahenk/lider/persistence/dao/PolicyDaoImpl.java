package tr.org.liderahenk.lider.persistence.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.enums.LiderMessageType;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecutePoliciesMessage;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPolicyDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;
import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;
import tr.org.liderahenk.lider.persistence.entities.PolicyImpl;

/**
 * Provides database access for policies. CRUD operations for policies and their
 * plugin or policy records should be handled via this service only.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy
 *
 */
public class PolicyDaoImpl implements IPolicyDao {

	private static Logger logger = LoggerFactory.getLogger(PolicyDaoImpl.class);

	private EntityManager entityManager;
	
	private final static String GET_LATEST_POLICY_QUERY = 
		"SELECT "
		+ " prof.PROFILE_ID, "
		+ " prof.ACTIVE, "
		+ " prof.CREATE_DATE, "
		+ " prof.DELETED, "
		+ " prof.DESCRIPTION, "
		+ " prof.LABEL, "
		+ " prof.MODIFY_DATE, "
		+ " prof.OVERRIDABLE, "
		+ " prof.PROFILE_DATA, "
		+ " prof.PLUGIN_ID, "
		+ " p.POLICY_VERSION, "
		+ " plugin.PLUGIN_NAME, "
		+ " plugin.PLUGIN_VERSION "
		+ " FROM "
		+ " C_PROFILE prof "
		+ " INNER JOIN "
		+ " C_POLICY_PROFILE pp ON (pp.PROFILE_ID = prof.PROFILE_ID) "
		+ " INNER JOIN "
		+ " C_POLICY p ON (p.POLICY_ID = pp.POLICY_ID) "
		+ " INNER JOIN "
		+ " C_PLUGIN plugin ON (plugin.PLUGIN_ID = prof.PLUGIN_ID) "
		+ " WHERE "
		+ " pp.POLICY_ID = "
		+ " (SELECT DISTINCT "
		+ " 	p.POLICY_ID "
		+ " 	FROM "
		+ " 	C_POLICY p "
		+ " 	INNER JOIN "
		+ " 	C_COMMAND c ON (c.POLICY_ID = p.POLICY_ID) "
		+ " 	INNER JOIN "
		+ " 	C_COMMAND_EXECUTION ce ON (ce.COMMAND_ID = c.COMMAND_ID) "
		+ " 	WHERE "
		+ " 	(ce.DN_TYPE = ?1 AND ce.DN = ?2) OR (ce.DN_TYPE = ?3 AND ce.DN IN ({0})) "
		+ " 	ORDER BY ce.CREATE_DATE DESC LIMIT 1)";

	public void init() {
		logger.info("Initializing policy DAO.");
	}

	public void destroy() {
		logger.info("Destroying policy DAO.");
	}

	@Override
	public IPolicy save(IPolicy policy) {
		PolicyImpl policyImpl = new PolicyImpl(policy);
		policyImpl.setCreateDate(new Date());
		policyImpl.setModifyDate(null);
		entityManager.persist(policyImpl);
		policyImpl.setPolicyVersion(policyImpl.getId() + "-1");
		logger.debug("IPolicy object persisted: {}", policyImpl.toString());
		return policyImpl;
	}

	@Override
	public PolicyImpl update(IPolicy policy) {
		PolicyImpl policyImpl = new PolicyImpl(policy);
		policyImpl.setModifyDate(new Date());
		policyImpl = entityManager.merge(policyImpl);
		logger.debug("IPolicy object merged: {}", policyImpl.toString());
		return policyImpl;
	}

	@Override
	public PolicyImpl saveOrUpdate(IPolicy policy) {
		PolicyImpl policyImpl = new PolicyImpl(policy);
		policyImpl.setModifyDate(new Date());
		policyImpl = entityManager.merge(policyImpl);
		logger.debug("IPolicy object merged: {}", policyImpl.toString());
		return policyImpl;
	}

	@Override
	public void delete(Long policyId) {
		PolicyImpl policyImpl = entityManager.find(PolicyImpl.class, policyId);
		// Never truly delete, just mark as deleted!
		policyImpl.setDeleted(true);
		policyImpl.setModifyDate(new Date());
		policyImpl = entityManager.merge(policyImpl);
		logger.debug("IPolicy object marked as deleted: {}", policyImpl.toString());
	}

	@Override
	public long countAll() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PolicyImpl find(Long policyId) {
		PolicyImpl policyImpl = entityManager.find(PolicyImpl.class, policyId);
		logger.debug("IPolicy object found: {}", policyImpl.toString());
		return policyImpl;
	}

	@Override
	public List<? extends IPolicy> findAll(Class<? extends IPolicy> obj, Integer maxResults) {
		List<PolicyImpl> policyList = entityManager
				.createQuery("select t from " + PolicyImpl.class.getSimpleName() + " t", PolicyImpl.class)
				.getResultList();
		logger.debug("IPolicy objects found: {}", policyList);
		return policyList;
	}

	@Override
	public List<? extends IPolicy> findByProperty(Class<? extends IPolicy> obj, String propertyName,
			Object propertyValue, Integer maxResults) {
		TypedQuery<PolicyImpl> query = entityManager.createQuery(
				"select t from " + PolicyImpl.class.getSimpleName() + " t where t." + propertyName + "= :propertyValue",
				PolicyImpl.class).setParameter("propertyValue", propertyValue);
		if (maxResults > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<PolicyImpl> policyList = query.getResultList();
		logger.debug("IPolicy objects found: {}", policyList);
		return policyList;
	}

	@Override
	public List<? extends IPolicy> findByProperties(Class<? extends IPolicy> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults) {
		orders = new ArrayList<PropertyOrder>();
		// TODO
//		PropertyOrder ord = new PropertyOrder("name", OrderType.ASC);
//		orders.add(ord);
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PolicyImpl> criteria = (CriteriaQuery<PolicyImpl>) builder.createQuery(PolicyImpl.class);
		Metamodel metamodel = entityManager.getMetamodel();
		EntityType<PolicyImpl> entityType = metamodel.entity(PolicyImpl.class);
		Root<PolicyImpl> from = (Root<PolicyImpl>) criteria.from(entityType);
		criteria.select(from);
		Predicate predicate = null;

		if (propertiesMap != null) {
			Predicate pred = null;
			for (Entry<String, Object> entry : propertiesMap.entrySet()) {
				if (entry.getValue() != null && !entry.getValue().toString().isEmpty()) {
					String[] key = entry.getKey().split("\\.");
					if (key.length > 1) {
						Join<Object, Object> join = null;
						for (int i = 0; i < key.length - 1; i++) {
							join = join != null ? join.join(key[i]) : from.join(key[i]);
						}
						pred = builder.equal(join.get(key[key.length-1]), entry.getValue());
					}
					else {
						pred = builder.equal(from.get(entry.getKey()), entry.getValue());
					}
					predicate = predicate == null ? pred : builder.and(predicate, pred);
				}
			}
			if (predicate != null) {
				criteria.where(predicate);
			}
		}

		if (orders != null && !orders.isEmpty()) {
			List<Order> orderList = new ArrayList<Order>();
			for (PropertyOrder order : orders) {
				orderList.add(order.getOrderType() == OrderType.ASC ? builder.asc(from.get(order.getPropertyName()))
						: builder.desc(from.get(order.getPropertyName())));
			}
			criteria.orderBy(orderList);
		}

		List<PolicyImpl> list = null;
		if (null != maxResults) {
			list = entityManager.createQuery(criteria).setMaxResults(maxResults).getResultList();
		} else {
			list = entityManager.createQuery(criteria).getResultList();
		}

		return list;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IExecutePoliciesMessage getLatestPolicy(String userDn, List<LdapEntry> groupDns, String userPolicyVersion) {

		StringBuilder builder;
		
		String groupDnParam;
		
		if (!groupDns.isEmpty()) {
			logger.debug("Preparing group DN parameter. groupDns.size(): " + groupDns.size());
			builder = new StringBuilder();
			for (int i = 0; i < groupDns.size(); i++) {
				LdapEntry entry = groupDns.get(i);

				if (i != groupDns.size() - 1) {
					builder.append("'" + entry.getDistinguishedName() + "',");
				} else {
					builder.append("'" + entry.getDistinguishedName() + "'");
				}
			}
			
			groupDnParam = builder.toString();
		} else {
			logger.debug("Group DN list is empty.");
			groupDnParam = "";
		}
		
		// TODO setParameter method of JPA does not work properly for native queries while giving a List or array as parameter
		// there may be some bug about this issue.
		String sql = GET_LATEST_POLICY_QUERY.replace("{0}", groupDnParam);
		
		logger.debug("Creating query.");
		Query query = entityManager.createNativeQuery(sql).setParameter(1, RestDNType.USER.getId()).setParameter(2, userDn).setParameter(3, RestDNType.GROUP.getId());

		logger.debug("Executing query.");
		final List<Object[]> resultList = query.getResultList();

		logger.debug("Getting latest policy version.");
		final String policyVersion = !resultList.isEmpty() ? (String) (resultList.get(0)[10]) : null;
		
		logger.debug("Getting profile list of policy.");
		final List<IProfile> profileList = !policyVersion.equals(userPolicyVersion) ? new ArrayList<IProfile>(): createProfileList(resultList);
		
		logger.debug("Creating message object.");
		IExecutePoliciesMessage message = new IExecutePoliciesMessage() {
			private static final long serialVersionUID = 7773614490334045662L;

			@Override
			public LiderMessageType getType() {
				return LiderMessageType.EXECUTE_POLICY;
			}
			
			@Override
			public Date getTimestamp() {
				return new Date();
			}
			
			@Override
			public String getRecipient() {
				return null;
			}
			
			@Override
			public String getUserPolicyVersion() {
				return policyVersion;
			}
			
			@Override
			public List<IProfile> getUserPolicyProfiles() {
				return profileList;
			}
			
			@Override
			public String getMachinePolicyVersion() {
				return null;
			}
			
			@Override
			public List<IProfile> getMachinePolicyProfiles() {
				return null;
			}
		};
		
		return message;
	}

	private List<IProfile> createProfileList(List<Object[]> resultList) {
		
		List<IProfile> profileList = new ArrayList<IProfile>();
		
		if (!resultList.isEmpty()) {
			for (final Object[] objArr : resultList) {
				IProfile profile = new IProfile() {
					
					private static final long serialVersionUID = 1245538060673249103L;
					
					@Override
					public Long getId() {
						return (Long) objArr[0];
					}
					
					@Override
					public boolean isActive() {
						return (Boolean) objArr[1];
					}
					
					@Override
					public Date getCreateDate() {
						return (Date) objArr[2];
					}
					
					@Override
					public boolean isDeleted() {
						return (Boolean) objArr[3];
					}
					
					@Override
					public String getDescription() {
						return (String) objArr[4];
					}
					
					@Override
					public String getLabel() {
						return (String) objArr[5];
					}
					
					@Override
					public Date getModifyDate() {
						return (Date) objArr[6];
					}
					
					@Override
					public boolean isOverridable() {
						return (Boolean) objArr[7];
					}
					
					@Override
					public byte[] getProfileDataBlob() {
						try {
							return (byte[]) objArr[8];
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
						return null;
					}
					
					@SuppressWarnings("unchecked")
					@Override
					public Map<String, Object> getProfileData() {
						try {
							Map<String, Object> map = new HashMap<String, Object>();
							map = new ObjectMapper().readValue(((byte[]) objArr[8]), Map.class);
							return map;
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
						return null;
					}
					
					@Override
					public String toJson() {
						try {
							return new ObjectMapper().writeValueAsString(this);
						} catch (JsonGenerationException e) {
							logger.error(e.getMessage(), e);
						} catch (JsonMappingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							logger.error(e.getMessage(), e);
						}
						return null;
					}
					
					@Override
					public IPlugin getPlugin() {
						IPlugin plugin = new IPlugin() {
							private static final long serialVersionUID = 7766957851982612882L;

							@Override
							public Long getId() {
								return (Long) objArr[9];
							}
							
							@Override
							public Date getCreateDate() {
								return null;
							}
							
							@Override
							public boolean isUserOriented() {
								return false;
							}
							
							@Override
							public boolean isPolicyPlugin() {
								return false;
							}
							
							@Override
							public boolean isMachineOriented() {
								return false;
							}
							
							@Override
							public boolean isDeleted() {
								return false;
							}
							
							@Override
							public boolean isActive() {
								return false;
							}
							
							@Override
							public String getVersion() {
								return (String) objArr[11];
							}
							
							@Override
							public List<? extends IProfile> getProfiles() {
								return null;
							}
							
							@Override
							public String getName() {
								return (String) objArr[10];
							}
							
							@Override
							public Date getModifyDate() {
								return null;
							}
							
							@Override
							public String getDescription() {
								return null;
							}
							
							@Override
							public void addProfile(IProfile profile) {
							}
						};
						
						return plugin;
					}
					
				};
				
				profileList.add(profile);
			}
		}
		return profileList;
	}

}
