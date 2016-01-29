package tr.org.liderahenk.lider.persistence.model.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import tr.org.liderahenk.lider.core.api.enums.CrudType;
import tr.org.liderahenk.lider.core.api.log.IOperationLog;

@Entity
@Table(name="OPERATION_LOG")
public class OperationLogImpl implements IOperationLog{

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@Column(name="name")
	private String name;
	@Column(name="active")
	private Boolean active;
	@Column(name="changedDate")
	private Date changedDate;
	@Column(name="creationDate")
	private Date creationDate;
	@Column(name="version")
	private Integer version;
	@Column(name="date")
	@Temporal(TemporalType.DATE)
	private Date date;
	@Column(name="userId")
	private String userId;
	@Column(name="pluginId")
	private String pluginId;
	@Column(name="taskId")
	private String taskId;
	@Column(name="action")
	private String action;
	@Column(name="serverIp")
	private String serverIp;
	@Column(name="resultCode")
	private String resultCode;
	@Column(name="logText")
	private String logText;
	@Column(name="checksum")
	private String checksum;
	@Column(name="crudTypeId")
	@Enumerated(EnumType.STRING)
	private CrudType crudType;
	@Column(name="clientCN")
	private String clientCN;
	
	
	public OperationLogImpl() {
		
	}
//
//	public OperationLogImpl(Long id, Date date,
//			String userId, String plugin, String task, String action,
//			String serverIp, String resultCode, String logText,
//			String checksum, CrudType crudType, String clientCN) {
//		super();
//		this.date = date;
//		this.userId = userId;
//		this.pluginId = plugin;
//		this.taskId = task;
//		this.action = action;
//		this.serverIp = serverIp;
//		this.resultCode = resultCode;
//		this.logText = logText;
//		this.checksum = checksum;
//		this.crudType = crudType;
//		this.clientCN = clientCN;
//		this.id = id;
//	}

	
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

	
	public Date getChangedDate() {
		return changedDate;
	}

	public void setChangedDate(Date changedDate) {
		this.changedDate = changedDate;
	}

	
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
