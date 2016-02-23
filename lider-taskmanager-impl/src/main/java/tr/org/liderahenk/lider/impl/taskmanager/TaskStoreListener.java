package tr.org.liderahenk.lider.impl.taskmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;

/**
 * Default implementation for hazelcast's {@link EntryListener}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskStoreListener implements EntryListener<String, TaskImpl> {

	private final static Logger LOG =  LoggerFactory.getLogger(TaskStoreListener.class);
	
	@Override
	public void entryAdded(EntryEvent<String, TaskImpl> evt) {
		LOG.debug("Task entry added:{} {}", evt.getKey(), evt.getValue());
	}
	@Override
	public void entryEvicted(EntryEvent<String, TaskImpl> evt) {
		LOG.debug("Task entry evicted:{} {}", evt.getKey(), evt.getValue());
	}
	@Override
	public void entryRemoved(EntryEvent<String, TaskImpl> evt) {
		LOG.debug("Task entry removed:{} {}", evt.getKey(), evt.getValue());
		//TODO: persist to RDBMS
	}
	@Override
	public void entryUpdated(EntryEvent<String, TaskImpl> evt) {
		LOG.debug("Task entry updated:{} {}", evt.getKey(), evt.getValue());
		//TODO: if task status eq closed, then schedule it to be removed && persist the latest version to RDBMC
	}

}
