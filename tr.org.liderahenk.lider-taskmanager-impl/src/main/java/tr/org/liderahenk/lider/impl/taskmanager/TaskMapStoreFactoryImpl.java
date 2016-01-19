package tr.org.liderahenk.lider.impl.taskmanager;

import java.util.Properties;

import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStoreFactory;

/**
 * Default implementation for hazelcast's {@link MapStoreFactory}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskMapStoreFactoryImpl implements
		MapStoreFactory<String, TaskImpl> {
	
	private TaskMapStore taskMapStore;
	
	public void setTaskMapStore(TaskMapStore taskMapStore) {
		this.taskMapStore = taskMapStore;
	}

	@Override
	public MapLoader<String, TaskImpl> newMapStore(String mapName,
			Properties properties) {
		return taskMapStore;
	}

}
