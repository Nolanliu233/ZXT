package cn.tongyuankeji.common.parameters;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author 代平 2017-05-02 创建
 */

public enum EnumGenderKind {
	unknown((byte) 0),
	male((byte) 1),
	famale((byte) 2);

	private byte value;

	private EnumGenderKind(byte value)
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

	public static EnumGenderKind valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumGenderKind.values().length; i++)
		{
			if (byteValue == EnumGenderKind.values()[i].byteValue())
				return EnumGenderKind.values()[i];
		}

		return EnumGenderKind.unknown;
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
			"{\"name\":0, \"value\":\"未知\"}," +
			"{\"name\":1, \"value\":\"男\"}," +
			"{\"name\":2, \"value\":\"女\"}]");
}
