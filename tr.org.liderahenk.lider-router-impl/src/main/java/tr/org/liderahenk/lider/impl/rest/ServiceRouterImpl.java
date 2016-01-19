package tr.org.liderahenk.lider.impl.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import tr.org.liderahenk.lider.core.api.plugin.CommandResultStatus;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.ICommandContext;
import tr.org.liderahenk.lider.core.api.plugin.ICommandContextFactory;
import tr.org.liderahenk.lider.core.api.plugin.ICommandResult;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
import tr.org.liderahenk.lider.core.api.rest.IRestResponse;
import tr.org.liderahenk.lider.core.api.rest.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.router.IServiceRegistry;
import tr.org.liderahenk.lider.core.api.router.IServiceRouter;
import tr.org.liderahenk.lider.core.api.router.InvalidRequestException;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskManager;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskSubmissionFailedException;

/**
 * Default implementation for {@link IServiceRouter}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class ServiceRouterImpl implements IServiceRouter{
	
	private static Logger logger = LoggerFactory.getLogger(ServiceRouterImpl.class);

	private IServiceRegistry serviceRegistry;
	private IResponseFactory responseFactory;//cyclic dependency!, created a core-impl module as a workaround, need more time later to make a better design. 
	private ICommandContextFactory commandContextFactory;
//	private ISchedulerService schedulerService;//TODO task scheduler handles scheduled tasks
	private ITaskManager taskManager;//TODO task manager gets run-now tasks
	
	public void setServiceRegistry(IServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	
	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}
	
	public void setCommandContextFactory(
			ICommandContextFactory commandContextFactory) {
		this.commandContextFactory = commandContextFactory;
	}
	
//	public void setSchedulerService(ISchedulerService taskScheduler) { //TODO  schedulerService closed by volkan
//		this.schedulerService = taskScheduler;
//	}
	
	public void setTaskManager(ITaskManager taskManager) {
		this.taskManager = taskManager;
	}
	
	@Override
	public IRestResponse delegateRequest( IRestRequest request) throws InvalidRequestException, TaskSubmissionFailedException{

		//TODO should we return a NEW instance (PROTOTYPE) of the actual command object on every call
		ICommand command = serviceRegistry.lookupCommand( request );
		
		if ( null == command ){
			throw new InvalidRequestException( request);
		}
		
		if(!request.getBody().isScheduled()){//TODO decide on command type, ServerCommand, ScheduleCommand, TaskCommand etc?
			ICommandContext commandContext = commandContextFactory.create( request);
			
			ICommandResult validationResult = command.validate(commandContext);
			
			if(CommandResultStatus.OK != validationResult.getStatus())
			{
				return responseFactory.createResponse( validationResult );
			}
			
			ICommandResult commandResult =  command.execute(commandContext);
			
			if ( CommandResultStatus.OK == commandResult.getStatus()){
				
				if( ! command.needsTask()){
					logger.debug("{} is a IServerCommand, no need to create a task", command);
					return responseFactory.createResponse( commandResult );
				}else 
				{
					logger.debug("{} is a ITaskCommand, I will call ITaskManager to create a task for this", command);
					String[] taskIds = null;
					try {
						taskIds = taskManager.addTask(request);//TODO better call async, LDAP search takes too long
					} catch (Exception e) {
						logger.error("Could not add task for request: ", e);
						List<String> messages = new ArrayList<String>();
						messages.add("Could not add task for request: " + e.getMessage());
						return responseFactory.createResponse( request, RestResponseStatus.ERROR, messages );
					}
					return responseFactory.createResponse( commandResult, taskIds );
				}
			}
			
			else{
				//TODO: be more specific about CommandResult and its failure
				return responseFactory.createResponse( commandResult );
			}
		}
		else {//if(request.getBody().isScheduled())
			List<String> messages = new ArrayList<String>();
			//TODO closed by volkan becaused of schedulerService
//			try {   
//				schedulerService.proccessSchedulerRequest(request);
//			} catch (Exception e) {
//				logger.error("Could not handle schedule request: ", e);
//				messages.add("Could not handle schedule request: " + e.getMessage());
//				//TODO: scheduler should give us more detail??
//				return responseFactory.createResponse( request, RestResponseStatus.ERROR, messages );
//			}
			return responseFactory.createResponse( request, RestResponseStatus.OK, Arrays.asList(new String[]{"Zamanlanmış görev başarıyla kaydedildi."}) );
		}
		
	}

}
