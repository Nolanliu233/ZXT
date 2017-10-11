package cn.tongyuankeji.common.encoder;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

import cn.tongyuankeji.common.util.Utils;

public class simpleEncoder
{
	public final static String PARAM_ENCODE_KEY = "20766caf0fd446c7b0b13f7fe985a8e8";
	
	private static simpleEncoder simpleencoder = null;

	public static simpleEncoder getSimpleEncoder()
	{
		if (null == simpleencoder)
		{
			simpleencoder = new simpleEncoder();
		}
		return simpleencoder;
	}

	public static String StringEncode(String privatecode, String content)
	{
		if (Utils.isBlank(privatecode) || Utils.isBlank(content))
			return null;
		byte[] bprivatecode = null;
		byte[] bcontent = null;
		String tempContent = null;
		try
		{
			bprivatecode = privatecode.getBytes("UTF-8");
			bcontent = content.getBytes("UTF-8");
			Encode(bprivatecode, bcontent);

			//base64
			bcontent = Base64.encodeBase64(bcontent);

			//			 tempContent =  new String(bcontent,"ASCII");
			tempContent = new String(bcontent, "UTF-8");

		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		return tempContent;
	}

	public static String StringDecode(String privatecode, String content)
	{
		if (Utils.isBlank(privatecode) || Utils.isBlank(content))
			return null;
		byte[] bprivatecode = null;
		byte[] bcontent = null;
		String tempContent = null;
		try
		{
			bprivatecode = privatecode.getBytes("UTF-8");
			bcontent = content.getBytes("UTF-8");

			//base64
			bcontent = Base64.decodeBase64(bcontent);
			Encode(bprivatecode, bcontent);

			tempContent = new String(bcontent, "UTF-8");

		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		return tempContent;
	}

	/*
	 * 简单的WEB传输数据加密方法，调用一次加密，再调用一次就解密
	 * 参数： byte[] privatecode： 你自己的密钥
	 * 参数： byte[] content：需要加密的内容
	 * 没有返回类容
	 */

	public static void Encode(byte[] privatecode, byte[] content)
	{
		for (int i = 0; i < content.length; i++)
		{
			content[i] = (byte) (content[i] ^ privatecode[i % privatecode.length]);
		}
	}
}
