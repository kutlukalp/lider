package tr.org.liderahenk.lider.ldap.model;

import tr.org.liderahenk.lider.core.api.ldap.model.ITaskPrivilege;

/**
 * Default implementation of {@link ITaskPrivilege}
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class TaskPrivilegeImpl implements ITaskPrivilege {

	/**
	 * DN of the target entry
	 */
	private String target;

	/**
	 * operation either contains 'command class ID' or 'ALL'
	 */
	private String operation;

	/**
	 * 
	 * @param target
	 * @param operation
	 * @param allowed
	 */
	public TaskPrivilegeImpl(String target, String operation) {
		super();
		this.target = target;
		this.operation = operation;
	}

	@Override
	public String getTarget() {
		return target;
	}

	@Override
	public String getOperation() {
		return operation;
	}

	@Override
	public String toString() {
		return "TaskPrivilegeImpl [target=" + target + ", operation=" + operation + "]";
	}

}
