package tr.org.liderahenk.deployer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.deployer.ILiderHotDeployListener;


/**
 * Default implementation for {@link ILiderHotDeployListener}
 * 
 * @author <a href="mailto:basaran.ismaill@gmail.com">İsmail BAŞARAN</a>
 * 
 */
public class LiderHotDeployListener implements ILiderHotDeployListener,Runnable{
	
	
	private final static Logger logger = LoggerFactory.getLogger(LiderHotDeployListener.class);
	
	private IConfigurationService configurationService;

	private WatchService watcher;
	private Map<WatchKey,Path> keys;
	
	
	@SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
	
	
	@Override
	public void register(Path dir) {
		try {
			WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			keys.put(key, dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void init(){
		try {
			this.watcher = FileSystems.getDefault().newWatchService();
			this.keys = new HashMap<WatchKey,Path>();
			Path dir = Paths.get(configurationService.getHotDeploymentPath()); // get path value from configuration file
			register(dir);
			new Thread(this).start(); //TODO
		} catch (IOException e) {
			logger.error("[LiderHotDeployListener] Exeption occured when initializing hot deployment listener...");
			e.printStackTrace();
		} 
	}
	
	public void destroy(){
		try {
			if(this.watcher != null){
				this.watcher.close();
			}
		} catch (IOException e) {
			logger.error("[LiderHotDeployListener] Exeption occured when destroying hot deployment listener...");
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		processEvents();
	}
	
	@Override
	public void processEvents() {
		for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
            	logger.error("Watcher key not found...");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                logger.info("%s: %s\n", event.kind().name(), child);
                System.out.println(event.kind().name());
                System.out.println(child);
                //TODO untar plugin and dispatch plugins 

                
            }
            
            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
	}
	
	
	/**
	 * 
	 * @param configurationService
	 */
	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}


	

}
