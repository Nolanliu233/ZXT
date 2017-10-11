package cn.tongyuankeji.common.parameters;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author 代平 2017-05-02 创建
 */

public enum EnumBaseUrlState {
	deleted((byte) -1),
	active((byte) 0),
	add((byte) 1),
	edit((byte) 2);

	private byte value;

	private EnumBaseUrlState(byte value)
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

	public static EnumBaseUrlState valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumBaseUrlState.values().length; i++)
		{
			if (byteValue == EnumBaseUrlState.values()[i].byteValue())
				return EnumBaseUrlState.values()[i];
		}

		return EnumBaseUrlState.active;
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
			"{\"name\":-1, \"value\":\"删除\"}," +
			"{\"name\":0, \"value\":\"正常\"}," +
			"{\"name\":1, \"value\":\"新增\"}," +
			"{\"name\":2, \"value\":\"编辑\"}]");

	public static final JSONArray displayArray = JSONArray.fromObject("[" +
			"{\"name\":0, \"value\":\"正常\"}," +
			"{\"name\":1, \"value\":\"新增\"}," +
			"{\"name\":2, \"value\":\"编辑\"}]");
}
