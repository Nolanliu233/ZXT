package cn.tongyuankeji.common.parameters;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public enum EnumUserCreatedType {
	regist((byte) 0), //自主注册
	company((byte) 1), //企业分配
	backUser((byte) 2), // 后台用户创建
	system((byte) 3), // 系统分配
	weichat((byte) 4), //微信绑定
	qq((byte) 5), //QQ绑定
	weibo((byte) 6); //微博绑定



	private byte value;

	private EnumUserCreatedType(byte value)
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

	public static EnumUserCreatedType valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumUserCreatedType.values().length; i++)
		{
			if (byteValue == EnumUserCreatedType.values()[i].byteValue())
				return EnumUserCreatedType.values()[i];
		}

		return EnumUserCreatedType.regist;
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
			"{\"name\":0, \"value\":\"自主注册\"}," +
			"{\"name\":1, \"value\":\"企业分配\"}," +
			"{\"name\":2, \"value\":\"后台创建\"}," +
			"{\"name\":3, \"value\":\"系统分配\"}," +
			"{\"name\":4, \"value\":\"微信绑定\"}," +
			"{\"name\":5, \"value\":\"QQ绑定\"}," +
			"{\"name\":6, \"value\":\"微博绑定\"}]");

	// 后台 用户管理的查询使用
	public static final JSONArray displayArray = JSONArray.fromObject("[" +
			"{\"name\":0, \"value\":\"自主注册\"}," +
			"{\"name\":1, \"value\":\"企业分配\"}," +
			"{\"name\":2, \"value\":\"后台创建\"}," +
			"{\"name\":3, \"value\":\"系统分配\"}," +
			"{\"name\":4, \"value\":\"微信绑定\"}," +
			"{\"name\":5, \"value\":\"QQ绑定\"}," +
			"{\"name\":6, \"value\":\"微博绑定\"}]");
}
