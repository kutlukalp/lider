package tr.org.liderahenk.lider.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.persistence.enums.ParameterType;

/**
 * This class represents a report parameter defined in a report template.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@JsonIgnoreProperties({ "template" })
@Entity
/*
 * FIXME a JPA bug prevents insert actions on this table due to uniqueConstraints annotation.
 */
@Table(name = "R_REPORT_TEMPLATE_PARAMETER" /*, uniqueConstraints = @UniqueConstraint(columnNames = { "REPORT_TEMPLATE_ID",
		"PARAMETER_KEY" })*/ )
public class ReportTemplateParameterImpl implements IReportTemplateParameter {

	private static final long serialVersionUID = -1361608449887309975L;

	@Id
	@GeneratedValue
	@Column(name = "PARAMETER_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "REPORT_TEMPLATE_ID", nullable = false)
	private ReportTemplateImpl template; // bidirectional

	@Column(name = "PARAMETER_KEY", nullable = false)
	private String key;

	@Column(name = "LABEL", nullable = false)
	private String label;

	@Column(name = "PARAMETER_TYPE", nullable = false)
	private Integer type;

	public ReportTemplateParameterImpl() {
	}

	public ReportTemplateParameterImpl(Long id, ReportTemplateImpl template, String key, String label,
			ParameterType type) {
		this.id = id;
		this.template = template;
		this.key = key;
		this.label = label;
		setType(type);
	}

	public ReportTemplateParameterImpl(IReportTemplateParameter param) {
		this.id = param.getId();
		this.key = param.getKey();
		this.label = param.getLabel();
		setType(param.getType());
		if (param.getTemplate() instanceof ReportTemplateImpl) {
			this.template = (ReportTemplateImpl) param.getTemplate();
		}
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public ReportTemplateImpl getTemplate() {
		return template;
	}

	public void setTemplate(ReportTemplateImpl template) {
		this.template = template;
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

	@Override
	public String toString() {
		return "ReportTemplateParameterImpl [id=" + id + ", key=" + key + ", label=" + label + ", type=" + type + "]";
	}

}
