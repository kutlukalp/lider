package tr.org.liderahenk.lider.impl.taskmanager;

import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.taskmanager.ITask;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskCommState;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskState;
import tr.org.liderahenk.lider.impl.rest.RestRequestImpl;

/**
 * Task implementation for {@link ITask}.
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class TaskImpl implements ITask {

	private static final long serialVersionUID = -2057571043079304092L;

	private static transient Logger logger = LoggerFactory.getLogger(TaskImpl.class);

	private String id;
	private boolean active = true;
	private Date creationDate;
	private Date changedDate;
	private Integer version;
	private RestRequestImpl request;
	private TaskState state;
	private TaskCommState commState;
	private Date timeout;
	private String owner;
	private String targetObjectDN;
	private String parentTaskId;
	private String targetJID;
	private boolean parent = false;

	public TaskImpl() {
		super();
	}

	public TaskImpl(String id, boolean active, Date creationDate, Date changedDate, Integer version,
			RestRequestImpl request, TaskState state, TaskCommState commState, Date timeout, String owner,
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

	public TaskImpl(ITask task) {
		this.id = task.getId();
		this.active = task.isActive();
		this.changedDate = task.getChangedDate();
		this.commState = task.getCommState();
		this.creationDate = task.getCreationDate();
		this.owner = task.getOwner();
		this.parent = task.isParent();
		this.parentTaskId = task.getParentTaskId();
		this.request = new RestRequestImpl(task.getRequest());
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

	public RestRequestImpl getRequest() {
		return request;
	}

	public void setRequest(RestRequestImpl request) {
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

	public void addSubTask(TaskImpl task) {
		task.setParentTaskId(this.getId());
	}

	@Override
	public String toJSON() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (Exception e) {
			logger.error("could not serialize Task: ", e);
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof TaskImpl))
			return false;
		return getId().equals(((TaskImpl) obj).getId());
	}

}
