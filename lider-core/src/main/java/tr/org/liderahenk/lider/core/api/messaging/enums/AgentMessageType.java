package tr.org.liderahenk.lider.core.api.messaging.enums;

/**
 * Types used when sending messages <b>from agents to Lider</b>.<br/>
 * <br/>
 * 
 * <b>TASK_STATUS</b>: Contains task-related messages.<br/>
 * <b>REGISTER</b>: Indicates that sender (agent) wants to register to the
 * system.<br/>
 * <b>REGISTER_LDAP</b>: Indicates that sender (agent) wants to register to the
 * ldap.<br/>
 * <b>UNREGISTER</b>: Indicates that sender (agent) wants to unregister from the
 * system.<br/>
 * <b>GET_POLICIES</b>: Agent sends this message during user login.<br/>
 * <b>LOGIN</b>: Agent sends this message for log purposes during user login.
 * <br/>
 * <b>LOGOUT</b>: Agent sends this message for log purposes during user logout.
 * <br/>
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author <a href="mailto:bm.volkansahin@gmail.com">Volkan Şahin</a>
 * 
 */
public enum AgentMessageType {
	TASK_STATUS(1), REGISTER(2), UNREGISTER(3), REGISTER_LDAP(4), GET_POLICIES(5), LOGIN(6), LOGOUT(7), POLICY_STATUS(
			8), MISSING_PLUGIN(9);

	private int id;

	private AgentMessageType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	/**
	 * Provide mapping enums with a fixed ID in JPA (a more robust alternative
	 * to EnumType.String and EnumType.Ordinal)
	 * 
	 * @param id
	 * @return related AgentMessageType enum
	 * @see http://blog.chris-ritchie.com/2013/09/mapping-enums-with-fixed-id-in
	 *      -jpa.html
	 * 
	 */
	public static AgentMessageType getType(Integer id) {
		if (id == null) {
			return null;
		}
		for (AgentMessageType position : AgentMessageType.values()) {
			if (id.equals(position.getId())) {
				return position;
			}
		}
		throw new IllegalArgumentException("No matching type for id: " + id);
	}

}
