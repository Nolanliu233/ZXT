package cn.tongyuankeji.common.parameters;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author 代平 2017-08-24 创建
 * 正文附件内容来源
 * 1、爬虫抓取
 * 2、后来人员修改为其他链接
 * 3、后台上传的文件
 */
public enum EnumAttachCreateType {
	crawler((byte) 0),
	link((byte) 1),
	upload((byte) 2);

	private byte value;

	private EnumAttachCreateType(byte value)
	{
		this.value = value;
	}

	public byte byteValue()
	{
		return value;
	}

	public Byte byteObject()
	{
		return new Byte(value);
	}

	public static EnumAttachCreateType valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumAttachCreateType.values().length; i++)
		{
			if (byteValue == EnumAttachCreateType.values()[i].byteValue())
				return EnumAttachCreateType.values()[i];
		}

		return EnumAttachCreateType.crawler;
	}

	public static String titleOf(byte byteValue)
	{
		JSONObject o = null;
		Iterator itr = displayArray.iterator();
		while (itr.hasNext())
		{
			o = (JSONObject) itr.next();
			if (o.getInt("name") == byteValue) // "name"的值刚好和byteValue相同的
				return o.getString("value");
		}

		return displayArray.getString(0);
	}


	public static final JSONArray displayArray = JSONArray.fromObject("[" +
			"{\"name\":0, \"value\":\"爬虫抓取\"}," +
			"{\"name\":1, \"value\":\"链接\"}," +
			"{\"name\":2, \"value\":\"上传\"}]");
}
