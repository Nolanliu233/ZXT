package cn.tongyuankeji.common.encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Hex;

import cn.tongyuankeji.common.parameters.ConstantBase;

/**
 * MD5密码加密
 * 
 * @author 代平 2017-05-02 创建
 */
public class Md5PwdEncoder implements PwdEncoder {
	private static Md5PwdEncoder md5Instance= null;
	
	public static Md5PwdEncoder getMd5(){
		if(null == md5Instance){
			md5Instance = new Md5PwdEncoder();
		}
		return md5Instance;
	}
	
	public String encodePassword(String rawPass) {
		return encodePassword(rawPass, defaultSalt);
	}

	public String encodePassword(String rawPass, String salt) {
		String saltedPass = mergePasswordAndSalt(rawPass, salt, false);
		MessageDigest messageDigest = getMessageDigest();
		byte[] digest;
		try {
			digest = messageDigest.digest(saltedPass.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 not supported!");
		}
		return new String(Hex.encodeHex(digest));
	}

	public boolean isPasswordValid(String encPass, String rawPass) {
		return isPasswordValid(encPass, rawPass, defaultSalt);
	}

	public boolean isPasswordValid(String encPass, String rawPass, String salt) {
		if (encPass == null) {
			return false;
		}
		String pass2 = encodePassword(rawPass, salt);
		return encPass.equals(pass2);
	}

	protected final MessageDigest getMessageDigest() {
		String algorithm = "MD5";
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("No such algorithm ["
					+ algorithm + "]");
		}
	}

	/**
	 * Used by subclasses to extract the password and salt from a merged
	 * <code>String</code> created using
	 * {@link #mergePasswordAndSalt(String,Object,boolean)}.
	 * <p>
	 * The first element in the returned array is the password. The second
	 * element is the salt. The salt array element will always be present, even
	 * if no salt was found in the <code>mergedPasswordSalt</code> argument.
	 * </p>
	 * 
	 * @param mergedPasswordSalt
	 *            as generated by <code>mergePasswordAndSalt</code>
	 * 
	 * @return an array, in which the first element is the password and the
	 *         second the salt
	 * 
	 * @throws IllegalArgumentException
	 *             if mergedPasswordSalt is null or empty.
	 */
	protected String mergePasswordAndSalt(String password, Object salt,
			boolean strict) {
		if (password == null) {
			password = "";
		}
		if (strict && (salt != null)) {
			if ((salt.toString().lastIndexOf("{") != -1)
					|| (salt.toString().lastIndexOf("}") != -1)) {
				throw new IllegalArgumentException(
						"Cannot use { or } in salt.toString()");
			}
		}
		if ((salt == null) || "".equals(salt)) {
			return password;
		} else {
			return password + "{" + salt.toString() + "}";
		}
	}

	/**
	 * 混淆码。防止破解。
	 */
	private String defaultSalt;

	/**
	 * 获得混淆码
	 * 
	 * @return
	 */
	public String getDefaultSalt() {
		return defaultSalt;
	}

	/**
	 * 设置混淆码
	 * 
	 * @param defaultSalt
	 */
	public void setDefaultSalt(String defaultSalt) {
		this.defaultSalt = defaultSalt;
	}
	
	////////////////////////////////////////
	

	//private final static Logger logger = LoggerFactory.getLogger(Md5PwdEncoder.class);

	/**
	 * @Description 字符串加密为MD5
	 * 				中文加密一致通用,必须转码处理：  
	 * 					plainText.getBytes("UTF-8")
	 * @param plainText
	 * 				需要加密的字符串
	 * @return
	 */
	public static String toMD5(String plainText) {
		StringBuffer rlt = new StringBuffer();
		try {
			rlt.append(md5String(plainText.getBytes(ConstantBase.ENCODING)));
		} catch (UnsupportedEncodingException e) {
			//logger.error(" CipherHelper toMD5 exception:", e.toString());
		}
		return rlt.toString();
	}

	/**
	 * MD5 参数签名生成算法
	 * @param HashMap<String,String> params 请求参数集，所有参数必须已转换为字符串类型
	 * @param String secret 签名密钥
	 * @return 签名
	 * @throws IOException
	 */
	public static String getSignature(HashMap<String, String> params, String secret) throws IOException {
		Map<String, String> sortedParams = new TreeMap<String, String>(params);
		Set<Entry<String, String>> entrys = sortedParams.entrySet();
		StringBuilder basestring = new StringBuilder();
		for (Entry<String, String> param : entrys) {
			basestring.append(param.getKey()).append("=").append(param.getValue());
		}
		basestring.append(secret);
		byte[] bytes = md5Raw(basestring.toString().getBytes(ConstantBase.ENCODING));
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex);
		}
		return sign.toString();
	}

	public static byte[] md5Raw(byte[] data) {
		byte[] md5buf = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			md5buf = md5.digest(data);
		} catch (Exception e) {
			md5buf = null;
			e.printStackTrace(System.err);
		}
		return md5buf;
	}

	public static String md5String(byte[] data) {
		String md5Str = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			md5Str = "";
			byte[] buf = md5.digest(data);
			for (int i = 0; i < buf.length; i++) {
				md5Str += byte2Hex(buf[i]);
			}
		} catch (Exception e) {
			md5Str = null;
			e.printStackTrace(System.err);
		}
		return md5Str;
	}

	public static String byte2Hex(byte b) {
		String hex = Integer.toHexString(b);
		if (hex.length() > 2) {
			hex = hex.substring(hex.length() - 2);
		}
		while (hex.length() < 2) {
			hex = "0" + hex;
		}
		return hex;
	}
	
	public static String byte2Hex(byte[] bytes) {
		Formatter formatter = new Formatter();
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		String hash = formatter.toString();
		formatter.close();
		return hash;
	}

}