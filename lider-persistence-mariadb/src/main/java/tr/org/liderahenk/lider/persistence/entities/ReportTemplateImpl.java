package tr.org.liderahenk.lider.persistence.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;

/**
 * Entity class for IReportTemplate objects.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Entity
@Table(name = "R_REPORT_TEMPLATE")
public class ReportTemplateImpl implements IReportTemplate {

	private static final long serialVersionUID = 5989888886942906625L;

	@Id
	@GeneratedValue
	@Column(name = "REPORT_TEMPLATE_ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "NAME", unique = true, nullable = false)
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "QUERY", nullable = false)
	private String query;

	@OneToMany(mappedBy = "template", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReportTemplateParameterImpl> templateParams; // bidirectional

	@OneToMany(mappedBy = "template", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReportTemplateColumnImpl> templateColumns; // bidirectional

	@Column(name = "REPORT_HEADER")
	private String reportHeader;

	@Column(name = "REPORT_FOOTER")
	private String reportFooter;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	public ReportTemplateImpl() {
	}

	public ReportTemplateImpl(Long id, String name, String description, String query,
			List<ReportTemplateParameterImpl> templateParams, List<ReportTemplateColumnImpl> templateColumns,
			String reportHeader, String reportFooter, Date createDate, Date modifyDate) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.query = query;
		this.templateParams = templateParams;
		this.templateColumns = templateColumns;
		this.reportHeader = reportHeader;
		this.reportFooter = reportFooter;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}

	public ReportTemplateImpl(IReportTemplate template) {
		this.id = template.getId();
		this.name = template.getName();
		this.description = template.getDescription();
		this.query = template.getQuery();
		this.reportHeader = template.getReportHeader();
		this.reportFooter = template.getReportFooter();
		this.createDate = template.getCreateDate();
		this.modifyDate = template.getModifyDate();

		// Convert IReportTemplateParameter to ReportTemplateParameterImpl
		List<? extends IReportTemplateParameter> params = template.getTemplateParams();
		if (params != null) {
			for (IReportTemplateParameter param : params) {
				addTemplateParameter(param);
			}
		}

		// Convert IReportTemplateColumn to ReportTemplateColumnImpl
		List<? extends IReportTemplateColumn> columns = template.getTemplateColumns();
		if (columns != null) {
			for (IReportTemplateColumn column : columns) {
				addTemplateColumn(column);
			}
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
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	@Override
	public List<ReportTemplateParameterImpl> getTemplateParams() {
		return templateParams;
	}

	public void setTemplateParams(List<ReportTemplateParameterImpl> templateParams) {
		this.templateParams = templateParams;
	}

	@Override
	public List<ReportTemplateColumnImpl> getTemplateColumns() {
		return templateColumns;
	}

	public void setTemplateColumns(List<ReportTemplateColumnImpl> templateColumns) {
		this.templateColumns = templateColumns;
	}

	@Override
	public String getReportHeader() {
		return reportHeader;
	}

	public void setReportHeader(String reportHeader) {
		this.reportHeader = reportHeader;
	}

	@Override
	public String getReportFooter() {
		return reportFooter;
	}

	public void setReportFooter(String reportFooter) {
		this.reportFooter = reportFooter;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	@Override
	public void addTemplateParameter(IReportTemplateParameter param) {
		if (templateParams == null) {
			templateParams = new ArrayList<ReportTemplateParameterImpl>();
		}
		ReportTemplateParameterImpl paramImpl = null;
		if (param instanceof ReportTemplateParameterImpl) {
			paramImpl = (ReportTemplateParameterImpl) param;
		} else {
			paramImpl = new ReportTemplateParameterImpl(param);
		}
		if (paramImpl.getTemplate() != this) {
			paramImpl.setTemplate(this);
		}
		templateParams.add(paramImpl);
	}

	@Override
	public void addTemplateColumn(IReportTemplateColumn column) {
		if (templateColumns == null) {
			templateColumns = new ArrayList<ReportTemplateColumnImpl>();
		}
		ReportTemplateColumnImpl columnImpl = null;
		if (column instanceof ReportTemplateColumnImpl) {
			columnImpl = (ReportTemplateColumnImpl) column;
		} else {
			columnImpl = new ReportTemplateColumnImpl(column);
		}
		if (columnImpl.getTemplate() != this) {
			columnImpl.setTemplate(this);
		}
		templateColumns.add(columnImpl);
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

	@Override
	public String toString() {
		return "ReportTemplateImpl [id=" + id + ", name=" + name + ", description=" + description + ", query=" + query
				+ ", templateParams=" + templateParams + ", templateColumns=" + templateColumns + ", reportHeader="
				+ reportHeader + ", reportFooter=" + reportFooter + ", createDate=" + createDate + ", modifyDate="
				+ modifyDate + "]";
	}

}
