package tr.org.liderahenk.lider.core.api.messaging.enums;

/**
 * Types used when sending messages <b>from Lider to agents</b>.<br/>
 * <br/>
 * 
 * <b>EXECUTE_TASK</b>: Commands agent to execute a provided machine task.<br/>
 * <b>EXECUTE_POLICY</b>: Commands agent to execute a provided policy.<br/>
 * <b>EXECUTE_SCRIPT</b>: Commands agent to execute a provided script and return
 * its result as response.<br/>
 * <b>REQUEST_FILE</b>: Commands agent to send a desired file back to Lider.
 * <br/>
 * <b>MOVE_FILE</b>: Commands agent to move file in Ahenk file system. <br/>
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author <a href="mailto:bm.volkansahin@gmail.com">Volkan Şahin</a>
 * 
 */

public enum LiderMessageType {
	EXECUTE_TASK, EXECUTE_SCRIPT, EXECUTE_POLICY, REQUEST_FILE, MOVE_FILE, REGISTRATION_RESPONSE
}
