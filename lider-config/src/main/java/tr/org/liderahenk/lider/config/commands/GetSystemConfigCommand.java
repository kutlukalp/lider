package tr.org.liderahenk.lider.config.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.plugin.CommandResultStatus;
import tr.org.liderahenk.lider.core.api.plugin.ICommandContext;
import tr.org.liderahenk.lider.core.api.plugin.ICommandResult;
import tr.org.liderahenk.lider.core.api.plugin.ICommandResultFactory;

/**
 * This ICommand implementation provides system configuration (such as LDAP
 * connection parameters and XMPP connection parameters) to Lider Console.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class GetSystemConfigCommand extends BaseCommand {

	private static Logger logger = LoggerFactory.getLogger(GetSystemConfigCommand.class);

	private ICommandResultFactory resultFactory;
	private IConfigurationService configurationService;

	@Override
	public ICommandResult execute(ICommandContext context) throws Exception {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		// XMPP configuration
		resultMap.put("xmppHost", configurationService.getXmppHost());
		resultMap.put("xmppPort", configurationService.getXmppPort());
		resultMap.put("xmppServiceName", configurationService.getXmppServiceName());
		resultMap.put("xmppMaxRetryConnectionCount", configurationService.getXmppMaxRetryConnectionCount());
		resultMap.put("xmppPacketReplayTimeout", configurationService.getXmppPacketReplayTimeout());
		resultMap.put("xmppPingTimeout", configurationService.getXmppPingTimeout());
		resultMap.put("xmppUseSsl", configurationService.getXmppUseSsl());
		resultMap.put("xmppUsername", "TODO"); // TODO
		resultMap.put("xmppPassword", "TODO"); // TODO
		// LDAP configuration
		resultMap.put("ldapServer", configurationService.getLdapServer());
		resultMap.put("ldapPort", configurationService.getLdapPort());
		resultMap.put("ldapUsername", "TODO"); // TODO
		resultMap.put("ldapPassword", "TODO"); // TODO
		resultMap.put("ldapRootDn", configurationService.getLdapRootDn());
		resultMap.put("ldapUseSsl", configurationService.getLdapUseSsl());

		logger.debug("System config: {}", resultMap);

		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this, resultMap);
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getCommandId() {
		return "GET-SYSTEM-CONFIG";
	}

	@Override
	public Boolean needsTask() {
		return false;
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
