package tr.org.liderahenk.lider.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import tr.org.liderahenk.lider.core.api.plugin.deployer.IManagedPlugin;
import tr.org.liderahenk.lider.core.api.plugin.deployer.IPluginPart;

@Entity
@Table(name = "PLUGIN_PART")
public class PluginPart implements IPluginPart{

	private static final long serialVersionUID = -409362861845723176L;
	
	@Id
	@GeneratedValue
	@Column(name = "PLUGIN_PART_ID", unique = true, nullable = false)
	private Long id;
	
	private String fileName;
	
	private String type;
	
	private String fullPath;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PLUGIN_INFO_ID", nullable = false)
	private ManagedPlugin info;
	
	
	public PluginPart() {
	}

	public PluginPart(Long id,String fileName,String type,String fullPath) {
		this.id = id;
		this.fileName = fileName;
		this.type = type;
		this.fullPath = fullPath;
		this.info = new ManagedPlugin(info);
	}
	
	public PluginPart(IPluginPart part) {
		this.id = part.getId();
		this.fileName = part.getFileName();
		this.type = part.getType();
		this.fullPath = part.getFullPath();
		this.info = new ManagedPlugin(part.getInfo());
	}
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFullPath() {
		return fullPath;
	}
	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
	public ManagedPlugin getInfo() {
		return info;
	}
	public void setInfo(ManagedPlugin info) {
		this.info = info;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
