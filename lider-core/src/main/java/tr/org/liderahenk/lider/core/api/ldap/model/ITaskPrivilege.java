package tr.org.liderahenk.lider.core.api.ldap.model;

/**
 * 
 * Bean mapping of task privilege
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 */
public interface ITaskPrivilege {
	/**
	 * 
	 * @return target dn for privilege
	 */
	String getTarget();

	/**
	 * 
	 * @return operation permitted
	 */
	String getOperation();

}
