package tr.org.liderahenk.lider.core.api.taskmanager;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
/**
 * ITask is the core component of request processing
 *  in Lider and agent.    
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface ITask  extends Serializable  {
	
	/**
	 * 
	 * @return unique task id
	 */
	String getId();
	
	/**
	 * 
	 * @return true if task is still in progress, or false if it is already finished
	 */
	boolean isActive();
	
	/**
	 * 
	 * @return the date task created
	 */
	Date getCreationDate();
	
	/**
	 * 
	 * @return last update date
	 */
	Date getChangedDate();
	
	/**
	 * 
	 * @return # of updates on task
	 */
	Integer getVersion();
	
	/**
	 * 
	 * @return {@link IRestRequest} for which this task is created 
	 */
	IRestRequest getRequest();
	
	/**
	 * @deprecated not used, subject to deprecation
	 */
	Integer getOrder();
	
	/**
	 * @return task priority, used in agent task queue processing order 
	 */
	Integer getPriority();
	
	
	/**
	 * @return task state in terms of agent processing
	 */
	TaskState getState();
	
	/**
	 * 
	 * @return target agent's communication state {@link TaskCommState}
	 */
	TaskCommState getCommState();
	
	/**
	 * 
	 * @return date that this tasks {@link #getCommState()} will be marked as {@link TaskCommState#AGENT_TIMEDOUT} 
	 */
	Date getTimeout();
	
	/**
	 * @see IRestRequest#getUser()
	 * @return user that created this task. 
	 */
	String getOwner();
	
	/**
	 * @see IRestRequest#getAccess()
	 * @return task is to be processed on this target agent dn  
	 */
	String getTargetObjectDN();
	
	/**
	 * 
	 * @return JSON serialized form of task
	 */
	String toJSON();

	/**
	 * 
	 * @return list of {@link ITaskMessage}'s populated during processing lifecycle of task.
	 * This list may consist of both server and agent processing information
	 */
	List<? extends ITaskMessage> getTaskHistory();

	/**
	 * 
	 * @return parent task id, if this task is a subtask, i.e. created from a root request with a subtree-search implied directive
	 * in {@link IRestRequest#getResource()}/{@link IRestRequest#getAccess()} 
	 */
	String getParentTaskId();

	/**
	 * 
	 * @return target agent jid of task 
	 * <b>Only English characters supported</b>
	 */
	String getTargetJID();
	
	/**
	 * 
	 * @return true if this is a parent task containing sub tasks, false otherwise
	 */
	boolean isParent();
	
	/**
	 * 
	 * @return plugin id creating task
	 */
	String getPluginId();
	
	/**
	 * 
	 * @return plugin id creating task
	 */
	String getPluginVersion();
	
}
