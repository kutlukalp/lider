package tr.org.liderahenk.lider.core.api.log;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import tr.org.liderahenk.lider.core.api.enums.CrudType;


/**
 * @author sbozu
 *
 */
@JsonAutoDetect
@JsonIgnoreProperties({"active", "changedDate", "creationDate", "checksum"})
public class OperationLogDto implements IOperationLog, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private Boolean active;
	private Date changedDate;
	private Date creationDate;
	private Integer version;
	private Date date; 
	private String userId;
	private String pluginId;
	private String taskId; 
	private String action;
	private String serverIp;
	private String resultCode;
	private String logText;
	private String checksum;
	private CrudType crudType;
	private String clientCN;
	
	
	public OperationLogDto() {
		
	}

	public OperationLogDto(Long id, Date date,
			String userId, String plugin, String task, String action,
			String serverIp, String resultCode, String logText,
			String checksum, CrudType crudType, String clientCN) {
		super();
		this.date = date;
		this.userId = userId;
		this.pluginId = plugin;
		this.taskId = task;
		this.action = action;
		this.serverIp = serverIp;
		this.resultCode = resultCode;
		this.logText = logText;
		this.checksum = checksum;
		this.crudType = crudType;
		this.clientCN = clientCN;
		this.id = id;
	}
	
	public OperationLogDto(IOperationLog log) {
		super();
		this.date = log.getDate();
		this.userId = "\"" + log.getUserId() + "\"";
		this.pluginId = "\"" + log.getPluginId() + "\"";
		this.taskId = "\"" + log.getTaskId() + "\"";
		this.action = "\"" + log.getAction() + "\"";
		this.serverIp = "\"" + log.getServerIp() + "\"";
		this.resultCode = "\"" + log.getServerIp() + "\"";
		this.logText = "\"" + log.getLogText() + "\"";
		this.checksum = "\"" +log.getChecksum() + "\"";
		this.crudType = log.getCrudType() ;
		this.clientCN = "\"" + log.getClientCN() + "\"";
		this.id = log.getId();
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}	
	

	public String getClientCN() {
		return clientCN;
	}

	public void setClientCN(String clientCN) {
		this.clientCN = clientCN;
	}
	
	@JsonSerialize(using=JsonDateSerializer.class)
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getLogText() {
		return logText;
	}

	public void setLogText(String logText) {
		this.logText = logText;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	public CrudType getCrudType() {
		return crudType;
	}
	
	public void setCrudType(CrudType crudType)
	{
		this.crudType = crudType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@JsonSerialize(using=JsonDateSerializer.class)
	public Date getChangedDate() {
		return changedDate;
	}

	public void setChangedDate(Date changedDate) {
		this.changedDate = changedDate;
	}

	@JsonSerialize(using=JsonDateSerializer.class)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
