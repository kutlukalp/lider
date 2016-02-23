package tr.org.liderahenk.lider.impl.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.enums.CrudType;
import tr.org.liderahenk.lider.core.api.log.IAgentLogService;
import tr.org.liderahenk.lider.core.api.log.IOperationLogService;

public class AgentLogServiceImpl implements IAgentLogService{
	
	private final static Logger LOG = LoggerFactory.getLogger(AgentLogServiceImpl.class);
	
	private static final String PARDUS_NAS_SC = "/pardus_nas/agent_logs";
	private static final String NEW_LOGS_DIR = PARDUS_NAS_SC + "/new";
	private static final String LOGS_BEING_PROCESSED_DIR = PARDUS_NAS_SC + "/processing";
	private static final String LOGS_PROCESSED_DIR = PARDUS_NAS_SC + "/archive";
	private static final String LOGS_ERROR_DIR = PARDUS_NAS_SC + "/error";
	private static final String LOGS_TEMP_DIR = PARDUS_NAS_SC + "/temp";
	
	
	private IOperationLogService logService;
	
	public void setLogService(IOperationLogService logService) {
		this.logService = logService;
	}
	
	public void init(){
		try{//too tired to define variables, live with it
			if (! new File(PARDUS_NAS_SC).exists()){
				new File(PARDUS_NAS_SC).mkdirs();
			}
			if (! new File(NEW_LOGS_DIR).exists()){
				new File(NEW_LOGS_DIR).mkdirs();
			}
			if (! new File(LOGS_BEING_PROCESSED_DIR).exists()){
				new File(LOGS_BEING_PROCESSED_DIR).mkdirs();
			}
			if (! new File(LOGS_PROCESSED_DIR).exists()){
				new File(LOGS_PROCESSED_DIR).mkdirs();
			}
			if (! new File(LOGS_ERROR_DIR).exists()){
				new File(LOGS_ERROR_DIR).mkdirs();
			}
			if (! new File(LOGS_TEMP_DIR).exists()){
				new File(LOGS_TEMP_DIR).mkdirs();
			}
		}
		catch(Exception e){
			LOG.error("error creating agent log directories: ", e);
		}
	}
	
	@Override
	public void processUploadedLogs() throws Exception{
		File dir = new File(NEW_LOGS_DIR);
		if (! dir.exists()){
			LOG.error("could not process logs. upload directory does not exist: {}", dir.getAbsolutePath());
			return;
		}
		File listDir[] = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().endsWith("tar.gz");
			}
		});
		if (listDir.length!=0){
		    for (File i:listDir){
		        /*  Warning! this will try and extract all files in the directory
		            if other files exist, a for loop needs to go here to check that
		            the file (i) is an archive file before proceeding */
		        if (i.isDirectory()){
		            break;
		        }
		        String fileName = i.toString();
		        String tarFileName = fileName +".tar";
		        FileInputStream instream= new FileInputStream(fileName);
		        GZIPInputStream ginstream =new GZIPInputStream(instream);
		        FileOutputStream outstream = new FileOutputStream(tarFileName);
		        byte[] buf = new byte[1024]; 
		        int len;
		        while ((len = ginstream.read(buf)) > 0) 
		        {
		            outstream.write(buf, 0, len);
		        }
		        ginstream.close();
		        outstream.close();
		        //There should now be tar files in the directory
		        //extract specific files from tar
		        TarArchiveInputStream myTarFile=new TarArchiveInputStream(new FileInputStream(tarFileName));
		        TarArchiveEntry entry = null;
		        int offset;
		        FileOutputStream outputFile=null;
		        //read every single entry in TAR file
		        while ((entry = myTarFile.getNextTarEntry()) != null) {
		            //the following two lines remove the .tar.gz extension for the folder name
		            fileName = i.getName().substring(0, i.getName().lastIndexOf('.'));
		            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		            File outputDir =  new File(i.getParent() + "/" + fileName + "/" + entry.getName());
		            if(! outputDir.getParentFile().exists()){ 
		                outputDir.getParentFile().mkdirs();
		            }
		            //if the entry in the tar is a directory, it needs to be created, only files can be extracted
		            if(entry.isDirectory()){
		                outputDir.mkdirs();
		            }else{
		                byte[] content = new byte[(int) entry.getSize()];
		                offset=0;
		                myTarFile.read(content, offset, content.length - offset);
		                outputFile=new FileOutputStream(outputDir);
		                IOUtils.write(content,outputFile);  
		                outputFile.close();
		                importLogFile(outputDir);
		                
		                File processedDir = new File(LOGS_PROCESSED_DIR, fileName);
		                if(!processedDir.exists()){
		                	processedDir.mkdirs();
		                }
		                
		                moveFile(outputDir, new File(processedDir, outputDir.getName()));
		            }
		        }
		        //close and delete the tar files, leaving the original .tar.gz and the extracted folders
		        myTarFile.close();
		        File tarFile =  new File(tarFileName);
		        tarFile.delete();
		        
		        //move processed tar.gz file to archive 
		        moveFile(i,new File(LOGS_PROCESSED_DIR, i.getName()));
		    }
		}
	}
	
	private void importLogFile(File f) throws Exception{
		InputStream fis = new FileInputStream(f);
		BufferedReader buff = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
		
		String line;
		while ( (line = buff.readLine()) != null ){
			try {
				String[] fields = line.split("\t");
				
				for (int i = 0; i < fields.length; i++) {
					LOG.info(fields[i]);
				}
				
				String date = fields[0];
				String pluginId = fields[1];
				String taskId = fields[3];
				String logText = fields[2] + ": " + fields[4];
				
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
				Date timestamp = dateFormat.parse(date);
				
				/*IOperationLog createLog( Date date, String userId, String pluginId,
						String taskId, String action, String serverIp, String resultCode,
						String logText, CrudType crudType, String clientCN);
						*/
				logService.createLog(timestamp, null, pluginId, taskId, null, null, null, logText, CrudType.Insert, null);
			} catch (Exception e) {
				LOG.warn("could not import log line: ", e);
			}
		}
		
		buff.close();
		fis.close();
	}
	
	private void moveFile(File source, File destination) throws Exception{
		LOG.debug("moving file source -> {}, dest -> {}", source.getAbsolutePath(), 
				destination.getAbsolutePath());
		if (!source.renameTo(destination)){
			throw new IOException("Cannot move file " + 
					source.getAbsolutePath() + 
					" to " + destination.getAbsolutePath());
		}
	}
	
}
