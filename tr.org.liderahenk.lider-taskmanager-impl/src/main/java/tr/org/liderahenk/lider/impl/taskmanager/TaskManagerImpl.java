/**
 * 
 */
package tr.org.liderahenk.lider.impl.taskmanager;

import static tr.org.liderahenk.lider.core.api.taskmanager.TaskState.TASK_ERROR;
import static tr.org.liderahenk.lider.core.api.taskmanager.TaskState.TASK_KILLED;
import static tr.org.liderahenk.lider.core.api.taskmanager.TaskState.TASK_PROCESSED;
import static tr.org.liderahenk.lider.core.api.taskmanager.TaskState.TASK_WARNING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.messaging.IMessage;
import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.IMessageSubscriber;
import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
import tr.org.liderahenk.lider.core.api.messaging.ITaskStatusUpdateMessage;
import tr.org.liderahenk.lider.core.api.messaging.ITaskStatusUpdateSubscriber;
import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
import tr.org.liderahenk.lider.core.api.taskmanager.ITask;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskManager;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskMessage;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskCommState;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskServiceException;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskState;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskSubmissionFailedException;
import tr.org.liderahenk.lider.impl.rest.RestRequestBodyImpl;
import tr.org.liderahenk.lider.impl.rest.RestRequestImpl;

/**
 * Default implementation for {@link ITaskManager}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskManagerImpl implements ITaskManager, ITaskStatusUpdateSubscriber, IMessageSubscriber{
	private TaskServiceImpl taskService;
	private ILDAPService ldapService;
	private IMessagingService messagingService;
	private IMessageFactory messageFactory;
	private IConfigurationService config; 
	
	private List<TaskState> taskEndingStates = Arrays.asList(new TaskState[]{TASK_PROCESSED,TASK_WARNING,TASK_ERROR, TASK_KILLED});
	
	private static Logger log = LoggerFactory.getLogger(TaskManagerImpl.class);
	
	private EventAdmin eventAdmin;
	
	private BundleContext bundleContext;
	
	public void init(){
		log.info("Initializing Task Manager...");
	}
	
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}
	
	public void setConfig(IConfigurationService config) {
		this.config = config;
	}
	
	public void setTaskService(TaskServiceImpl taskService) {
		this.taskService = taskService;
	}
	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}
	
	public void setMessageFactory(IMessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}
	
	public void setMessagingService(IMessagingService messagingService) {
		this.messagingService = messagingService;
	}
	
	@Override
	public void messageReceived(ITaskStatusUpdateMessage message) {
		// TODO convert ITaskStatusUpdateMessage to TaskStatusUpdateImpl
	}
	
	//@Override
	@Deprecated
	public void messageReceived(String jid, String msgJson) {
		//maybe any irrelevant message to task manager no need to print info trace
		log.debug(String.format("Received message '%1$s' from %2$s", msgJson, jid));
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			
			
			TaskStatusUpdateImpl taskUpdate = mapper.readValue(msgJson,TaskStatusUpdateImpl.class);
			
			String taskId = taskUpdate.getTask(); 
			
			if( null == taskId ){
				log.warn("cannot find task id in received message, this does not seem to be a VALID task status message");
				return ;
			}
			
			TaskImpl taskImpl = taskService.get(taskId);
			
			if( null == taskImpl ){
				log.info("cannot find task with id {}, task may already be deleted", taskId );
				return;
			}
			
			String taskOwnerDn = taskImpl.getOwner();
			
			String taskOwnerJid = ldapService.getEntry(taskOwnerDn, config.getAgentLdapJidAttribute()).get(config.getAgentLdapJidAttribute());
			
			String agentDn = ldapService.search(config.getAgentLdapJidAttribute(), jid.split("@")[0], new String[]{}).get(0).getDistinguishedName();
			
			taskUpdate.setTaskOwnerDn(taskOwnerDn);
			taskUpdate.setTaskOwnerJid(taskOwnerJid);
			
			taskUpdate.setFromDn(agentDn);
			taskUpdate.setFromJid(jid);
			//taskUpdate.setFromDn();
			if (processMessage(taskUpdate)){
				if(taskEndingStates.contains(taskUpdate.getType())){
					//async 
					Dictionary payload = new Hashtable();
					payload.put("taskUpdate", taskUpdate);
					eventAdmin.postEvent(new Event("tr/org/pardus/mys/taskmanager/task/update", payload));
				}
			}
		} catch (Exception e) {
			//maybe any irrelevant message to task manager no need to print info trace
			log.info("Task manager could not parse message from {}, enable debug for further information", jid, msgJson);
			log.error("cannot parse message into task status update => {}", msgJson, e );
			log.debug("Task manager could not parse message: {}", msgJson );
			log.debug("Exception trace: ",e);
		} 
		
	}
	
	private boolean processMessage(TaskStatusUpdateImpl taskUpdate) throws TaskServiceException 
	{
		
		String taskId = taskUpdate.getTask();
		Date messageTime = taskUpdate.getTimestamp();
		
		ArrayList<ITaskMessage> taskMessageList = new ArrayList<ITaskMessage>();
		
		if ( null != taskUpdate.getMessages()) {
			for(TaskMessageImpl message : taskUpdate.getMessages()) {
					taskMessageList.add( message );
			}
		}
		
		
		if( null == taskId ){
			log.warn("cannot find task id in received message, this does not seem to be a VALID task status message");
			return false;
		}
		
		TaskImpl taskImpl = taskService.get(taskId);
		
		if( null == taskImpl ){
			log.info("cannot find task with id {}, task may already be deleted", taskId );
			return false;
		}
		
		if( taskUpdate.getFromJid().toLowerCase(Locale.ENGLISH).indexOf( taskImpl.getTargetJID().toLowerCase(Locale.ENGLISH) ) < 0 ){
			log.error("bounced task status update notify message, ignoring");
			return false;
		}
		
		TaskState taskUpdateType = taskUpdate.getType();
	
		taskService.update(taskId, taskUpdateType, taskMessageList, messageTime);
		
		return true;
	}

	@Override
	public String[] addTask(IRestRequest request) throws TaskSubmissionFailedException
	{
		List<String> taskIds = new ArrayList<String>();
		
//		log.info("creating task for request {}/{}/{}/{}/{}", request.getResource(),
//				request.getAccess(),
//				request.getAttribute(),
//				request.getCommand(),
//				request.getAction() );
		try {
			
			IRestRequest restRequest = request;// task.getRequest();
			
			String targetDN = ldapService.getDN(request.getAccess(), "objectClass", request.getResource() );
			TaskImpl parentTask;
			
			
			if (targetDN != null) {
				//single task from a request pointing a ldap node
				parentTask =  new TaskImpl(restRequest.getAccess(), restRequest);
				LdapEntry ldapEntry = ldapService.getEntry(targetDN, config.getAgentLdapJidAttribute());
				parentTask.setTargetJID(ldapEntry.get(config.getAgentLdapJidAttribute()));
				
				IMessage message =  messageFactory.create(parentTask);
				
				if ( ! messagingService.isRecipientOnline(message.getRecipient()) ){
					log.warn("{} is OFFLINE, marking task comm state accordingly", message.getRecipient());
					parentTask.setCommState(TaskCommState.AGENT_OFFLINE);
				}
				else{
					parentTask.setCommState(TaskCommState.AGENT_ONLINE);
				}
				
				parentTask.setTargetJID(message.getRecipient());
				parentTask.setParent(false);
				
				taskService.insert(parentTask);
				
				taskIds.add(parentTask.getId());
				
				messagingService.sendMessage(message);
			}
			else{
				//multiple tasks from a request pointing a ldap subtree query
				List<String> childObjects = ldapService.getChilds( request.getAccess(), "objectClass", request.getResource(),false );
				
				//check LDAP node in request is valid
				if( null == childObjects || childObjects.isEmpty())
				{
					childObjects = ldapService.getChilds( request.getAccess(), "objectClass", request.getResource(),true );
					if( null == childObjects || childObjects.isEmpty())
					{
						throw new TaskSubmissionFailedException( "Request does not seem to point a valid LDAP entry -> " 
								+ request.getAccess() + " (objectClass=" + request.getResource() + ") "+ " in " + request.getURL() );
					}
				}
				//not creating parent task
				/*parentTask = new TaskImpl(restRequest.getAccess(), restRequest);
				parentTask.setParent(true);
				
				taskService.insert(parentTask);*/
				
				for ( String childObjectDn : childObjects ) {
					TaskImpl subTask =  new TaskImpl(childObjectDn, request/*, parentTask.getId()*/);
					LdapEntry ldapEntry = ldapService.getEntry(childObjectDn, config.getAgentLdapJidAttribute());
					subTask.setTargetJID(ldapEntry.get(config.getAgentLdapJidAttribute()));
					IMessage message =  messageFactory.create(subTask);
					log.info("Sending task to --> " + message.getRecipient());
					
					if ( ! messagingService.isRecipientOnline(message.getRecipient()) ){
						log.warn("{} is OFFLINE, marking task comm state accordingly", message.getRecipient());
						subTask.setCommState(TaskCommState.AGENT_OFFLINE);
					}
					else{
						subTask.setCommState(TaskCommState.AGENT_ONLINE);
					}
					
					subTask.setTargetJID(message.getRecipient());
					
					taskService.insert(subTask);
					
					taskIds.add(subTask.getId());
					
					messagingService.sendMessage(message);
				}
			}
			return taskIds.toArray(new String[taskIds.size()]);
		} catch (Exception e) {
			e.printStackTrace();
			//TODO reorganize exception stack
			if ( e instanceof TaskSubmissionFailedException )
				throw (TaskSubmissionFailedException) e;
			
			throw new TaskSubmissionFailedException( e);
		}
	}
	
	@Override
	public void updateTaskPriority(String currentUser, String id, Integer priority) throws TaskServiceException, TaskSubmissionFailedException{
		//temporary :) fix for agent task queue priority update 
		
		log.info("will create a task to update task priority, id -> {}, priority -> {}", id, priority);
		
		ITask targetTask = taskService.get(id);
		
		String resource = targetTask.getRequest().getResource();
		String access = targetTask.getRequest().getAccess();
		String attribute = "TASK";
		String command = "PRIORITY";
		String action = "UPDATE";
		Map<String,Object> customParams = new HashMap<String, Object>();
		customParams.put("taskId", id);
		customParams.put("priority", priority);
		RestRequestBodyImpl body = new RestRequestBodyImpl();
		body.setCustomParameterMap(customParams);
		body.setPriority(1);
		RestRequestImpl request = new RestRequestImpl(resource, access, attribute, command, action, body, currentUser);
		
		this.addTask(request);
		
		taskService.update(id, priority);//update task priority in task db too
			
			
	}
	
	@Override
	public void updateAhenk(String currentUser, String agentDn, Set<String> plugins) throws TaskSubmissionFailedException{
		//temporary :) fix for agent task queue priority update 
		
		log.info("will create a task to update agent plugins, agent dn => {}, cuurent user => {}", agentDn, currentUser);
		
		
		String resource = "*";//targetTask.getRequest().getResource();
		String access = agentDn;//targetTask.getRequest().getAccess();
		String attribute = "AGENT";
		String command = "SOFTWARE";
		String action = "UPDATE";
		Map<String,Object> customParams = new HashMap<String, Object>();
		customParams.put("plugins", plugins);
		customParams.put("action", "update");
		RestRequestBodyImpl body = new RestRequestBodyImpl();
		body.setPluginId("installer");
		body.setCustomParameterMap(customParams);
		RestRequestImpl request = new RestRequestImpl(resource, access, attribute, command, action, body, currentUser);
		
		this.addTask(request);
		
					
	}


}
