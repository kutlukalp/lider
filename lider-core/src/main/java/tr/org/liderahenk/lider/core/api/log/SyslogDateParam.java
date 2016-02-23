package tr.org.liderahenk.lider.core.api.log;

import java.io.Serializable;

public class SyslogDateParam implements Serializable{
	
	private static final long serialVersionUID = 4186411775999861911L;
	private Long startDate;
	private Long endDate;
	
	public SyslogDateParam(Long startDate,Long endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public SyslogDateParam() {
	}
	
	
	public Long getStartDate() {
		return startDate;
	}
	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}
	public Long getEndDate() {
		return endDate;
	}
	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

}
