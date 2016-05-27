package tr.org.liderahenk.lider.core.api.persistence.entities;

import tr.org.liderahenk.lider.core.api.rest.enums.DNType;

/**
 * ISearchGroupEntry is entity class for search group entries.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.ISearchGroup
 *
 */
public interface ISearchGroupEntry extends IEntity {

	/**
	 * 
	 * @return parent entity
	 */
	ISearchGroup getGroup();

	/**
	 * 
	 * @return LDAP DN
	 */
	String getDn();

	/**
	 * 
	 * @return DN type
	 */
	DNType getDnType();

}
