package tr.org.liderahenk.lider.core.api.plugin;

import java.util.Map;

import tr.org.liderahenk.lider.core.api.rest.IRestRequest;


/**
 * 
 * <p> This is the interface for server-side plugin command implementations.Any class implementing this interface
 * can deploy a new command to the server. 
 * ServiceRouter directs RestRequests
 * to the appropriate command if exists according to respective properties.</p>    
 * 
 * <p>The first two parameters (/ObjectClass/ObjectDN_or_ObjectContainerDN) are reserved by the core system, but last three of these parameters may be freely defined by the plugin developer (attribute/command/action)
 * and we match the actual command to run according to actual values of these three parameters, any usage is welcome as long as they
 * form a <b>unique</b> composition!</p>
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * 
 */
public interface ICommand {
	
	
	/**
	 * Any custom plugin command should implement this method and return a non-null {@link ICommandResult}, 
	 * A command may access to {@link IAuthService}, ILdapService and more plugin services provided by the core system to do its necessary job
	 * @return command result {@link ICommandResult}
	 */
	ICommandResult execute( ICommandContext context );
	
	/**
	 * Any custom plugin command should implement this method and return a non-null {@link ICommandResult}, 
	 * It should check whether the arguments are valid.
	 * @return command result {@link ICommandResult}
	 */
	ICommandResult validate( ICommandContext context );

	/**
	 * 
	 * @return attribute parameter matcher for this command
	 * 
	 * @see IRestRequest#getAttribute()
	 */
	String getAttribute();
	
	/**
	 * 
	 * @return command parameter matcher for this command
	 * 
	 * @see IRestRequest#getCommand()
	 */
	String getCommand();
	
	/**
	 * 
	 * @return command parameter matcher for this command
	 * 
	 * @see IRestRequest#getAction()
	 */
	String getAction();

	/**
	 * 
	 * @return id of the plugin implementing this command
	 */
	String getPluginId();
	
	/**
	 * 
	 * @return title of the plugin implementing this command
	 */
	String getPluginTitle();

	/**
	 * 
	 * @return version of the plugin implementing this command
	 */
	String getPluginVersion();

	/**
	 * 
	 * @return group id of command
	 */
	String getGroupId();

	/**
	 * 
	 * @return group title of command
	 */
	String getGroupTitle();
	
	/**
	 * 
	 * @return i18n group titles of command, key= locale, value= title
	 */
	Map<String,String> getGroupTitles();
	
	/**
	 * Any custom plugin command should implement this method and return a non-null String to String Map
	 * that explains the command in different languages. Keys should be 2 letters long language identifier
	 * and values should be the explanation in that language.
	 * @return language to display name map
	 */
	Map<String,String> getDisplayName();
	
	/**  
	 * @return true if this command needs agent interaction to fulfill its job, false otherwise
	 */
	Boolean needsTask();
	
}
