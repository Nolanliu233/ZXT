
package cn.tongyuankeji.common.encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.common.encoder.Base64Utils;

/**
 * AES加密工具类
 * 
 * @author 代平 2017-05-02 创建
 */
public class AES extends Encrypt
{
	static final String ALGORITHM = "AES";
	SecretKeySpec secretKey;

	public AES()
	{
		setKey(ConstantBase.AES_Key);//secret key
	}

	public AES(String str)
	{
		setKey(str);//generate secret key
	}

	public SecretKey getSecretKey()
	{
		return secretKey;
	}

	/**
	 * generate KEY
	 */
	public void setKey(String strKey)
	{
		try
		{
			byte[] bk = Md5PwdEncoder.md5Raw(strKey.getBytes(ConstantBase.ENCODING));
			this.secretKey = new SecretKeySpec(bk, ALGORITHM);
		}
		catch (Exception e)
		{}
	}

	/**
	 * @Description AES encrypt
	 * @param str
	 * @return
	 */
	public String encryptAES(String str)
	{
		byte[] encryptBytes = null;
		String encryptStr = null;
		try
		{
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
			encryptBytes = cipher.doFinal(str.getBytes());
			if (encryptBytes != null)
			{
				encryptStr = Base64Utils.encryptBASE64(encryptBytes);
			}
		}
		catch (Exception e)
		{}
		return encryptStr;
	}

	/**
	 * @Description AES decrypt
	 * @param str
	 * @return
	 */
	public String decryptAES(String str)
	{
		byte[] decryptBytes = null;
		String decryptStr = null;
		try
		{
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
			byte[] scrBytes = Base64Utils.decryptBASE64(str);
			decryptBytes = cipher.doFinal(scrBytes);
		}
		catch (Exception e)
		{}
		if (decryptBytes != null)
		{
			decryptStr = new String(decryptBytes);
		}
		return decryptStr;
	}

	/**
	 * AES encrypt
	 */
	@Override
	public String encrypt(String value, String key) throws Exception
	{
		return this.encryptAES(value);
	}

	/**
	 * AES decrypt
	 */
	@Override
	public String decrypt(String value, String key) throws Exception
	{
		return this.decryptAES(value);

	}
}