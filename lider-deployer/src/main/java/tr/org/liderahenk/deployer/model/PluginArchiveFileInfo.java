package tr.org.liderahenk.deployer.model;

import tr.org.liderahenk.lider.core.api.deployer.ILiderHotDeployListener;

/**
 * Default implementation for {@link ILiderHotDeployListener}
 * 
 * @author <a href="mailto:basaran.ismaill@gmail.com">İsmail BAŞARAN</a>
 * 
 */
public class PluginArchiveFileInfo {
	
	
	private String liderPluginName;
	private String ahenkPluginName;
	private String liderConsolePluginDirectory;
	private int status;
	
	public String getLiderPluginName() {
		return liderPluginName;
	}
	public void setLiderPluginName(String liderPluginName) {
		this.liderPluginName = liderPluginName;
	}
	public String getAhenkPluginName() {
		return ahenkPluginName;
	}
	public void setAhenkPluginName(String ahenkPluginName) {
		this.ahenkPluginName = ahenkPluginName;
	}
	public String getLiderConsolePluginDirectory() {
		return liderConsolePluginDirectory;
	}
	public void setLiderConsolePluginDirectory(String liderConsolePluginDirectory) {
		this.liderConsolePluginDirectory = liderConsolePluginDirectory;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
