package tr.org.liderahenk.lider.core.api.deployer;

import java.nio.file.Path;

public interface ILiderHotDeployListener {
	
	void register(Path dir);
	void processEvents();

}
