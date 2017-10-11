package cn.tongyuankeji.common.parameters;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public enum EnumClientType {
	unkown((byte) 0), // 未知
	weixin((byte) 1), // 微信
	browser((byte) 2), // 浏览器
	app((byte) 3); // App

	private byte value;

	private EnumClientType(byte value)
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

	public static EnumClientType valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumClientType.values().length; i++)
		{
			if (byteValue == EnumClientType.values()[i].byteValue())
				return EnumClientType.values()[i];
		}

		return EnumClientType.unkown;
	}

	public static String titleOf(byte byteValue)
	{
		JSONObject o = null;
		Iterator itr = dataArray.iterator();
		while (itr.hasNext())
		{
			o = (JSONObject) itr.next();
			if (o.getInt("name") == byteValue) // "name"的值刚好和byteValue相同的
				return o.getString("value");
		}

		return dataArray.getString(0);
	}

	public static final JSONArray dataArray = JSONArray.fromObject("[" +
			"{\"name\":0, \"value\":\"未知\"}," +
			"{\"name\":1, \"value\":\"微信\"}," +
			"{\"name\":2, \"value\":\"浏览器\"}," +
			"{\"name\":3, \"value\":\"App\"}]");
}
