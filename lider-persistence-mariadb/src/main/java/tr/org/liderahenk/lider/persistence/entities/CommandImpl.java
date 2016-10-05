package tr.org.liderahenk.lider.persistence.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.core.api.rest.enums.DNType;

/**
 * Entity class for command.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.ICommand
 *
 */
@Entity
@Table(name = "C_COMMAND", uniqueConstraints = @UniqueConstraint(columnNames = { "POLICY_ID", "TASK_ID" }))
public class CommandImpl implements ICommand {

	private static final long serialVersionUID = 5691035821804595271L;

	@Id
	@GeneratedValue
	@Column(name = "COMMAND_ID", unique = true, nullable = false)
	private Long id;

	// FIXME these should be FetchType.LAZY but current version of OpenJPA does
	// not retrieve the records on access.
	@OneToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "POLICY_ID", referencedColumnName = "POLICY_ID", insertable = true, updatable = false, nullable = true, unique = false)
	private PolicyImpl policy;

	// FIXME these should be FetchType.LAZY but current version of OpenJPA does
	// not retrieve the records on access.
	@OneToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = true, updatable = false, nullable = true, unique = false)
	private TaskImpl task;

	@Lob
	@Column(name = "DN_LIST")
	private String dnListJsonString;

	@Column(name = "DN_TYPE", length = 1)
	private Integer dnType;

	@Lob
	@Column(name = "UID_LIST")
	private String uidListJsonString;

	@Column(name = "COMMAND_OWNER_UID")
	private String commandOwnerUid;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTIVATION_DATE", nullable = true)
	private Date activationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@OneToMany(mappedBy = "command", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
	private List<CommandExecutionImpl> commandExecutions = new ArrayList<CommandExecutionImpl>(); // bidirectional

	public CommandImpl() {
	}

	public CommandImpl(Long id, IPolicy policy, ITask task, List<String> dnList, DNType dnType, List<String> uidList,
			String commandOwnerUid, Date activationDate, Date createDate, List<CommandExecutionImpl> commandExecutions)
			throws JsonGenerationException, JsonMappingException, IOException {
		this.id = id;
		this.policy = (PolicyImpl) policy;
		this.task = (TaskImpl) task;
		ObjectMapper mapper = new ObjectMapper();
		this.dnListJsonString = mapper.writeValueAsString(dnList);
		setDnType(dnType);
		this.uidListJsonString = uidList != null ? mapper.writeValueAsString(uidList) : null;
		this.commandOwnerUid = commandOwnerUid;
		this.activationDate = activationDate;
		this.createDate = createDate;
		this.commandExecutions = commandExecutions;
	}

	public CommandImpl(ICommand command) throws JsonGenerationException, JsonMappingException, IOException {
		this.id = command.getId();
		this.policy = (PolicyImpl) command.getPolicy();
		this.task = (TaskImpl) command.getTask();
		ObjectMapper mapper = new ObjectMapper();
		this.dnListJsonString = mapper.writeValueAsString(command.getDnList());
		setDnType(command.getDnType());
		this.uidListJsonString = command.getUidList() != null ? mapper.writeValueAsString(command.getUidList()) : null;
		this.commandOwnerUid = command.getCommandOwnerUid();
		this.activationDate = command.getActivationDate();
		this.createDate = command.getCreateDate();

		// Convert ICommandExecution to CommandExecutionImpl
		List<? extends ICommandExecution> tmpCommandExecutions = command.getCommandExecutions();
		if (tmpCommandExecutions != null) {
			for (ICommandExecution commandExecution : tmpCommandExecutions) {
				addCommandExecution(commandExecution);
			}
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
	public PolicyImpl getPolicy() {
		return policy;
	}

	public void setPolicy(PolicyImpl policy) {
		this.policy = policy;
	}

	@Override
	public TaskImpl getTask() {
		return task;
	}

	public void setTask(TaskImpl task) {
		this.task = task;
	}

	public String getDnListJsonString() {
		return dnListJsonString;
	}

	@Transient
	@Override
	public List<String> getDnList() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(dnListJsonString, new TypeReference<ArrayList<String>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setDnListJsonString(String dnListJsonString) {
		this.dnListJsonString = dnListJsonString;
	}

	@Override
	public DNType getDnType() {
		return DNType.getType(dnType);
	}

	public void setDnType(DNType dnType) {
		if (dnType == null) {
			this.dnType = null;
		} else {
			this.dnType = dnType.getId();
		}
	}

	@SuppressWarnings("unchecked")
	@Transient
	@Override
	public List<String> getUidList() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return (List<String>) (uidListJsonString != null
					? mapper.readValue(uidListJsonString, new TypeReference<ArrayList<String>>() {
					}) : null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setUidListJsonString(String uidListJsonString) {
		this.uidListJsonString = uidListJsonString;
	}

	@Override
	public String getCommandOwnerUid() {
		return commandOwnerUid;
	}

	public void setCommandOwnerUid(String commandOwnerUid) {
		this.commandOwnerUid = commandOwnerUid;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	@Override
	public List<CommandExecutionImpl> getCommandExecutions() {
		return commandExecutions;
	}

	public void setCommandExecutions(List<CommandExecutionImpl> commandExecutions) {
		this.commandExecutions = commandExecutions;
	}

	@Override
	public void addCommandExecution(ICommandExecution commandExecution) {
		if (commandExecutions == null) {
			commandExecutions = new ArrayList<CommandExecutionImpl>();
		}
		CommandExecutionImpl commandExecutionImpl = null;
		if (commandExecution instanceof CommandExecutionImpl) {
			commandExecutionImpl = (CommandExecutionImpl) commandExecution;
		} else {
			commandExecutionImpl = new CommandExecutionImpl(commandExecution);
		}
		if (commandExecutionImpl.getCommand() != this) {
			commandExecutionImpl.setCommand(this);
		}
		commandExecutions.add(commandExecutionImpl);
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
		return "CommandImpl [id=" + id + ", dnListJsonString=" + dnListJsonString + ", dnType=" + dnType
				+ ", createDate=" + createDate + ", commandExecutions=" + commandExecutions + "]";
	}

}
