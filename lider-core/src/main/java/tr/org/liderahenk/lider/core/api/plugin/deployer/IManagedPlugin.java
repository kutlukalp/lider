package tr.org.liderahenk.lider.core.api.plugin.deployer;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public interface IManagedPlugin extends Serializable{
	
	
	Long getId();
	
	String getName();
	
	String getVersion();
	
	Date getInstallationDate();
	
	List<? extends IPluginPart> getParts();
	
	Boolean getActive();
	
}
