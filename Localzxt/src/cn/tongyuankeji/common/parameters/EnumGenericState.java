package cn.tongyuankeji.common.parameters;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author 代平 2017-05-02 创建
 */

public enum EnumGenericState {
	deleted((byte) 0),//删除
	hidden((byte) 1),//隐藏
	active((byte) 2);//激活

	private byte value;

	private EnumGenericState(byte value)
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

	public static EnumGenericState valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumGenericState.values().length; i++)
		{
			if (byteValue == EnumGenericState.values()[i].byteValue())
				return EnumGenericState.values()[i];
		}

		return EnumGenericState.hidden;
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
			"{\"name\":0, \"value\":\"删除\"}," +
			"{\"name\":1, \"value\":\"隐藏\"}," +
			"{\"name\":2, \"value\":\"正常\"}]");

	public static final JSONArray displayArray = JSONArray.fromObject("[" +
			"{\"name\":1, \"value\":\"隐藏\"}," +
			"{\"name\":2, \"value\":\"正常\"}]");
}
