package Encryption;

public class ProxyDef {
	private static String reencryptionKeyPath = "/Users/chencaixia/SecretCloud/Proxy/reencryptionKeys/";
	private static String DESkeyPath = "/Users/chencaixia/SecretCloud/Proxy/DESkeys/";
	private static String ciphersPath = "/Users/chencaixia/SecretCloud/Proxy/ciphers/";
	private static String conditionPath = "/Users/chencaixia/SecretCloud/Proxy/conditions/";
	public static final String paramsPath = "/Users/chencaixia/SecretCloud/Proxy/params/";
	
	public static String getCiphersPath(String id, String fileName) {
		String path = ciphersPath + id + "/" + fileName;
		return path;
	}
	
	public static String getDESkeyPath(String id, String fileName) {
		fileName = fileName.replace(".", "");
		String path = DESkeyPath + id + "_" + fileName + "_desKey.dat";
		return path;
	}
	
	public static String getReencryptionKeyPath(String rkName) {
		String path = reencryptionKeyPath + rkName;
		return path;
	}
	
	public static String getConditionPath(String id, String fileName) {
		fileName = fileName.replace(".", "");
		String path = conditionPath + id + "_" + fileName + "_condition.dat";
		return path;
	}
}
