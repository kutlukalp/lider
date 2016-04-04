package tr.org.liderahenk.lider.persistence.entities;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;

/**
 * Entity class for command execution.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution
 *
 */
@JsonIgnoreProperties({ "command" })
@Entity
@Table(name = "C_COMMAND_EXECUTION", uniqueConstraints = @UniqueConstraint(columnNames = { "COMMAND_ID", "DN_TYPE",
		"DN" }) )
public class CommandExecutionImpl implements ICommandExecution {

	private static final long serialVersionUID = 298103880409529933L;

	@Id
	@GeneratedValue
	@Column(name = "COMMAND_EXECUTION_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "COMMAND_ID", nullable = false)
	private CommandImpl command; // bidirectional

	@Column(name = "DN_TYPE", length = 1)
	private Integer dnType;

	@Column(name = "DN")
	private String dn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@OneToMany(mappedBy = "commandExecution", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
	@OrderBy("createDate DESC")
	private List<CommandExecutionResultImpl> commandExecutionResults = new ArrayList<CommandExecutionResultImpl>(); // bidirectional

	public CommandExecutionImpl() {
	}

	public CommandExecutionImpl(Long id, CommandImpl command, RestDNType dnType, String dn, Date createDate,
			List<CommandExecutionResultImpl> commandExecutionResults) {
		this.id = id;
		this.command = command;
		setDnType(dnType);
		this.dn = dn;
		this.createDate = createDate;
		this.commandExecutionResults = commandExecutionResults;
	}

	public CommandExecutionImpl(ICommandExecution commandExecution) {
		this.id = commandExecution.getId();
		setDnType(commandExecution.getDnType());
		this.dn = commandExecution.getDn();
		this.createDate = commandExecution.getCreateDate();

		// Convert ICommandExecutionResult to CommandExecutionResultImpl
		List<? extends ICommandExecutionResult> tmpCommandExecutionResults = commandExecution
				.getCommandExecutionResults();
		if (tmpCommandExecutionResults != null) {
			for (ICommandExecutionResult tmpCommandExecutionResult : tmpCommandExecutionResults) {
				addCommandExecutionResult(tmpCommandExecutionResult);
			}
		}

		if (commandExecution.getCommand() instanceof CommandImpl) {
			this.command = (CommandImpl) commandExecution.getCommand();
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
	public CommandImpl getCommand() {
		return command;
	}

	public void setCommand(CommandImpl command) {
		this.command = command;
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
	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	@Override
	public List<CommandExecutionResultImpl> getCommandExecutionResults() {
		return commandExecutionResults;
	}

	public void setCommandExecutionResults(List<CommandExecutionResultImpl> commandExecutionResults) {
		this.commandExecutionResults = commandExecutionResults;
	}

	@Override
	public void addCommandExecutionResult(ICommandExecutionResult commandExecutionResult) {
		if (commandExecutionResults == null) {
			commandExecutionResults = new ArrayList<CommandExecutionResultImpl>();
		}
		CommandExecutionResultImpl commandExecutionResultImpl = null;
		if (commandExecutionResult instanceof CommandExecutionResultImpl) {
			commandExecutionResultImpl = (CommandExecutionResultImpl) commandExecutionResult;
		} else {
			commandExecutionResultImpl = new CommandExecutionResultImpl(commandExecutionResult);
		}
		if (commandExecutionResultImpl.getCommandExecution() != this) {
			commandExecutionResultImpl.setCommandExecution(this);
		}
		commandExecutionResults.add(commandExecutionResultImpl);
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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
		return "CommandExecutionImpl [id=" + id + ", dnType=" + dnType + ", dn=" + dn + ", commandExecutionResults="
				+ commandExecutionResults + "]";
	}

}
