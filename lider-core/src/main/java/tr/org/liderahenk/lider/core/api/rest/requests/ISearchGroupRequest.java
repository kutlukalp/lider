package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.Map;
import java.util.Set;

public interface ISearchGroupRequest extends IRequest {

	String getName();

	boolean isSearchAgents();

	boolean isSearchUsers();

	boolean isSearchGroups();

	Map<String, String> getCriteria();

	Set<? extends ISearchGroupEntryRequest> getEntries();

}
