package tr.org.liderahenk.lider.persistence.entities;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;

@Entity
@Table(name = "C_TASK")
public class TaskImpl implements ITask {

	private static final long serialVersionUID = 843694316079776849L;

	@Id
	@GeneratedValue
	@Column(name = "TASK_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PLUGIN_ID", nullable = false)
	private PluginImpl plugin; // unidirectional

	@Column(name = "COMMAND_CLS_ID")
	private String commandClsId;

	@Lob
	@Column(name = "PARAMETER_MAP")
	private byte[] parameterMap;

	@Column(name = "DELETED")
	private boolean deleted = false;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	public TaskImpl() {
	}

	public TaskImpl(Long id, PluginImpl plugin, String commandClsId, byte[] parameterMap, boolean deleted,
			Date createDate, Date modifyDate) {
		this.id = id;
		this.plugin = plugin;
		this.commandClsId = commandClsId;
		this.parameterMap = parameterMap;
		this.deleted = deleted;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}

	public TaskImpl(ITask task) {
		this.id = task.getId();
		this.commandClsId = task.getCommandClsId();
		this.parameterMap = task.getParameterMap();
		this.deleted = task.isDeleted();
		this.createDate = task.getCreateDate();
		this.modifyDate = task.getModifyDate();
		if (task.getPlugin() instanceof PluginImpl) {
			this.plugin = (PluginImpl) task.getPlugin();
		}
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public PluginImpl getPlugin() {
		return plugin;
	}

	public void setPlugin(PluginImpl plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getCommandClsId() {
		return commandClsId;
	}

	public void setCommandClsId(String commandClsId) {
		this.commandClsId = commandClsId;
	}

	@Override
	public byte[] getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(byte[] parameterMap) {
		this.parameterMap = parameterMap;
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	@Override
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return "TaskImpl [id=" + id + ", plugin=" + plugin + ", commandClsId=" + commandClsId + ", parameterMap="
				+ Arrays.toString(parameterMap) + "]";
	}

}
