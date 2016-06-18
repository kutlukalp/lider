package tr.org.liderahenk.lider.service.requests;

import java.util.Date;

import tr.org.liderahenk.lider.core.api.persistence.enums.ViewColumnType;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportViewColumnRequest;

public class ReportViewColReqImpl implements IReportViewColumnRequest {

	private static final long serialVersionUID = 808547110927918705L;

	private Long id;

	private Long referencedColumnId;

	private ViewColumnType type;

	private String legend;

	private Integer width;

	private Date timestamp;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getReferencedColumnId() {
		return referencedColumnId;
	}

	public void setReferencedColumnId(Long referencedColumnId) {
		this.referencedColumnId = referencedColumnId;
	}

	@Override
	public ViewColumnType getType() {
		return type;
	}

	public void setType(ViewColumnType type) {
		this.type = type;
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
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
