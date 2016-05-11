package tr.org.liderahenk.lider.core.api.persistence.enums;

/**
 * Provides parameter types that can be used for report and report templates.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public enum ParameterType {
	// In case of new parameter type, make sure to update
	// ReportDaoImpl.validate() method as well!
	STRING(1), DATE(2), NUMBER(3);

	private int id;

	private ParameterType(int id) {
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
	 * @return related CrudType enum
	 * @see http://blog.chris-ritchie.com/2013/09/mapping-enums-with-fixed-id-in
	 *      -jpa.html
	 * 
	 */
	public static ParameterType getType(Integer id) {
		if (id == null) {
			return null;
		}
		for (ParameterType paramType : ParameterType.values()) {
			if (id.equals(paramType.getId())) {
				return paramType;
			}
		}
		throw new IllegalArgumentException("No matching type for id: " + id);
	}

}
