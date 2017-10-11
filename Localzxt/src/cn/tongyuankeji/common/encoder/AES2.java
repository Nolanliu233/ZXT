package cn.tongyuankeji.common.encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;

import cn.tongyuankeji.common.parameters.ConstantBase;

/**
 * AES加密工具类。先用AES(UTF-8)解密，再用Base64.decode
 * 
 * @author 代平 2017-08-23 创建
 */
public class AES2
{
	static final Logger log = LogManager.getLogger(AES2.class.getName());

	public static String decrypt(String rawData, String key)
	{
		SecretKeySpec secretKey = null;
		Cipher cipher = null;
		byte[] byteKey = null, byteData = null, byteScr = null;

		try
		{
			// key[]
			byteKey = key.getBytes(ConstantBase.ENCODING);
			secretKey = new SecretKeySpec(byteKey, AES.ALGORITHM);

			// cipher
			cipher = Cipher.getInstance(AES.ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);

			// decrypt
			byteScr = Base64.decode(rawData.getBytes(ConstantBase.ENCODING));
			byteData = cipher.doFinal(byteScr);
		}
		catch (Exception ex)
		{
			log.error("AES decrypting failed", ex);
		}

		// to string
		if (byteData != null)
			return new String(byteData);

		return null;
	}

}