package tr.org.liderahenk.lider.core.api.rest;


import java.io.Serializable;

/**
 * Rest request object representing REST requests made to Lider Rest Services
 * 
 * <blockquote>RESTFUL URL: /resource/access/attribute/command/action<br>
 * 					Samples:<br>	
 * 						<b>/ObjectClass/ObjectDN_or_ObjectContainerDN/USBPLG/KEYBOARD/ENABLED</b><br>
 * 						<b>/ObjectClass/ObjectDN_or_ObjectContainerDN/USBPLG/CAMERA/DISABLED<br>
 * 						<b>/ObjectClass/ObjectDN_or_ObjectContainerDN/USBPLG/DISK/ENABLED<br>
 * 						<b>/ObjectClass/ObjectDN_or_ObjectContainerDN/SCRIPTPLG/SAVE/SCRIPT</b><br>
 * 						<b>/ObjectClass/ObjectDN_or_ObjectContainerDN/SCRIPTPLG/GET/SCRIPT</b><br>
 * 						<b>/ObjectClass/ObjectDN_or_ObjectContainerDN/SCRIPTPLG/DELETE/SCRIPT</b><br>
 * 						<b>/ObjectClass/ObjectDN_or_ObjectContainerDN/SCRIPTPLG/RUN/SCRIPT</b><br>
 * </blockquote>
 * Note: Any command can use IRestRequestBody.customParameterMap attribute to send their custom parameters
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * 
 */
public interface IRestRequest extends Serializable{
	
	/**
	 * @return ObjectClass of a single target entry or target entries below a container DN ( TaskManager decides the actual case according to the given DN in access attribute),
	 *  <br>i.e. resource type ==> ObjectClass (PardusUser or PardusComputer etc.), 
	 * plugin may have extended LDAP schema with new ObjectClass(es) and may even use these new ones in Commands too.
	 */
	String getResource();
	
	/**
	 *   
	 * @return DN of a single target entry or a container of target entries ( TaskManager decides the actual case according to the given DN in access attribute),<br/>
	 *  i.e. a single entry such as  <br/><b>cn=bduman,ou=people,ou=ank,dc=example,dc=com</b>, <br/>  or all the entries under <b>ou=people,ou=ank,dc=example,dc=com</b> 
	 *  that have target ObjectClass given in actual <b>resource</b> attribute
	 */
	String getAccess();
	
	/**
	 * 
	 * @return 3rd path variable in REST-style requests
	 */
	String getAttribute();
	
	/**
	 * 
	 * @return 4th path variable in REST-style requests
	 */
	String getCommand();
	
	/**
	 * 
	 * @return 5th path variable in REST-style requests
	 */
	String getAction();
	
	/**
	 * 
	 * @return full URL path for request; i.e resource/access/attribute/command/action
	 */
	String getURL();
	
	/**
	 * 
	 * @return user making the request
	 */
	String getUser();
	
	/**
	 * 
	 * @return (method POST) request body of the request
	 */
	IRestRequestBody getBody();
	
}
