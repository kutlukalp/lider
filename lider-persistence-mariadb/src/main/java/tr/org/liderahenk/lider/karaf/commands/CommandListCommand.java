package tr.org.liderahenk.lider.karaf.commands;

import java.util.List;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.support.table.Col;
import org.apache.karaf.shell.support.table.Row;
import org.apache.karaf.shell.support.table.ShellTable;

import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;

/**
 * Custom Karaf console command to list commands (executed tasks & policies).
 * Type <code>command:list</code> to execute command.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Service
@Command(scope = "command", name = "list", description = "Lists command records")
public class CommandListCommand implements Action {

	@Reference
	private ICommandDao commandDao;

	public CommandListCommand() {
	}

	@Override
	public Object execute() throws Exception {
		ShellTable table = new ShellTable();
		// ID
		Col column = table.column("ID");
		column.alignCenter().bold();
		// DN List
		column = table.column("DN List");
		column.alignCenter().cyan().maxSize(100);
		// DN Type
		column = table.column("DN Type");
		column.alignCenter().cyan();
		// Owner UID
		column = table.column("Owner UID");
		column.alignCenter();
		// Policy ID
		column = table.column("Related Policy ID");
		column.alignCenter();
		// Task ID
		column = table.column("Related Task ID");
		column.alignCenter();

		List<? extends ICommand> list = commandDao.findAll(ICommand.class, null);
		if (list != null) {
			for (ICommand command : list) {
				Row row = table.addRow();
				row.addContent(command.getId(), command.getDnList(), command.getDnType(), command.getCommandOwnerUid(),
						command.getPolicy() != null ? command.getPolicy().getId() : null,
						command.getTask() != null ? command.getTask().getId() : null);
			}
		}
		table.print(System.out);

		return null;
	}

	public void setCommandDao(ICommandDao commandDao) {
		this.commandDao = commandDao;
	}

}
