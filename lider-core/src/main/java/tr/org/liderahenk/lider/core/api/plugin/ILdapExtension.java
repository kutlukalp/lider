package tr.org.liderahenk.lider.core.api.plugin;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * Interface defines ldif file to extend ldap schema
 */
public interface ILdapExtension {
	/**
	 * 
	 * @return ldif file as inputstream
	 * @throws IOException
	 */
	InputStream getLdifFile() throws IOException;
}
