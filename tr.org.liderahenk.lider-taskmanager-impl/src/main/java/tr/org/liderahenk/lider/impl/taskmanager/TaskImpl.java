package tr.org.liderahenk.lider.impl.taskmanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
import tr.org.liderahenk.lider.core.api.taskmanager.ITask;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskMessage;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskCommState;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskState;

/**
 * Task manager implementation for {@link ITask}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskImpl implements ITask {
	
	private static transient Logger logger = LoggerFactory.getLogger(TaskImpl.class); 
	
	private String id;
	private boolean active = true;
	private Date creationDate;
	private Date changedDate;
	private Integer version;
	private Integer order;
	private Integer priority;
	private TaskState state;
	private TaskCommState commState;
	private Date timeout;
	private RestRequestImpl request;
	private String targetObjectDN;
	private String parentTaskId;
	private String pluginId;
	private String pluginVersion;
	private List<TaskMessageImpl> taskHistory = new ArrayList<TaskMessageImpl>(0);

	private String targetJID;

	private String owner;

	private boolean parent = false;
	
	public TaskImpl() {
		// TODO Auto-generated constructor stub
	}
	
	public TaskImpl(String targetObjectDN, String targetJID, String parentTaskId, IRestRequest request) {
		this(targetObjectDN, request, parentTaskId );
		this.targetJID= targetJID;
	}
	
	public TaskImpl(String targetObjectDN, String targetJID, IRestRequest request) {
		this(targetObjectDN, request);
		this.targetJID= targetJID.toLowerCase(Locale.ENGLISH);
	}
	
	public TaskImpl(String targetObjectDN, IRestRequest request) {
		this.id = String.valueOf(UUID.randomUUID());
		//TODO plugin_id, rest url, attribute,command,action, 
		if( request.getUser() != null) 
		{
			this.owner = request.getUser();
		}
		else{
			this.owner = "ANONYMOUS";
		}
		
		this.targetObjectDN = targetObjectDN;
		this.active = true;
		this.creationDate = new Date();
		this.changedDate = creationDate;
		this.version = 0;
		this.priority = (null != request.getBody().getPriority()) ? request.getBody().getPriority() : 5;
		this.state = TaskState.CREATED;
		this.timeout = new Date(creationDate.getTime()+ TaskStoreHazelcastImpl.getTimeout());
		this.request = (RestRequestImpl) request;
		this.pluginId = request.getBody().getPluginId();
		this.pluginVersion = request.getBody().getPluginVersion();
		
	}
	
	public TaskImpl(String targetObjectDN, IRestRequest request, String parentTaskId) {
		this( targetObjectDN,  request);
		this.parentTaskId =  parentTaskId;
		
	}

	public TaskImpl(ITask task) {
		this.id = task.getId();
		this.active = task.isActive();
		this.changedDate = task.getChangedDate();
		this.commState = task.getCommState();
		this.creationDate = task.getCreationDate();
		this.order = task.getOrder();
		this.owner = task.getOwner();
		this.priority = task.getPriority();
		this.parentTaskId = task.getParentTaskId();
		this.request = new RestRequestImpl(task.getRequest());
		this.state = task.getState();
		this.targetJID = (null == task.getTargetJID()) ? "ERROR" : task.getTargetJID().toLowerCase(Locale.ENGLISH);
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
	public boolean isParent() {
		return parent;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public void setRequest(RestRequestImpl request) {
		this.request = request;
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
	
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@Override
	public TaskState getState() {
		return state;
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
	
	public void setParent(boolean parent) {
		this.parent = parent;
	}
	
	@Override
	public List<TaskMessageImpl> getTaskHistory() {
		return taskHistory;
	}

	@Override
	public String getTargetObjectDN() {
		
		return targetObjectDN;
	}
	
	public void setTargetObjectDN(String targetObjectDN) {
		this.targetObjectDN = targetObjectDN;
	}

	@Override
	public String getTargetJID() {
		return targetJID;
	}

	public void setTargetJID(String targetJID) {
		this.targetJID = targetJID.toLowerCase(Locale.ENGLISH);
	}

	public TaskCommState getCommState() {
		return commState;
	}

	public void setCommState(TaskCommState commState) {
		this.commState = commState;
	}

	public void addSubTask(TaskImpl task) {
		//this.subTasks.add(task);
		task.setParentTaskId(this.getId());
	}

	@Override
	public String toJSON(){
			try {
				return new ObjectMapper().writeValueAsString(this);
			}  catch (Exception e) {
				logger.error("could not serialize Task: ", e);
			}
			return null;
	}
	
	@Override
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public boolean isActive() {
		return active;
	}
	
	public void setTaskHistory(List<TaskMessageImpl> taskHistory) {
		this.taskHistory = taskHistory;
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
		if(! (obj instanceof TaskImpl))
			return false;
		return getId().equals(((TaskImpl)obj).getId());
	}
	
	public List<TaskMessageImpl> load( List<? extends ITaskMessage> msgs){
		if( null == msgs){
			return new ArrayList<TaskMessageImpl>();
		}
		List<TaskMessageImpl> messages = new ArrayList<TaskMessageImpl>();
		for(ITaskMessage msg : msgs){
			messages.add(new TaskMessageImpl(msg));
		}
		return messages;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2057571043079304092L;

}
