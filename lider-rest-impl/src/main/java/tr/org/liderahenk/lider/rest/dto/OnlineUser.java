package tr.org.liderahenk.lider.rest.dto;

import java.io.Serializable;
import java.util.Date;

public class OnlineUser implements Serializable {

	private static final long serialVersionUID = -1532561934289814137L;

	private String hostname;

	private String ipAddresses; // Comma-separated IP addresses

	private String dn;

	private String username;

	private Date createDate;

	public OnlineUser(String hostname, String ipAddresses, String dn, String username, Date createDate) {
		super();
		this.hostname = hostname;
		this.ipAddresses = ipAddresses;
		this.dn = dn;
		this.username = username;
		this.createDate = createDate;
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

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "OnlineUser [hostname=" + hostname + ", ipAddresses=" + ipAddresses + ", dn=" + dn + ", username="
				+ username + ", createDate=" + createDate + "]";
	}

}
