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
 * <b>INSTALL_PLUGIN</b>: Commands agent to install new plugin according to
 * provided parameters. <br/>
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author <a href="mailto:bm.volkansahin@gmail.com">Volkan Şahin</a>
 * 
 */
public enum LiderMessageType {
	EXECUTE_TASK(1), EXECUTE_SCRIPT(2), EXECUTE_POLICY(3), REGISTRATION_RESPONSE(4), INSTALL_PLUGIN(
			5), PLUGIN_NOT_FOUND(6), RESPONSE_AGREEMENT(7);

	private int id;

	private LiderMessageType(int id) {
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
	 * @return related LiderMessageType enum
	 * @see http://blog.chris-ritchie.com/2013/09/mapping-enums-with-fixed-id-in
	 *      -jpa.html
	 * 
	 */
	public static LiderMessageType getType(Integer id) {
		if (id == null) {
			return null;
		}
		for (LiderMessageType type : LiderMessageType.values()) {
			if (id.equals(type.getId())) {
				return type;
			}
		}
		throw new IllegalArgumentException("No matching type for id: " + id);
	}

}
