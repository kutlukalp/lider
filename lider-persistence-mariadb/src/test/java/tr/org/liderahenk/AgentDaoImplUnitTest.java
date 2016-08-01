package tr.org.liderahenk;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.persistence.dao.AgentDaoImpl;
import tr.org.liderahenk.lider.persistence.entities.AgentImpl;

public class AgentDaoImplUnitTest {

	AgentDaoImpl agentDao;
	EntityManager entityManager;
	
	
	
	
	@Test
	public void save(){
		entityManager = Mockito.mock(EntityManager.class);
		agentDao = Mockito.mock(AgentDaoImpl.class);
		agentDao.setEntityManager(entityManager);
		
//		doAnswer(new Answer<Void>() {
//		    public Void answer(InvocationOnMock invocation) {
//		      Object[] args = invocation.getArguments();
//		      System.out.println("called with arguments: " + Arrays.toString(args));
//		      return null;
//		    }
//		}).when(mockWorld).setState(anyString());
//		
//		when(entityManager.persist(any(AgentImpl.class))).thenAnswer(new Answer<AgentImpl>() {
//			@Override
//			public AgentImpl answer(InvocationOnMock invocation) throws Throwable {
//				Long id = (Long)invocation.getArguments()[0];
//				AgentImpl a = new AgentImpl();
//				a.setId(id);
//				return a;
//			}
//		});
		
		
		when(agentDao.save(any(IAgent.class))).thenAnswer(new Answer<AgentImpl>() {
			@Override
			public AgentImpl answer(InvocationOnMock invocation) throws Throwable {
				IAgent a = (IAgent)invocation.getArguments()[0];
				AgentImpl aimpl = new AgentImpl(a);
				aimpl.setId(1L);
				return aimpl;
			}
		});
		
		
		IAgent agent = Mockito.mock(IAgent.class);
		IAgent saveAgent = agentDao.save(agent);
		
		assertNotNull(saveAgent);
		assertTrue(saveAgent.getId() == 1L);
		
		
	}
	
	@Test
	public void find(){
		entityManager = Mockito.mock(EntityManager.class);
		agentDao = Mockito.mock(AgentDaoImpl.class);
		agentDao.setEntityManager(entityManager);
//		when(entityManager.find(AgentImpl.class, anyLong())).thenAnswer(new Answer<AgentImpl>() {
//			@Override
//			public AgentImpl answer(InvocationOnMock invocation) throws Throwable {
//				Long id = (Long)invocation.getArguments()[1];
//				AgentImpl a = new AgentImpl();
//				a.setId(id);
//				return a;
//			}
//		});
		
		when(agentDao.find(anyLong())).thenAnswer(new Answer<AgentImpl>() {
			@Override
			public AgentImpl answer(InvocationOnMock invocation) throws Throwable {
				Long id = (Long)invocation.getArguments()[0];
				AgentImpl a = new AgentImpl();
				a.setId(id);
				return a;
			}
		});
		
		AgentImpl find = agentDao.find(1L);
		assertTrue(find.getId() > 0);
		
	}
	
	
	
}
