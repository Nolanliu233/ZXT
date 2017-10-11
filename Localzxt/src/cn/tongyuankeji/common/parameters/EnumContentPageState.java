package cn.tongyuankeji.common.parameters;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public enum EnumContentPageState {
	deleted((byte) 0), // 删除
	closed((byte) 1), // 关闭
	no_segment((byte) 2), // 未分词
	err_segment((byte) 3), // 分词错误
	wait_audit((byte) 4), // 草稿
	audit_back((byte) 5), // 分词错误
	active((byte) 6); // 正常，已分词

	private byte value;

	private EnumContentPageState(byte value)
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

	public static EnumContentPageState valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumContentPageState.values().length; i++)
		{
			if (byteValue == EnumContentPageState.values()[i].byteValue())
				return EnumContentPageState.values()[i];
		}

		return EnumContentPageState.closed;
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
			"{\"name\":2, \"value\":\"未分词\"}," +
			"{\"name\":3, \"value\":\"分词错误\"}," +
			"{\"name\":4, \"value\":\"未审核\"}," +
			"{\"name\":5, \"value\":\"审核驳回\"}," +
			"{\"name\":6, \"value\":\"发布\"}]");

	// 后台 用户管理的查询使用
	public static final JSONArray displayArray = JSONArray.fromObject("[" +
			"{\"name\":1, \"value\":\"关闭\"}," +
			"{\"name\":2, \"value\":\"未分词\"}," +
			"{\"name\":3, \"value\":\"分词错误\"}," +
			"{\"name\":4, \"value\":\"未审核\"}," +
			"{\"name\":5, \"value\":\"审核驳回\"}," +
			"{\"name\":6, \"value\":\"发布\"}]");
}
