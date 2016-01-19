package tr.org.liderahenk.lider.impl.rest;

import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.rest.IScheduleRequest;
import tr.org.liderahenk.lider.core.api.rest.ScheduleOperation;

/**
 * Default implementation for {@link IScheduleRequest}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class ScheduleRequestImpl implements IScheduleRequest {
	
	
	private ScheduleOperation operation;
	private String scheduleId;
	private String cronString;
	private Date startDate;
	private Date endDate;
	private boolean isActive;
	
	public ScheduleRequestImpl() {
	}

	public ScheduleRequestImpl(IScheduleRequest req) {
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
	
	public ScheduleRequestImpl fromJson(String json){
		ObjectMapper mapper = new ObjectMapper(); 
		try {
			return mapper.readValue(json, ScheduleRequestImpl.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
