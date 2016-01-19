package tr.org.liderahenk.lider.impl.log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import tr.org.liderahenk.lider.core.api.log.IOperationLog;

public class Checksum {
	
	public static String CalculateChecksum(IOperationLog log) throws NoSuchAlgorithmException
	{		
		MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
	
		byte[] salt = new String("teslaRulz").getBytes();
		
		
		if(null != log.getAction() && !log.getAction().isEmpty()){
			messageDigest.update(log.getAction().getBytes());
		}
		if(null != log.getActive()){
			messageDigest.update(log.getActive().toString().getBytes());
		}
		if(null != log.getClientCN() && !log.getClientCN().isEmpty()){
			messageDigest.update(log.getClientCN().getBytes());
		}
		if( null != log.getCrudType() ){
			messageDigest.update(log.getCrudType().toString().getBytes());
		}
//		if(null != log.getDate())
//			messageDigest.update(log.getDate().toString().getBytes());
//		if(null != log.getId())
//			messageDigest.update(log.getId().toString().getBytes());
		if(null != log.getLogText() && !log.getLogText().isEmpty()){
			messageDigest.update(log.getLogText().getBytes());
		}
		if(null != log.getServerIp() && !log.getServerIp().isEmpty()){
			messageDigest.update(log.getServerIp().getBytes());
		}
		if(null != log.getPluginId() && !log.getPluginId().isEmpty()){
			messageDigest.update(log.getPluginId().getBytes());
		}
		if(null != log.getResultCode() && !log.getResultCode().isEmpty()){
			messageDigest.update(log.getResultCode().getBytes());
		}
		if(null != log.getTaskId() && !log.getTaskId().isEmpty()){
			messageDigest.update(log.getTaskId().getBytes());
		}
		if(null != log.getUserId() && !log.getUserId().isEmpty()){
			messageDigest.update(log.getUserId().getBytes());
		}
		
		messageDigest.update(salt);
		
		byte[] digestBytes = messageDigest.digest();

		StringBuffer stringBuffer = new StringBuffer("");
		
		for (int i = 0; i < digestBytes.length; i++) {
			stringBuffer.append(Integer.toString((digestBytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		return stringBuffer.toString();		
	}
}
