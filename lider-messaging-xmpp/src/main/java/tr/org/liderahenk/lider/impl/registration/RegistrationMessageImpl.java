package tr.org.liderahenk.lider.impl.registration;

import java.util.Date;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.messaging.IRegistrationMessage;
import tr.org.liderahenk.lider.core.api.messaging.MessageType;

/**
 * Default implementation for {@link IRegistrationMessage}.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class RegistrationMessageImpl implements IRegistrationMessage {

	private MessageType type;
	private String from;
	private String password;
	private String hostname;
	private String ipAddresses;
	private String macAddresses;
	private Map<String, Object> data;
	private Date timestamp;

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getIpAddresses() {
		return ipAddresses;
	}

	public void setIpAddresses(String ipAddresses) {
		this.ipAddresses = ipAddresses;
	}

	public String getMacAddresses() {
		return macAddresses;
	}

	public void setMacAddresses(String macAddresses) {
		this.macAddresses = macAddresses;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
