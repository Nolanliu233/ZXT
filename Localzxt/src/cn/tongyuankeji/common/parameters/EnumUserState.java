package cn.tongyuankeji.common.parameters;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author 代平 2017-05-02 创建
 */

public enum EnumUserState {
	deleted((byte) 0), // 删除
	closed((byte) 1), // 关闭
	registered((byte) 2), // 注册
	frozen((byte) 3), // 冻结
	active((byte) 4); // 激活、正常

	private byte value;

	private EnumUserState(byte value)
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

	public static EnumUserState valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumUserState.values().length; i++)
		{
			if (byteValue == EnumUserState.values()[i].byteValue())
				return EnumUserState.values()[i];
		}

		return EnumUserState.closed;
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
			"{\"name\":1, \"value\":\"关闭\"}," +
			"{\"name\":2, \"value\":\"注册\"}," +
			"{\"name\":3, \"value\":\"冻结\"}," +
			"{\"name\":4, \"value\":\"发布\"}]");

	// 后台 用户管理的查询使用
	public static final JSONArray displayArray = JSONArray.fromObject("[" +
			"{\"name\":1, \"value\":\"关闭\"}," +
			"{\"name\":2, \"value\":\"注册\"}," +
			"{\"name\":3, \"value\":\"冻结\"}," +
			"{\"name\":4, \"value\":\"发布\"}]");
}
