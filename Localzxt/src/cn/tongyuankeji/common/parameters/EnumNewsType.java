package cn.tongyuankeji.common.parameters;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public enum EnumNewsType {

	companyNews((byte)0),//公司新闻
	trideNews((byte)1),//行业新闻
	mediaReport((byte)2);//媒体报道
	
	private byte value;
	private EnumNewsType(byte value){
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

	public static EnumNewsType valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumNewsType.values().length; i++)
		{
			if (byteValue == EnumNewsType.values()[i].byteValue())
				return EnumNewsType.values()[i];
		}

		return EnumNewsType.companyNews;
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
			"{\"name\":0, \"value\":\"公司新闻\"}," +
			"{\"name\":1, \"value\":\"行业新闻\"}," +
			"{\"name\":2, \"value\":\"媒体报道\"}]");
}
