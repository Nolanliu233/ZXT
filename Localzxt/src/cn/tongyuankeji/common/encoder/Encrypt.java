
package cn.tongyuankeji.common.encoder;

/**
 * SSO 对称加密父类
 * <p>
 * @author   liusujia	 
 */
public abstract class Encrypt {

	/**
	 * 字符串内容加密
	 * <p>
	 * @param value 加密内容
	 * @param key 密钥
	 * @return
	 * @throws Exception
	 */
	public abstract String encrypt(String value, String key) throws Exception;

	/**
	 * 字符串内容解密
	 * <p>
	 * @param value 解密内容
	 * @param key 密钥
	 * @return
	 * @throws Exception
	 */
	public abstract String decrypt(String value, String key) throws Exception;
}
