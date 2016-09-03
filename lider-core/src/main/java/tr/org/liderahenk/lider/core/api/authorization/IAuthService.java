package tr.org.liderahenk.lider.core.api.authorization;

import java.util.List;

import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportView;

/**
 * Provides authorization services
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IAuthService {

	/**
	 * Calculate 'permitted' LDAP entries of the specified user for the
	 * specified target operation.
	 * 
	 * @param userDn
	 * @param entries
	 * @param targetOperation
	 * @return
	 */
	List<LdapEntry> getPermittedEntries(String userDn, List<LdapEntry> entries, String targetOperation);

	/**
	 * Calculate 'permitted' report templates of the specified user
	 * 
	 * @param userDn
	 * @param templates
	 * @return
	 */
	List<? extends IReportTemplate> getPermittedTemplates(String userDn, List<? extends IReportTemplate> templates);

	/**
	 * Calculate 'permitted' report views of the specified user
	 * 
	 * @param userDn
	 * @param views
	 * @return
	 */
	List<? extends IReportView> getPermittedViews(String userDn, List<? extends IReportView> views);

	/**
	 * Check if the specified user can view/generate the specified report.
	 * 
	 * @param userDn
	 * @param reportCode
	 * @return
	 */
	boolean canGenerateReport(String userDn, String reportCode);
}
