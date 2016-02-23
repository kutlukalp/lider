package tr.org.liderahenk.lider.impl.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TarArchiveReadTest {
	
	private static Logger logger = LoggerFactory.getLogger(TarArchiveReadTest.class);
	
	private static final String PARDUS_NAS_SC = "/Users/bduman"+"/pardus_nas/agent_logs";
	private static final String NEW_LOGS_DIR = PARDUS_NAS_SC + "/new";
	//private static final String LOGS_BEING_PROCESSED_DIR = PARDUS_NAS_SC + "/processing";
	private static final String LOGS_PROCESSED_DIR = PARDUS_NAS_SC + "/archive";
	//private static final String LOGS_ERROR_DIR = PARDUS_NAS_SC + "/error";
	//private static final String LOGS_TEMP_DIR = PARDUS_NAS_SC + "/temp";

	public static void main(String[] args) throws Exception {
		//AgentLogServiceImpl logServiceImpl = new AgentLogServiceImpl();
		//readTest();
		
		//logServiceImpl.checkLogDir();
		
		new TarArchiveReadTest().processUploadedLogs();
	}
	
	public void processUploadedLogs() throws Exception{
		File dir = new File(NEW_LOGS_DIR);
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
		
		//List<String> lines = new ArrayList<String>(); 
		ObjectMapper mapper = new ObjectMapper();
		
		String line;
		while ( (line = buff.readLine()) != null ){
			//processLogLine(line);
			//lines.add(line);
			try {
				line.replaceAll("'", "\"");
				Map<String,Object> map = mapper.readValue(line, new TypeReference<Map<String,Object>>() {});
				
			    map.get("user");
				map.get("severity");
				Map<String,Object> message = (Map<String,Object>)map.get("message");
				Map<String,Object> task = (Map<String,Object>)message.get("task");
				
				task.get("action");
				task.get("clientCN");
				
				message.get("message");
				task.get("pluginId");
				message.get("resultCode");
				InetAddress.getLocalHost().toString();
				task.get("taskId");
				
				
				new Date((Long)message.get("time"));
				//logService.createLog(date, userId, pluginId, taskId, action, serverIp, resultCode, logText, crudType, clientCN);
			} catch (Exception e) {
				logger.warn("could not import log line: ", e);
			}
		}
		
		buff.close();
		fis.close();
	}
	
	private static void moveFile(File source, File destination) throws Exception{
		logger.debug("moving file source -> {}, dest -> {}", source.getAbsolutePath(), 
				destination.getAbsolutePath());
		if (!source.renameTo(destination)){
			throw new IOException("Cannot move file " + 
					source.getAbsolutePath() + 
					" to " + destination.getAbsolutePath());
		}
	}

}
