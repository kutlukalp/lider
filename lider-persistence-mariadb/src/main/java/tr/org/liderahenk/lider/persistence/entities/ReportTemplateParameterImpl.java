package tr.org.liderahenk.lider.persistence.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "R_REPORT_TEMPLATE_PARAMETER", uniqueConstraints = @UniqueConstraint(columnNames = { "REPORT_TEMPLATE_ID",
		"PARAMETER_KEY" }) )
public class ReportTemplateParameterImpl implements IReportTemplateParameter {

	private static final long serialVersionUID = -1361608449887309975L;

	@Id
	@GeneratedValue
	@Column(name = "TEMPLATE_PARAMETER_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "REPORT_TEMPLATE_ID", nullable = false)
	private ReportTemplateImpl template; // bidirectional

	@Column(name = "PARAMETER_KEY", nullable = false)
	private String key;

	@Column(name = "LABEL", nullable = false, length = 250)
	private String label;

	@Column(name = "PARAMETER_TYPE", nullable = false)
	private Integer type;

	@Column(name = "DEFAULT_VALUE", nullable = true, length = 4000)
	private String defaultValue;

	@Column(name = "MANDATORY")
	private boolean mandatory;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	public ReportTemplateParameterImpl() {
	}

	public ReportTemplateParameterImpl(Long id, ReportTemplateImpl template, String key, String label,
			ParameterType type, String defaultValue, boolean mandatory, Date createDate) {
		this.id = id;
		this.template = template;
		this.key = key;
		this.label = label;
		setType(type);
		this.defaultValue = defaultValue;
		this.mandatory = mandatory;
		this.createDate = createDate;
	}

	public ReportTemplateParameterImpl(IReportTemplateParameter param) {
		this.id = param.getId();
		this.key = param.getKey();
		this.label = param.getLabel();
		setType(param.getType());
		this.defaultValue = param.getDefaultValue();
		this.mandatory = param.isMandatory();
		this.createDate = param.getCreateDate();
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
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "ReportTemplateParameterImpl [id=" + id + ", key=" + key + ", label=" + label + ", type=" + type + "]";
	}

	/**
	 * hashCode() & equals() are overridden to prevent duplicate records!
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	/**
	 * hashCode() & equals() are overridden to prevent duplicate records!
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportTemplateParameterImpl other = (ReportTemplateParameterImpl) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

}
