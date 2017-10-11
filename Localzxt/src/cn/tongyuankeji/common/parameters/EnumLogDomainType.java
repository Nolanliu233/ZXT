package cn.tongyuankeji.common.parameters;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * SiteLog.DomainType
 * 
 * @author 代平 2015-05-15
 */
public enum EnumLogDomainType
{
	invalid((byte) 0), // 不存在的性质
	
	app_setting((byte) 1),
	dict((byte) 2),
	gadget_pool((byte) 3),
	specialty((byte) 4),
	prof_rank((byte) 5),
	role((byte) 7),
	goverment((byte) 8),
	back_user((byte) 9),
	cert((byte)10),
	catalog((byte) 11),		
	survey_item((byte) 12),
	backuser_user((byte) 13), // 后台用户操作前台用
	
	feedback((byte)41),
	
	user((byte) 101); // 用户自身动作
	
	private byte value;
	
	private EnumLogDomainType(byte value)
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
	
	public static EnumLogDomainType valueOf(byte byteValue)
	{
		for (int i = 1; i < EnumLogDomainType.values().length; i++) // 跳过EnumLogDomainType.invalid
		{
			if (byteValue == EnumLogDomainType.values()[i].byteValue())
				return EnumLogDomainType.values()[i];
		}
		
		return EnumLogDomainType.invalid;
	}
	
	public static String titleOfBackData(byte byteValue)
	{
		JSONObject o = null;
		Iterator itr = backArray.iterator();
		while (itr.hasNext())
		{
			o = (JSONObject) itr.next();
			if (o.getInt("name") == byteValue) // "name"的值刚好和byteValue相同的
				return o.getString("value");
		}
		
		return backArray.getString(0);
	}
	
	// 后台用
	public static final JSONArray backArray = JSONArray.fromObject("[" +
			"{\"name\":1, \"value\":\"系统配置\"}," +
			"{\"name\":2, \"value\":\"常用选项\"}," +
			"{\"name\":4, \"value\":\"行业\"}," +
			"{\"name\":5, \"value\":\"职称\"}," +
			"{\"name\":7, \"value\":\"角色\"}," +
			"{\"name\":8, \"value\":\"园区\"}," +
			"{\"name\":9, \"value\":\"后台用户\"}," +
			"{\"name\":13, \"value\":\"用户\"}]");
}
