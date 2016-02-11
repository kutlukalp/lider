package tr.org.liderahenk.lider.impl.command;

import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.ICommandContext;
import tr.org.liderahenk.lider.core.api.plugin.ICommandResult;
import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskStatusUpdate;

public class SampleCommand implements ICommand, ITaskAwareCommand{

	@Override
	public void onTaskUpdate(ITaskStatusUpdate taskStatusUpdate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ICommandResult execute(ICommandContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPluginName() {
		// TODO Auto-generated method stub
		return "sample";
	}

	@Override
	public String getPluginVersion() {
		// TODO Auto-generated method stub
		return "sample";
	}

	@Override
	public String getCommandId() {
		// TODO Auto-generated method stub
		return "sampleCommand";
	}

	@Override
	public Boolean needsTask() {
		// TODO Auto-generated method stub
		return false;
	}

}
