package tr.org.liderahenk.lider.impl.ldap;

import tr.org.liderahenk.lider.core.model.ldap.IUserPrivilege;

/**
 * Default implementation of {@link IUserPrivilege}
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class UserPrivilegeImpl implements IUserPrivilege {

	/**
	 * 
	 */
	private String target;

	/**
	 * 
	 */
	private String operation;

	/**
	 *
	 * @param target
	 * @param operation
	 * @param allowed
	 */
	private boolean allowed;

	/**
	 * 
	 * @param target
	 * @param operation
	 * @param allowed
	 */
	public UserPrivilegeImpl(String target, String operation, boolean allowed) {
		super();
		this.target = target;
		this.operation = operation;
		this.allowed = allowed;
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
	public boolean isAllowed() {
		return allowed;
	}

	@Override
	public String toString() {
		return "[" + this.getClass().getSimpleName() + " target: " + target + ", operation: " + operation
				+ ", allowed: " + allowed + " ]";
	}

}
