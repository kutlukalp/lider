package tr.org.liderahenk.lider.core.api.messaging;

/**
 * Message types used between agents and Lider XMPP client.<br/>
 * <br/>
 * 
 * <b>TASK_STATUS</b>: Contains task-related messages.<br/>
 * <b>REGISTER</b>: Indicates that sender (agent) wants to register to the
 * system.<br/>
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
 * @see tr.org.liderahenk.lider.impl.messaging.XMPPClientImpl
 * 
 */
public enum MessageType {
	TASK_STATUS, REGISTER, UNREGISTER, GET_POLICIES, LOGIN, LOGOUT
}
