package tr.org.liderahenk.lider.core.api.plugin.deployer;

import java.io.Serializable;

public interface IPluginPart extends Serializable{
	
	Long getId();
	
	String getFileName();
	
	String getType();
	
	String getFullPath();
	
	IManagedPlugin getInfo();
	
}
