package tr.org.liderahenk.lider.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.persistence.enums.ParameterType;

/**
 * This class represents a report parameter defined in a report template.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Entity
@Table(name = "R_REPORT_TEMPLATE_PARAMETER")
public class ReportTemplateParameterImpl implements IReportTemplateParameter {

	private static final long serialVersionUID = -1361608449887309975L;

	@Id
	@GeneratedValue
	@Column(name = "PARAMETER_ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "PARAMETER_KEY")
	private String key;

	@Column(name = "LABEL")
	private String label;

	@Column(name = "PARAMETER_TYPE")
	private Integer type;

	public ReportTemplateParameterImpl() {
	}

	public ReportTemplateParameterImpl(Long id, String key, String label, ParameterType type) {
		this.id = id;
		this.key = key;
		this.label = label;
		setType(type);
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public ParameterType getType() {
		return ParameterType.getType(type);
	}

	public void setType(ParameterType type) {
		if (type == null) {
			this.type = null;
		} else {
			this.type = type.getId();
		}
	}

}
