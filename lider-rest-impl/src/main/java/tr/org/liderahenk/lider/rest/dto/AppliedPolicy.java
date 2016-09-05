package tr.org.liderahenk.lider.rest.dto;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;

/**
 * This is a specialized class which is used to list executed policies with some
 * additional info.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppliedPolicy implements Serializable {

	private static final long serialVersionUID = -8740486474920176215L;

	private Long id;

	private String label;

	private Date createDate;

	private Integer successResults;

	private Integer errorResults;

	public AppliedPolicy(IPolicy policy, Integer successResults, Integer errorResults) {
		super();
		this.id = policy.getId();
		this.label = policy.getLabel();
		this.createDate = policy.getCreateDate();
		this.successResults = successResults;
		this.errorResults = errorResults;
	}

	public AppliedPolicy(Long id, String label, Date createDate, Integer successResults, Integer errorResults) {
		super();
		this.id = id;
		this.label = label;
		this.createDate = createDate;
		this.successResults = successResults;
		this.errorResults = errorResults;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getSuccessResults() {
		return successResults;
	}

	public void setSuccessResults(Integer successResults) {
		this.successResults = successResults;
	}

	public Integer getErrorResults() {
		return errorResults;
	}

	public void setErrorResults(Integer errorResults) {
		this.errorResults = errorResults;
	}

}
