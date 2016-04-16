package tr.org.liderahenk.lider.karafconsole.commands;

import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.support.table.Col;
import org.apache.karaf.shell.support.table.Row;
import org.apache.karaf.shell.support.table.ShellTable;

import tr.org.liderahenk.lider.core.api.persistence.dao.IPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;

/**
 * Custom Karaf console command to list plugins. Type <code>plugin:list</code>
 * to execute command.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Command(scope = "plugin", name = "list", description = "Lists plugin records")
public class PluginListCommand implements Action {

	private IPluginDao pluginDao;

	@Override
	public Object execute(CommandSession session) throws Exception {
		ShellTable table = new ShellTable();
		// ID
		Col column = table.column("ID");
		column.alignCenter().bold();
		// Label
		column = table.column("Name");
		column.alignCenter().cyan();
		// Version
		column = table.column("Version");
		column.alignCenter().cyan();
		// Create Date
		column = table.column("Create Date");
		column.alignCenter();
		// Modify Date
		column = table.column("Modify Date");
		column.alignCenter();
		// Deleted
		column = table.column("Deleted");
		column.alignCenter();

		List<? extends IPlugin> list = pluginDao.findAll(IPlugin.class, null);
		if (list != null) {
			for (IPlugin plugin : list) {
				Row row = table.addRow();
				row.addContent(plugin.getId(), plugin.getName(), plugin.getVersion(), plugin.getCreateDate(),
						plugin.getModifyDate() != null ? plugin.getModifyDate() : "", plugin.isDeleted() ? "x" : "");
			}
		}
		table.print(System.out);

		return null;
	}

	public void setPluginDao(IPluginDao pluginDao) {
		this.pluginDao = pluginDao;
	}

}
