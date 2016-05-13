package tr.org.liderahenk.lider.persistence.commands;

import tr.org.liderahenk.lider.core.api.plugin.ICommand;

public abstract class BaseCommand implements ICommand {

	private static final String NAME = "LIDER-PERSISTENCE";
	private static final String VERSION = "1.0.0";

	@Override
	public String getPluginName() {
		return NAME;
	}

	@Override
	public String getPluginVersion() {
		return VERSION;
	}

}
