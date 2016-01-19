package tr.org.liderahenk.lider.impl.messaging;

import tr.org.pardus.mys.core.api.messaging.IMessage;
import tr.org.pardus.mys.core.api.messaging.IMessageFactory;
import tr.org.pardus.mys.core.api.taskmanager.ITask;
import tr.org.pardus.mys.ldap.api.ILDAPService;

/**
 * Default implementation for {@link IMessageFactory}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class MessageFactoryImpl implements IMessageFactory {

	@Override
	public IMessage create(ITask task) {
		String recipient = task.getTargetJID();//getJid(task.getTargetObjectDN());
		String message = null;
		try{
			message = task.toJSON(); 
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return new MessageImpl(recipient, message);
	}

}
