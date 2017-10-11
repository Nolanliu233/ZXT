package cn.tongyuankeji.common.parameters;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public enum EnumNewsOrderBy {
	alwaysTop((byte)0),//首页置顶显示
	show((byte)1),//首页显示
	hidden((byte)2);//首页不显示
	
	private byte value;
	private EnumNewsOrderBy(byte value){
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

	public static EnumNewsOrderBy valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumNewsOrderBy.values().length; i++)
		{
			if (byteValue == EnumNewsOrderBy.values()[i].byteValue())
				return EnumNewsOrderBy.values()[i];
		}

		return EnumNewsOrderBy.hidden;
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
			"{\"name\":0, \"value\":\"首页置顶\"}," +
			"{\"name\":1, \"value\":\"首页显示\"}," +
			"{\"name\":2, \"value\":\"首页不显示\"}]");
}
