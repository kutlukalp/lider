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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportViewColumn;
import tr.org.liderahenk.lider.core.api.persistence.enums.ViewColumnType;

@JsonIgnoreProperties({ "view" })
@Entity
@Table(name = "R_REPORT_VIEW_COLUMN")
public class ReportViewColumnImpl implements IReportViewColumn {

	private static final long serialVersionUID = -8966099076093392712L;

	@Id
	@GeneratedValue
	@Column(name = "VIEW_COLUMN_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "REPORT_VIEW_ID", nullable = false)
	private ReportViewImpl view;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TEMPLATE_COLUMN_ID", nullable = false)
	private ReportTemplateColumnImpl referencedColumn;

	@Column(name = "COLUMN_TYPE", nullable = false)
	private Integer type;

	@Column(name = "LEGEND", nullable = true)
	private String legend;

	@Column(name = "WIDTH")
	private Integer width;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	public ReportViewColumnImpl() {
	}

	public ReportViewColumnImpl(Long id, ReportViewImpl view, ReportTemplateColumnImpl referencedColumn,
			ViewColumnType type, String legend, Integer width, Date createDate) {
		this.id = id;
		this.view = view;
		this.referencedColumn = referencedColumn;
		setType(type);
		this.legend = legend;
		this.width = width;
		this.createDate = createDate;
	}

	public ReportViewColumnImpl(IReportViewColumn column) {
		this.id = column.getId();
		this.legend = column.getLegend();
		setType(column.getType());
		this.legend = column.getLegend();
		this.width = column.getWidth();
		this.createDate = column.getCreateDate();
		if (column.getView() instanceof ReportViewImpl) {
			this.view = (ReportViewImpl) column.getView();
		}
		if (column.getReferencedColumn() instanceof ReportTemplateColumnImpl) {
			this.referencedColumn = (ReportTemplateColumnImpl) column.getReferencedColumn();
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
	public ReportViewImpl getView() {
		return view;
	}

	public void setView(ReportViewImpl view) {
		this.view = view;
	}

	@Override
	public ReportTemplateColumnImpl getReferencedColumn() {
		return referencedColumn;
	}

	public void setReferencedColumn(ReportTemplateColumnImpl referencedColumn) {
		this.referencedColumn = referencedColumn;
	}

	@Override
	public ViewColumnType getType() {
		return ViewColumnType.getType(type);
	}

	public void setType(ViewColumnType type) {
		if (type == null) {
			this.type = null;
		} else {
			this.type = type.getId();
		}
	}

	@Override
	public String getLegend() {
		return legend;
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

	@Override
	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportViewColumnImpl other = (ReportViewColumnImpl) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReportViewColumnImpl [id=" + id + ", type=" + type + ", legend=" + legend + "]";
	}

}
