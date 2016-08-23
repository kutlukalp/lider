package tr.org.liderahenk.lider.ldap.model;

import tr.org.liderahenk.lider.core.api.ldap.model.IReportPrivilege;

/**
 * Default implementation of {@link IReportPrivilege}
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ReportPrivilegeImpl implements IReportPrivilege {

	/**
	 * Unique report code
	 */
	private String reportCode;

	/**
	 * 
	 * @param reportCode
	 * @param allowed
	 */
	public ReportPrivilegeImpl(String reportCode) {
		super();
		this.reportCode = reportCode;
	}

	@Override
	public String getReportCode() {
		return reportCode;
	}

	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}

}
