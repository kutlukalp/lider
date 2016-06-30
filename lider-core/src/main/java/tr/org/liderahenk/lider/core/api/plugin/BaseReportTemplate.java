package tr.org.liderahenk.lider.core.api.plugin;

import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;

/**
 * Convenience class for report templates. Plugins should extend this class in
 * order to provide their report templates.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public abstract class BaseReportTemplate implements IReportTemplate {

	private static final long serialVersionUID = -1871581480779346848L;

	@Override
	public Long getId() {
		return null;
	}

	@Override
	public Date getCreateDate() {
		return null;
	}

	@Override
	public Date getModifyDate() {
		return null;
	}

	@Override
	public void addTemplateParameter(IReportTemplateParameter param) {
	}

	@Override
	public void addTemplateColumn(IReportTemplateColumn column) {
	}
	
	@Override
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
