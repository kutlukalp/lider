package tr.org.liderahenk.deployer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
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
	private static final int BUFFER = 8*1024;

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
			Path dir = Paths.get(configurationService.getHotDeploymentPath()); 
			register(dir);
			new Thread(this).start(); //TODO do something better
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
                try {
					System.out.println(Files.probeContentType(child));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                if (checkFileMimeType(child)){
                	System.out.println("Hemmen yüklüyorum plugini saol yigenim");
                }else {
                	System.out.println("Bu dosya lider plugini icin uygun degildir.");
                }
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
	
	
	public boolean checkFileMimeType(Path filePath){
		try {
			//FIXME get from configuration manager
			if (Files.probeContentType(filePath).equalsIgnoreCase("application/gzip" ) 
				|| Files.probeContentType(filePath).equalsIgnoreCase("application/x-tar" )
				|| Files.probeContentType(filePath).equalsIgnoreCase("application/x-compressed-tar")){
				return true;
			}else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	
	 //FIXME return file names of archived files
	 public void unTarPlugin(String tarGzipFilePath, String destPath) throws IOException {
	        TarArchiveInputStream tarIn = null;
	        try {
	            GzipCompressorInputStream gzIn = new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(tarGzipFilePath), BUFFER));
	            tarIn = new TarArchiveInputStream(gzIn);
	            
	            TarArchiveEntry entry;
	            while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
	                if (entry.isDirectory()) {
	                    File f = new File(destPath + File.separator + entry.getName());
	                    f.mkdirs();
	                } else {
	                    int count;
	                    byte data[] = new byte[BUFFER];
	                    FileOutputStream fos = new FileOutputStream(destPath + File.separator + entry.getName());
	                    BufferedOutputStream destOut = new BufferedOutputStream(fos, BUFFER);
	                    while ((count = tarIn.read(data, 0, BUFFER)) != -1) {
	                        destOut.write(data, 0, count);
	                    }
	                    destOut.close();
	                }
	            }
	        } finally {
	            if (tarIn != null) {
	                tarIn.close();
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