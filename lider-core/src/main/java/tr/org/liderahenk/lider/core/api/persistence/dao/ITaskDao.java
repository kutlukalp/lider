package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;

import tr.org.liderahenk.lider.core.api.persistence.IBaseDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;

/**
 * Provides task related database operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface ITaskDao extends IBaseDao<ITask> {

	List<? extends ICommand> findFutureTasks();

}
