package tr.org.liderahenk.lider.core.api.rest.processors;

import java.util.Date;

import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IPolicyRequestProcessor {

	/**
	 * 
	 * @param json
	 * @return
	 */
	IRestResponse execute(String json);

	/**
	 * 
	 * @param json
	 * @return
	 */
	IRestResponse add(String json);

	/**
	 * 
	 * @param json
	 * @return
	 */
	IRestResponse update(String json);

	/**
	 * 
	 * @param label
	 * @param active
	 * @return
	 */
	IRestResponse list(String label, Boolean active);

	/**
	 * 
	 * @param id
	 * @return
	 */
	IRestResponse get(Long id);

	/**
	 * 
	 * @param id
	 * @return
	 */
	IRestResponse delete(Long id);

	/**
	 * 
	 * @param label
	 * @param createDateRangeStart
	 * @param createDateRangeEnd
	 * @param status
	 * @param maxResults
	 * @return
	 */
	IRestResponse listAppliedPolicies(String label, Date createDateRangeStart, Date createDateRangeEnd, Integer status,
			Integer maxResults);

	/**
	 * 
	 * @param policyId
	 * @return
	 */
	IRestResponse listCommands(Long policyId);

}
