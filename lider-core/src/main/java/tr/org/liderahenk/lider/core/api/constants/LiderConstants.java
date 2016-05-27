package tr.org.liderahenk.lider.core.api.constants;

/**
 * Provides common constants used throughout the system.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class LiderConstants {

	/**
	 * Event topics used by {@link EventAdmin}
	 */
	public static final class EVENTS {
		/**
		 * Thrown when file received
		 */
		public static final String FILE_RECEIVED = "tr/org/liderahenk/file/received";
		/**
		 * Thrown when plugin registered
		 */
		public static final String PLUGIN_REGISTERED = "tr/org/liderahenk/plugin/registered";
		/**
		 * Thrown when task status message received
		 */
		public static final String TASK_STATUS_RECEIVED = "tr/org/liderahenk/task/status/received";
		/**
		 * Thrown when policy status message received
		 */
		public static final String POLICY_STATUS_RECEIVED = "tr/org/liderahenk/policy/status/received";
	}

}
