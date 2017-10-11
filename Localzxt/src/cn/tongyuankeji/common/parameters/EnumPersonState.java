package cn.tongyuankeji.common.parameters;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Organization.EStatus、SiteUser.EStatus、Student.EStatus
 * 
 * @author 代平 2017-05-02 创建
 */

public enum EnumPersonState
{
	deleted((byte) 0), // 删除
	closed((byte) 1), // 关闭
	registered((byte) 2), // 注册
	frozen((byte) 3), // 冻结
	active((byte) 4); // 已激活
	
	private byte value;
	
	private EnumPersonState(byte value)
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
	
	public static EnumPersonState valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumPersonState.values().length; i++)
		{
			if (byteValue == EnumPersonState.values()[i].byteValue())
				return EnumPersonState.values()[i];
		}
		
		return EnumPersonState.closed;
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
	
	public static EnumPersonState higherOf(EnumPersonState sourceStatus)
	{
		for (int i = 0; i < EnumPersonState.values().length - 1; i++)
		{
			if (sourceStatus.byteObject().equals(EnumPersonState.values()[i].byteValue()))
				return EnumPersonState.values()[i + 1];
		}
		
		return EnumPersonState.active;
	}
		
	public static final JSONArray dataArray = JSONArray.fromObject("[" +
			"{\"name\":0, \"value\":\"删除\"}," +
			"{\"name\":1, \"value\":\"关闭\"}," +
			"{\"name\":2, \"value\":\"注册\"}," +
			"{\"name\":3, \"value\":\"冻结\"}," +
			"{\"name\":4, \"value\":\"已激活\"}]");
	
	public static final JSONArray displayArray = JSONArray.fromObject("[" +
			"{\"name\":1, \"value\":\"关闭\"}," +
			"{\"name\":2, \"value\":\"注册\"}," +
			"{\"name\":3, \"value\":\"冻结\"}," +
			"{\"name\":4, \"value\":\"已激活\"}]");
}
