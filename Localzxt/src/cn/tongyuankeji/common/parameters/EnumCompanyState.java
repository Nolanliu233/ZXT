package cn.tongyuankeji.common.parameters;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public enum EnumCompanyState {
	deleted((byte) 0), // 删除
	closed((byte) 1), // 关闭
	draft((byte) 2), // 草稿
	ToAudit((byte) 3),//待审核
	frozen((byte) 4), // 冻结
	active((byte) 5); // 已激活


	private byte value;

	private EnumCompanyState(byte value)
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

	public static EnumCompanyState valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumCompanyState.values().length; i++)
		{
			if (byteValue == EnumCompanyState.values()[i].byteValue())
				return EnumCompanyState.values()[i];
		}

		return EnumCompanyState.closed;
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
			"{\"name\":2, \"value\":\"草稿\"}," +
			"{\"name\":3, \"value\":\"待审核\"}," +
			"{\"name\":4, \"value\":\"冻结\"}," +
			"{\"name\":5, \"value\":\"以激活\"}]");

	// 后台 用户管理的查询使用
	public static final JSONArray displayArray = JSONArray.fromObject("[" +
			"{\"name\":1, \"value\":\"关闭\"}," +
			"{\"name\":2, \"value\":\"草稿\"}," +
			"{\"name\":3, \"value\":\"待审核\"}," +
			"{\"name\":4, \"value\":\"冻结\"}," +
			"{\"name\":5, \"value\":\"以激活\"}]");
}
