package tr.org.liderahenk.lider.persistence.entities;

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

import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;

/**
 * Entity class for IOperationLog objects.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see
 *
 */
@Entity
@Table(name = "OPERATION_LOG")
public class OperationLogImpl implements IOperationLog {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME")
	private String name;

	@Column(name = "ACTIVE")
	private Boolean active;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CHANGED_DATE")
	private Date changedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATION_DATE")
	private Date creationDate;

	@Column(name = "VERSION")
	private Integer version;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATE")
	private Date date;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "PLUGIN_ID")
	private String pluginId;

	@Column(name = "TASK_ID")
	private String taskId;

	@Column(name = "ACTION")
	private String action;

	@Column(name = "SERVER_IP")
	private String serverIp;

	@Column(name = "RESULT_CODE")
	private String resultCode;

	@Column(name = "LOG_TEXT")
	private String logText;

	@Column(name = "CHECKSUM")
	private String checksum;

	@Enumerated(EnumType.STRING)
	@Column(name = "CRUD_TYPE")
	private CrudType crudType;

	@Column(name = "CLIENT_CN")
	private String clientCN;

	public OperationLogImpl() {
		super();
	}

	public OperationLogImpl(Long id, String name, Boolean active, Date changedDate, Date creationDate, Integer version,
			Date date, String userId, String pluginId, String taskId, String action, String serverIp, String resultCode,
			String logText, String checksum, CrudType crudType, String clientCN) {
		super();
		this.id = id;
		this.name = name;
		this.active = active;
		this.changedDate = changedDate;
		this.creationDate = creationDate;
		this.version = version;
		this.date = date;
		this.userId = userId;
		this.pluginId = pluginId;
		this.taskId = taskId;
		this.action = action;
		this.serverIp = serverIp;
		this.resultCode = resultCode;
		this.logText = logText;
		this.checksum = checksum;
		this.crudType = crudType;
		this.clientCN = clientCN;
	}

	public OperationLogImpl(IOperationLog operationLog) {
		this.id = operationLog.getId();
		this.action = operationLog.getAction();
		this.active = operationLog.getActive();
		this.changedDate = operationLog.getChangedDate();
		this.checksum = operationLog.getChecksum();
		this.clientCN = operationLog.getClientCN();
		this.creationDate = operationLog.getCreationDate();
		this.crudType = operationLog.getCrudType();
		this.date = operationLog.getDate();
		this.logText = operationLog.getLogText();
		this.name = operationLog.getName();
		this.pluginId = operationLog.getPluginId();
		this.resultCode = operationLog.getResultCode();
		this.serverIp = operationLog.getServerIp();
		this.taskId = operationLog.getTaskId();
		this.userId = operationLog.getUserId();
		this.version = operationLog.getVersion();
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

	public void setCrudType(CrudType crudType) {
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
