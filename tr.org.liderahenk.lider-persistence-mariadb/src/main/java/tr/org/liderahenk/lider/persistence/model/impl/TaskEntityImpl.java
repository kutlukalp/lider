package tr.org.liderahenk.lider.persistence.model.impl;

import java.io.IOException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import tr.org.liderahenk.lider.core.api.taskmanager.ITask;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskCommState;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskState;

/**
 * Entity class for ITask objects.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.impl.taskmanager.TaskImpl
 *
 */
@Entity
@Table(name = "TASK")
public class TaskEntityImpl implements ITask {

	private static final long serialVersionUID = -2057571043079904092L;

	@Id
	@Column(name = "ID")
	private String id;

	@Column(name = "ACTIVE")
	private boolean active = true;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATION_DATE")
	private Date creationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CHANGED_DATE")
	private Date changedDate;

	@Column(name = "VERSION")
	private Integer version;

	@Embedded
	private RestRequestEntityImpl request;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATE")
	private TaskState state;

	@Enumerated(EnumType.STRING)
	@Column(name = "COMM_STATE")
	private TaskCommState commState;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TIMEOUT")
	private Date timeout;

	@Column(name = "OWNER")
	private String owner;

	@Column(name = "TARGET_OBJECT_DN")
	private String targetObjectDN;

	@Column(name = "PARENT_TASK_ID")
	private String parentTaskId;

	@Column(name = "TARGET_JID")
	private String targetJID;

	@Column(name = "PARENT")
	private boolean parent = false;

	public TaskEntityImpl() {
	}

	public TaskEntityImpl(String id, boolean active, Date creationDate, Date changedDate, Integer version,
			RestRequestEntityImpl request, TaskState state, TaskCommState commState, Date timeout, String owner,
			String targetObjectDN, String parentTaskId, String targetJID, boolean parent) {
		super();
		this.id = id;
		this.active = active;
		this.creationDate = creationDate;
		this.changedDate = changedDate;
		this.version = version;
		this.request = request;
		this.state = state;
		this.commState = commState;
		this.timeout = timeout;
		this.owner = owner;
		this.targetObjectDN = targetObjectDN;
		this.parentTaskId = parentTaskId;
		this.targetJID = targetJID;
		this.parent = parent;
	}

	public TaskEntityImpl(ITask task) throws JsonGenerationException, JsonMappingException, IOException {
		this.active = task.isActive();
		this.changedDate = task.getChangedDate();
		this.commState = task.getCommState();
		this.creationDate = task.getCreationDate();
		this.id = task.getId();
		this.owner = task.getOwner();
		this.parent = task.isParent();
		this.parentTaskId = task.getParentTaskId();
		this.request = new RestRequestEntityImpl(task.getRequest());
		this.state = task.getState();
		this.targetJID = task.getTargetJID();
		this.targetObjectDN = task.getTargetObjectDN();
		this.timeout = task.getTimeout();
		this.version = task.getVersion();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getChangedDate() {
		return changedDate;
	}

	public void setChangedDate(Date changedDate) {
		this.changedDate = changedDate;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public RestRequestEntityImpl getRequest() {
		return request;
	}

	public void setRequest(RestRequestEntityImpl request) {
		this.request = request;
	}

	public TaskState getState() {
		return state;
	}

	public void setState(TaskState state) {
		this.state = state;
	}

	public TaskCommState getCommState() {
		return commState;
	}

	public void setCommState(TaskCommState commState) {
		this.commState = commState;
	}

	public Date getTimeout() {
		return timeout;
	}

	public void setTimeout(Date timeout) {
		this.timeout = timeout;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getTargetObjectDN() {
		return targetObjectDN;
	}

	public void setTargetObjectDN(String targetObjectDN) {
		this.targetObjectDN = targetObjectDN;
	}

	public String getParentTaskId() {
		return parentTaskId;
	}

	public void setParentTaskId(String parentTaskId) {
		this.parentTaskId = parentTaskId;
	}

	public String getTargetJID() {
		return targetJID;
	}

	public void setTargetJID(String targetJID) {
		this.targetJID = targetJID;
	}

	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof TaskEntityImpl))
			return false;
		return getId().equals(((TaskEntityImpl) obj).getId());
	}

	@Override
	@Transient
	public String toJSON() {
		return null;
	}

}
