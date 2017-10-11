package cn.tongyuankeji.common.parameters;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public enum EnumSolarSortField {
	cawlerTime((byte)0),//爬取时间
	typeId((byte)1),//消息口
	levelId((byte)2),//等级
	areaId((byte)3),//地区
	beginAt((byte)4),//发布时间
	endAt((byte)5);//截止时间
	private byte value;
	private EnumSolarSortField(byte value){
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

	public static EnumSolarSortField valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumSolarSortField.values().length; i++)
		{
			if (byteValue == EnumSolarSortField.values()[i].byteValue())
				return EnumSolarSortField.values()[i];
		}

		return EnumSolarSortField.cawlerTime;
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
			"{\"name\":\"cawlerTime\", \"value\":\"爬取时间\"}," +
			"{\"name\":\"typeId\", \"value\":\"消息口\"}," +
			"{\"name\":\"levelId\", \"value\":\"等级\"}," +
			"{\"name\":\"areaId\", \"value\":\"地区\"}," +
			"{\"name\":\"beginAt\", \"value\":\"发布时间\"}," +
			"{\"name\":\"endAt\", \"value\":\"截止时间\"}]");
}
