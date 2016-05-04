package tr.org.liderahenk.lider.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;

/**
 * This class represents a report column defined in a report template.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Entity
@Table(name = "R_REPORT_TEMPLATE_COLUMN")
public class ReportTemplateColumnImpl implements IReportTemplateColumn {

	private static final long serialVersionUID = 7196785409916030894L;

	@Id
	@GeneratedValue
	@Column(name = "COLUMN_ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "NAME", nullable = false)
	private String name;

	@Column(name = "VISIBLE")
	private boolean visible;

	@Column(name = "WIDTH")
	private Integer width;

	@Column(name = "COLUMN_ORDER")
	private Integer columnOrder;

	public ReportTemplateColumnImpl() {
	}

	public ReportTemplateColumnImpl(Long id, String name, boolean visible, Integer width, Integer columnOrder) {
		this.id = id;
		this.name = name;
		this.visible = visible;
		this.width = width;
		this.columnOrder = columnOrder;
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

}
