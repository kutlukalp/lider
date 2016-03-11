package tr.org.liderahenk.lider.core.api.rest.requests;

import java.io.Serializable;
import java.util.Date;

public interface IRequest extends Serializable {
	
	Date getTimestamp();

}
