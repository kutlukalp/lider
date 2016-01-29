package tr.org.liderahenk.lider.persistence.model.impl;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import tr.org.liderahenk.lider.core.api.rest.IScheduleRequest;
import tr.org.liderahenk.lider.core.api.rest.ScheduleOperation;

@Embeddable
public class ScheduleRequestEntityImpl implements IScheduleRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	private ScheduleOperation operation;
	
	private String scheduleId;
	
	private String cronString;

	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	private boolean isActive;
	
	public ScheduleRequestEntityImpl() {
	}

	public ScheduleRequestEntityImpl(IScheduleRequest req) {
		this.cronString = req.getCronString();
		this.endDate = req.getEndDate();
		this.isActive = req.isActive();
		this.operation = req.getOperation();
		this.scheduleId = req.getScheduleId();
		this.startDate = req.getStartDate();
	}

	@Override
	public ScheduleOperation getOperation() {
		return operation;
	}

	@Override
	public String getScheduleId() {
		return scheduleId;
	}

	@Override
	public String getCronString() {
		return cronString;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
	public Boolean isActive() {
		return isActive;
	}
	
	public void setOperation(ScheduleOperation operation) {
		this.operation = operation;
	}

	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}

	public void setCronString(String cronString) {
		this.cronString = cronString;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}
