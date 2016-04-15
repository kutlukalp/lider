package tr.org.liderahenk.lider.karafconsole.commands;

import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.support.table.Col;
import org.apache.karaf.shell.support.table.Row;
import org.apache.karaf.shell.support.table.ShellTable;

import tr.org.liderahenk.lider.core.api.persistence.dao.IProfileDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;

@Command(scope = "profile", name = "list", description = "Lists profile records")
public class ProfileCommand implements Action {

	private IProfileDao profileDao;

	public void setProfileDao(IProfileDao profileDao) {
		this.profileDao = profileDao;
	}

	@Override
	public Object execute(CommandSession session) throws Exception {
		ShellTable table = new ShellTable();
		// ID
		Col column = table.column("ID");
		column.alignCenter().bold();
		// Label
		column = table.column("Label");
		column.alignCenter().cyan();
		// Plugin
		column = table.column("Related Plugin");
		column.alignCenter();
		// Create Date
		column = table.column("Creation");
		column.alignCenter();
		// Deleted
		column = table.column("Deleted");
		column.alignCenter();

		List<? extends IProfile> list = profileDao.findAll(IProfile.class, null);
		if (list != null) {
			for (IProfile profile : list) {
				Row row = table.addRow();
				row.addContent(profile.getId(), profile.getLabel(),
						profile.getPlugin().getName() + "-" + profile.getPlugin().getVersion(), profile.getCreateDate(),
						profile.isDeleted() ? "x" : "");
			}
		}
		table.print(System.out);

		return null;
	}

}
