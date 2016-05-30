package tr.org.liderahenk.lider.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.ISystemEventsProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "SystemEventsProperties")
public class SystemEventsPropertiesImpl implements ISystemEventsProperties {

	private static final long serialVersionUID = -8006113886525801620L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "SystemEventID", nullable = true)
	private Integer systemEventsId;

	@Column(name = "ParamName", nullable = true)
	private String paramName;

	@Lob
	@Column(name = "ParamValue", nullable = true)
	private String paramValue;

	public SystemEventsPropertiesImpl(Long id, Integer systemEventsId, String paramName, String paramValue) {
		super();
		this.id = id;
		this.systemEventsId = systemEventsId;
		this.paramName = paramName;
		this.paramValue = paramValue;
	}

	public SystemEventsPropertiesImpl(ISystemEventsProperties property) {
		this.id = property.getSystemEventPropertyId();
		this.systemEventsId = property.getSystemEventsId();
		this.paramName = property.getParamName();
		this.paramValue = property.getParamValue();
	}

	public SystemEventsPropertiesImpl() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	@Override
	public Integer getSystemEventsId() {
		return systemEventsId;
	}

	public void setSystemEventsId(Integer systemEventsId) {
		this.systemEventsId = systemEventsId;
	}

	@Override
	public Long getSystemEventPropertyId() {
		return id;
	}

}
