package tr.org.liderahenk.lider.rest.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.router.IServiceRegistry;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

/**
 * This implementation is used to get task AND report codes (provided by all
 * bundles). To achieve this, it simply uses {@link ServiceRegistryImpl}
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class GetTaskReportCodesCommand extends BaseCommand {

	private ICommandResultFactory resultFactory;
	private IServiceRegistry registry;

	@Override
	public ICommandResult execute(ICommandContext context) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("task-codes", registry.getTaskCodes());
		resultMap.put("report-codes", registry.getReportCodes());
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this, resultMap);
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this);
	}

	@Override
	public String getCommandId() {
		return "GET-TASK-REPORT-CODES";
	}

	@Override
	public Boolean executeOnAgent() {
		return false;
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public void setRegistry(IServiceRegistry registry) {
		this.registry = registry;
	}

}
