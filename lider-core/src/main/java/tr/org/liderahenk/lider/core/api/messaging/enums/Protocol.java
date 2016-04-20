package tr.org.liderahenk.lider.core.api.messaging.enums;

/**
 * Protocol enum is used to indicate how an agent can install the target plugin.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public enum Protocol {
	WGET(1), RSYNC(2), TORRENT(3);

	private int id;

	private Protocol(int id) {
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
	 * @return related Protocol enum
	 * @see http://blog.chris-ritchie.com/2013/09/mapping-enums-with-fixed-id-in
	 *      -jpa.html
	 * 
	 */
	public static Protocol getType(Integer id) {
		if (id == null) {
			return null;
		}
		for (Protocol position : Protocol.values()) {
			if (id.equals(position.getId())) {
				return position;
			}
		}
		throw new IllegalArgumentException("No matching type for id: " + id);
	}
}
