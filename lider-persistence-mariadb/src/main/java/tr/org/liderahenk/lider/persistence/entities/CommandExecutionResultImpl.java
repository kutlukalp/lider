package tr.org.liderahenk.lider.persistence.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.enums.ContentType;

/**
 * Entity class for execution result.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.
 *      ICommandExecutionResult
 *
 */
@JsonIgnoreProperties({ "commandExecution" })
@Entity
@Table(name = "C_COMMAND_EXECUTION_RESULT")
public class CommandExecutionResultImpl implements ICommandExecutionResult {

	private static final long serialVersionUID = -8995839892973401085L;

	@Id
	@GeneratedValue
	@Column(name = "COMMAND_EXECUTION_RESULT_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "COMMAND_EXECUTION_ID", nullable = false)
	private CommandExecutionImpl commandExecution; // bidirectional

	@Column(name = "AGENT_ID")
	private Long agentId;

	@Enumerated(EnumType.STRING)
	@Column(name = "RESPONSE_CODE", nullable = false)
	private StatusCode responseCode;

	@Column(name = "RESPONSE_MESSAGE")
	private String responseMessage;

	@Lob
	@Column(name = "RESPONSE_DATA")
	private byte[] responseData;

	@Enumerated(EnumType.STRING)
	@Column(name = "CONTENT_TYPE")
	private ContentType contentType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	public CommandExecutionResultImpl() {
	}

	public CommandExecutionResultImpl(Long id, CommandExecutionImpl commandExecution, Long agentId,
			StatusCode responseCode, String responseMessage, byte[] responseData, ContentType contentType,
			Date createDate, Date modifyDate) {
		super();
		this.id = id;
		this.commandExecution = commandExecution;
		this.agentId = agentId;
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.responseData = responseData;
		this.contentType = contentType;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}

	public CommandExecutionResultImpl(ICommandExecutionResult commandExecutionResult) {
		this.id = commandExecutionResult.getId();
		this.agentId = commandExecutionResult.getAgentId();
		this.responseCode = commandExecutionResult.getResponseCode();
		this.responseMessage = commandExecutionResult.getResponseMessage();
		this.responseData = commandExecutionResult.getResponseData();
		this.contentType = commandExecutionResult.getContentType();
		this.createDate = commandExecutionResult.getCreateDate();
		this.modifyDate = commandExecutionResult.getModifyDate();
		if (commandExecutionResult.getCommandExecution() instanceof CommandExecutionImpl) {
			this.commandExecution = (CommandExecutionImpl) commandExecutionResult.getCommandExecution();
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
	public CommandExecutionImpl getCommandExecution() {
		return commandExecution;
	}

	public void setCommandExecution(CommandExecutionImpl commandExecution) {
		this.commandExecution = commandExecution;
	}

	@Override
	public StatusCode getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(StatusCode responseCode) {
		this.responseCode = responseCode;
	}

	@Override
	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	@Override
	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	@Override
	public byte[] getResponseData() {
		return responseData;
	}

	public void setResponseData(byte[] responseData) {
		this.responseData = responseData;
	}

	@Override
	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
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

}
