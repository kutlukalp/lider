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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
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
import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;

/**
 * Entity class for command.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.ICommand
 *
 */
@Entity
@Table(name = "C_COMMAND", uniqueConstraints = @UniqueConstraint(columnNames = { "POLICY_ID", "TASK_ID" }) )
public class CommandImpl implements ICommand {

	private static final long serialVersionUID = 5691035821804595271L;

	@Id
	@GeneratedValue
	@Column(name = "COMMAND_ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "POLICY_ID")
	private Long policyId; // A command references to either a policy or task!

	@Column(name = "TASK_ID")
	private Long taskId; // A command references to either a policy or task!

	@Lob
	@Column(name = "DN_LIST")
	private String dnListJsonString;

	@Column(name = "DN_TYPE", length = 1)
	private Integer dnType;

	@Column(name = "COMMAND_OWNER_JID")
	private String commandOwnerJid;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@OneToMany(mappedBy = "command", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private List<CommandExecutionImpl> commandExecutions = new ArrayList<CommandExecutionImpl>(); // bidirectional

	public CommandImpl() {
	}

	public CommandImpl(Long id, Long policyId, Long taskId, List<String> dnList, RestDNType dnType, Date createDate,
			Date modifyDate, List<CommandExecutionImpl> commandExecutions)
					throws JsonGenerationException, JsonMappingException, IOException {
		this.id = id;
		this.policyId = policyId;
		this.taskId = taskId;
		this.dnListJsonString = new ObjectMapper().writeValueAsString(dnList);
		setDnType(dnType);
		this.createDate = createDate;
		this.commandExecutions = commandExecutions;
	}

	public CommandImpl(ICommand command) throws JsonGenerationException, JsonMappingException, IOException {
		this.id = command.getId();
		this.policyId = command.getPolicyId();
		this.taskId = command.getTaskId();
		this.dnListJsonString = new ObjectMapper().writeValueAsString(command.getDnList());
		setDnType(command.getDnType());
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
	public Long getPolicyId() {
		return policyId;
	}

	public void setPolicyId(Long policyId) {
		this.policyId = policyId;
	}

	@Override
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
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
	public RestDNType getDnType() {
		return RestDNType.getType(dnType);
	}

	public void setDnType(RestDNType dnType) {
		if (dnType == null) {
			this.dnType = null;
		} else {
			this.dnType = dnType.getId();
		}
	}

	@Override
	public String getCommandOwnerJid() {
		return commandOwnerJid;
	}

	public void setCommandOwnerJid(String commandOwnerJid) {
		this.commandOwnerJid = commandOwnerJid;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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
		return "CommandImpl [id=" + id + ", policyId=" + policyId + ", taskId=" + taskId + ", dnListJsonString="
				+ dnListJsonString + ", dnType=" + dnType + ", createDate=" + createDate + ", commandExecutions="
				+ commandExecutions + "]";
	}

}
