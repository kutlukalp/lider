package tr.org.liderahenk.lider.persistence.mariadb.plugin.impl;

import static org.mockito.Mockito.when;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import junit.framework.TestCase;
import tr.org.liderahenk.lider.core.api.plugin.IPluginDbService;

@RunWith(JUnit4.class)
public class PluginDbServiceImplTest extends TestCase {

	IPluginDbService pluginDbService;
	EntityManager entityManager;

	@Before
	public void setUp() {
		pluginDbService = new PluginDbServiceImpl();
		entityManager = mock(EntityManager.class);
		// inject our stubbed entity manager
		((PluginDbServiceImpl) pluginDbService).setEntityManager(entityManager);
	}
	
	@Test
	public void findAll() {
		// TODO
		// TODO
//		// stub the entity manager to return a meaningful result when somebody asks
//        // for the FIND_ALL named query
//        Query query = mock(Query.class);
//        when(entityManager.createQuery("")).thenReturn(query);
//        // stub the query returned above to return a meaningful result when somebody
//        // asks for the result list
//        List<Comment> dummyResult = new LinkedList<Comment>();
//        when(query.getResultList()).thenReturn(dummyResult);
//
//        // let's call findAll() and see what it does
//        List<Comment> result = commentService.findAll();
//
//        // did it request the named query?
//        verify(entityManager).createNamedQuery(Comment.FIND_ALL, Comment.class);
//        // did it ask for the result list of the named query?
//        verify(query).getResultList();
//        // did it return the result list of the named query?
//        assertSame(dummyResult, result);
//
//        // success, it did all of the above!
	}

}
