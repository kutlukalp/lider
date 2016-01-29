package tr.org.liderahenk.lider.persistence.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
import tr.org.liderahenk.lider.core.api.taskmanager.ITask;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskMessage;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskCommState;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskState;

@Entity(name="Task")
@Table(name="TASK")
public class TaskEntityImpl implements ITask{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private boolean active = true;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date changedDate;
	private Integer version;
	private Integer order;
	private Integer priority;
	
	@Enumerated(EnumType.STRING)
	private TaskState state;
	
	@Enumerated(EnumType.STRING)
	private TaskCommState commState;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timeout;
	
	
	private RestRequestEntityImpl request;
	private String targetObjectDN;
	private String parentTaskId;
	
	@ElementCollection(fetch=FetchType.EAGER, targetClass=TaskMessageEntityImpl.class)
	private List<TaskMessageEntityImpl> taskHistory = new ArrayList<TaskMessageEntityImpl>(0);

	private String targetJID;

	private String owner;
	
	private boolean parent = false;
	
//	@Column(name="ppp",updatable=false,insertable=false)
	private String pluginId;
	
	private String pluginVersion;
	
	public TaskEntityImpl() {
	}
	
	public TaskEntityImpl(ITask task) throws Exception {
		this.id = task.getId();
		this.active = task.isActive();
		this.changedDate = task.getChangedDate();
		this.commState = task.getCommState();
		this.creationDate = task.getCreationDate();
		this.order = task.getOrder();
		this.owner = task.getOwner();
		this.priority = task.getPriority();
		this.parentTaskId = task.getParentTaskId();
		//FIXME update object another way
//		this.request = new RestRequestEntityImpl(task.getRequest());
		this.state = task.getState();
		this.targetJID = task.getTargetJID();
		this.targetObjectDN = task.getTargetObjectDN();
		this.taskHistory = load(task.getTaskHistory());
		this.timeout =task.getTimeout();
		this.version = task.getVersion();
		this.parent = task.isParent();
		this.pluginId = task.getPluginId();
		this.pluginVersion = task.getPluginVersion();
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public Date getChangedDate() {
		return changedDate;
	}
	
	public void setChangedDate(Date changedDate) {
		this.changedDate = changedDate;
	}

	@Override
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public Integer getOrder() {
		return order;
	}

	@Override
	public Integer getPriority() {
		return priority;
	}

	@Override
	public TaskState getState() {
		return state;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public void setRequest(RestRequestEntityImpl request) {
		this.request = request;
	}

	@Override
	public Date getTimeout() {
		return timeout;
	}
	
	public void setTimeout(Date timeout) {
		this.timeout = timeout;
	}

	@Override
	public IRestRequest getRequest() {
		return request;
	}

	@Override
	public String getParentTaskId() {
		return parentTaskId;
	}
	
	public void setState(TaskState state) {
		this.state = state;
	}
	
	
	public void setParentTaskId(String parentTaskId) {
		this.parentTaskId = parentTaskId;
	}
	
	@Override
	public List<TaskMessageEntityImpl> getTaskHistory() {
		return taskHistory;
	}

	@Override
	public String getTargetObjectDN() {
		
		return targetObjectDN;
	}
	
	public void setTargetObjectDN(String targetObjectDN) {
		this.targetObjectDN = targetObjectDN;
	}

	public String getTargetJID() {
		return targetJID;
	}

	public void setTargetJID(String targetJID) {
		this.targetJID = targetJID;
	}

	public TaskCommState getCommState() {
		return commState;
	}

	public void setCommState(TaskCommState commState) {
		this.commState = commState;
	}

	@Override
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void setTaskHistory(List<TaskMessageEntityImpl> taskHistory) {
		this.taskHistory = taskHistory;
	}
	
	public void setParent(boolean parent) {
		this.parent = parent;
	}
	
	@Override
	public boolean isParent() {
		return parent;
	}
	
	
	public String getPluginId() {
		return pluginId;
	}
	
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
	
	public String getPluginVersion() {
		return pluginVersion;
	}
	
	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( this == obj)
			return true;
		if(! (obj instanceof TaskEntityImpl))
			return false;
		return getId().equals(((TaskEntityImpl)obj).getId());
	}

	@Override
	@Transient
	public String toJSON() {
		return null;
	}
	
	@Transient
	public List<TaskMessageEntityImpl> load( List<? extends ITaskMessage> msgs){
		if( null == msgs){
			return new ArrayList<TaskMessageEntityImpl>();
		}
		List<TaskMessageEntityImpl> messages = new ArrayList<TaskMessageEntityImpl>();
		for(ITaskMessage msg : msgs){
			messages.add(new TaskMessageEntityImpl(msg));
		}
		return messages;
	}
}
