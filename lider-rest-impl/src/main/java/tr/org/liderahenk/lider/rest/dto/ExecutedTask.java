package tr.org.liderahenk.lider.rest.dto;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;

/**
 * This is a specialized class which is used to list executed tasks with some
 * additional info.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutedTask implements Serializable {

	private static final long serialVersionUID = 911947090772911900L;

	private Long id;

	private String pluginName;

	private String pluginVersion;

	private String commandClsId;

	private Date createDate;

	private Integer successResults;

	private Integer warningResults;

	private Integer errorResults;

	private Boolean cancelled;

	private Boolean scheduled;

	public ExecutedTask(ITask task, Integer successResults, Integer warningResults, Integer errorResults) {
		super();
		this.id = task.getId();
		this.pluginName = task.getPlugin().getName();
		this.pluginVersion = task.getPlugin().getVersion();
		this.commandClsId = task.getCommandClsId();
		this.createDate = task.getCreateDate();
		this.successResults = successResults;
		this.warningResults = warningResults;
		this.errorResults = errorResults;
		this.cancelled = task.isDeleted();
		this.scheduled = task.getCronExpression() != null && !task.getCronExpression().isEmpty();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public String getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	public String getCommandClsId() {
		return commandClsId;
	}

	public void setCommandClsId(String commandClsId) {
		this.commandClsId = commandClsId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getErrorResults() {
		return errorResults;
	}

	public void setErrorResults(Integer errorResults) {
		this.errorResults = errorResults;
	}

	public Integer getSuccessResults() {
		return successResults;
	}

	public void setSuccessResults(Integer successResults) {
		this.successResults = successResults;
	}

	public Integer getWarningResults() {
		return warningResults;
	}

	public void setWarningResults(Integer warningResults) {
		this.warningResults = warningResults;
	}

	public Boolean getCancelled() {
		return cancelled;
	}

	public void setCancelled(Boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Boolean getScheduled() {
		return scheduled;
	}

	public void setScheduled(Boolean scheduled) {
		this.scheduled = scheduled;
	}

}
