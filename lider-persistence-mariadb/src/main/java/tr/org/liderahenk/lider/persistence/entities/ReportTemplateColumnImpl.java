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

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportTemplateColumRequest;

/**
 * This class represents a report column defined in a report template.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@JsonIgnoreProperties({ "template" })
@Entity
@Table(name = "R_REPORT_TEMPLATE_COLUMN", uniqueConstraints = @UniqueConstraint(columnNames = { "REPORT_TEMPLATE_ID",
		"COLUMN_ORDER" }) )
public class ReportTemplateColumnImpl implements IReportTemplateColumn {

	private static final long serialVersionUID = 7196785409916030894L;

	@Id
	@GeneratedValue
	@Column(name = "COLUMN_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REPORT_TEMPLATE_ID", nullable = false)
	private ReportTemplateImpl template; // bidirectional

	@Column(name = "NAME", nullable = false)
	private String name;

	@Column(name = "VISIBLE")
	private boolean visible;

	@Column(name = "WIDTH")
	private Integer width;

	@Column(name = "COLUMN_ORDER", nullable = false)
	private Integer columnOrder;

	public ReportTemplateColumnImpl() {
	}

	public ReportTemplateColumnImpl(Long id, ReportTemplateImpl template, String name, boolean visible, Integer width,
			Integer columnOrder) {
		this.id = id;
		this.template = template;
		this.name = name;
		this.visible = visible;
		this.width = width;
		this.columnOrder = columnOrder;
	}

	public ReportTemplateColumnImpl(IReportTemplateColumn column) {
		this.id = column.getId();
		this.name = column.getName();
		this.visible = column.isVisible();
		this.width = column.getWidth();
		this.columnOrder = column.getColumnOrder();
		if (column.getTemplate() instanceof ReportTemplateImpl) {
			this.template = (ReportTemplateImpl) column.getTemplate();
		}
	}

	public ReportTemplateColumnImpl(IReportTemplateColumRequest c) {
		this.id = c.getId();
		this.name = c.getName();
		this.visible = c.isVisible();
		this.width = c.getWidth();
		this.columnOrder = c.getColumnOrder();
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
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	@Override
	public Integer getColumnOrder() {
		return columnOrder;
	}

	public void setColumnOrder(Integer columnOrder) {
		this.columnOrder = columnOrder;
	}

	@Override
	public String toString() {
		return "ReportTemplateColumnImpl [id=" + id + ", name=" + name + ", visible=" + visible + ", width=" + width
				+ ", columnOrder=" + columnOrder + "]";
	}

}
