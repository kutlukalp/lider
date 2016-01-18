package tr.org.liderahenk.lider.core.api.taskmanager;

/**
 * Task states during task processing 
 * CREATED: Task created in server
 * TASK_RECEIVED: Task received by target agent
 * TASK_PUT_IN_QUEUE: Task put into job queue of agent 
 * TASK_PROCESSING: Target agent started processing task
 * TASK_PROCESSED: Task processed successfully of agent
 * TASK_ERROR: Task processed with errors by target agent
 * TASK_TIMEOUT: Task timed-out while processing of agent
 * TASK_KILLED: Task killed before processing by agent
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public enum TaskState {
	CREATED,
	TASK_RECEIVED,
	TASK_PUT_IN_QUEUE,
	TASK_PROCESSING,
	TASK_PROCESSED,
	TASK_WARNING,
	TASK_ERROR,
	TASK_TIMEOUT,//task timed-out on agent
	TASK_KILLED;
	
}
