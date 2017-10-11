package cn.tongyuankeji.common.parameters;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * SiteLog.DomainTable
 * 
 * @author 代平 2017-05-17 创建
 */
public enum EnumLogDomainTable
{
	invalid((byte) 0), // 不存在的性质
	
	app_setting((byte) 1),
	dict((byte) 2),
	gadget_pool((byte) 3),
	specialty((byte) 4),
	prof_rank((byte) 5),
	video_metadata((byte) 6),
	role((byte) 7),
	organization((byte) 8),
	back_user((byte) 9),
	cert((byte)10),	
	catalog((byte) 11),
	survey_item((byte) 12),
	backuser_user((byte) 13), // 后台用户操作前台用户
	governing((byte) 14),
	
	feedback((byte)41),
	
	user((byte) 101);
	
	private byte value;
	
	private EnumLogDomainTable(byte value)
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
	
	public static EnumLogDomainTable valueOf(byte byteValue)
	{
		for (int i = 1; i < EnumLogDomainTable.values().length; i++) // 跳过EnumLogDomainType.invalid
		{
			if (byteValue == EnumLogDomainTable.values()[i].byteValue())
				return EnumLogDomainTable.values()[i];
		}
		
		return EnumLogDomainTable.invalid;
	}
	
	
	public static String titleOfUserData(byte byteValue)
	{
		JSONObject o = null;
		Iterator itr = userArray.iterator();
		while (itr.hasNext())
		{
			o = (JSONObject) itr.next();
			if (o.getInt("name") == byteValue) // "name"的值刚好和byteValue相同的
				return o.getString("value");
		}
		
		return userArray.getString(0);
	}
	
	// 用户用
	public static final JSONArray userArray = JSONArray.fromObject("[" +
			"{\"name\":101, \"value\":\"用户\"}]");
}
