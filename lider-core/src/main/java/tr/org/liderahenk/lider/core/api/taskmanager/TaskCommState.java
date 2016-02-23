package tr.org.liderahenk.lider.core.api.taskmanager;

/**
 * Target agent communication states in task
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public enum TaskCommState {
	AGENT_ONLINE,
	AGENT_OFFLINE,
	AGENT_TIMEDOUT,
	AGENT_RETRY;
}
