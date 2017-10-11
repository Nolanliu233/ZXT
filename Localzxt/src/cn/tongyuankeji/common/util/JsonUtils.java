package cn.tongyuankeji.common.util;


import cn.tongyuankeji.dao.ReadonlyTable;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

/**
 * JSON 工具类
 * 
 * @author 代平 2017-05-02 创建
 */
public final class JsonUtils
{
	/*------------------------------- 通用帮助方法 -------------------------------*/

	/**
	 * 将source[key]的值，全换成"*"
	 * 
	 * @param source
	 *            被检查的JSON节点对象
	 * 
	 * @param key
	 *            检查JSON节点对象中哪个属性
	 * @param skip
	 *            跳过无需转换的字符。没有需要跳过的字符时，传入null
	 */
	public final static void hide(JSONObject source, String key, Character[] skip)
	{
		assert source != null : "hide(JSONObject, String, Character[]) 参数source不能为空";
		assert !Utils.isBlank(key) : "hide(JSONObject, String, Character[]) 参数key不能为空";

		boolean isSkip = false;

		if (!source.has(key))
			return;
		String value = source.getString(key);
		if (!Utils.isBlank(value))
		{
			char[] arr = new char[value.length()];
			for (int i = 0; i < arr.length; i++)
			{
				isSkip = false;

				if (skip != null)
				{
					for (int j = 0; j < skip.length; j++)
					{
						if (skip[j].equals(value.charAt(i)))
						{
							isSkip = true;
							break;
						}
					}
				}

				arr[i] = isSkip ? value.charAt(i) : '*';
			}

			value = new String(arr);

			source.put(key, value);
		}
	}

	/**
	 * value是null或空字符串时，填充为JSONNull。否则，填充value值
	 * 
	 * @param target
	 *            放入到哪个JSON节点对象
	 * 
	 * @param key
	 *            节点Key值
	 * 
	 * @param value
	 *            节点Value值。传入null时，填为JSONNull。空字符串的，填为JSONNull。
	 * 
	 * @param time2Date
	 *            如果value是java.sql.Timestamp类型，是否转成日期？不是Timestamp对象的，传入null。是Timestamp对象的，但是time2Date为空时，转为时间字符串
	 */
	public final static <T> void put(JSONObject target, String key, T value, Boolean time2Date)
	{
		assert target != null : "put(JSONObject, String, Object, Boolean) 参数target不能为空";
		assert !Utils.isBlank(key) : "put(JSONObject, String, Object, Boolean) 参数key不能为空";

		if (value == null)
		{
			target.put(key, JSONNull.getInstance());
			return;
		}

		if (value instanceof String && Utils.isBlank((String) value))
		{
			target.put(key, JSONNull.getInstance());
			return;
		}

		if (value.getClass().isAssignableFrom(java.sql.Timestamp.class))
		{
			if (time2Date != null && time2Date)
				target.put(key, DateUtils.timestamp2DateString((java.sql.Timestamp) value));
			else
				target.put(key, DateUtils.timestamp2String((java.sql.Timestamp) value));
		}

		else if (value.getClass().isAssignableFrom(java.util.Date.class))
			target.put(key, DateUtils.date2String((java.util.Date) value));

		else if (value.getClass().isAssignableFrom(java.sql.Date.class))
			target.put(key, DateUtils.date2String((java.sql.Date) value));

		else
			target.put(key, value);
	}

	/**
	 * value是null或空字符串时，填充为JSONNull。否则，填充value值
	 * 
	 * @param target
	 *            放入到哪个JSON节点对象
	 * 
	 * @param key
	 *            节点Key值
	 * 
	 * @param value
	 *            节点Value值。传入null时，填为JSONNull。空字符串的，填为JSONNull。
	 */
	public final static <T> void put(JSONObject target, String key, T value)
	{
		JsonUtils.put(target, key, value, null);
	}

	/**
	 * 创建一个JSONObject, 键 - key，值 - String.format(format, args)
	 */
	public final static JSONObject createSimple(String key, String format, Object... args)
	{
		JSONObject result = new JSONObject();
		if (Utils.isBlank(format))
			result.put(key, JSONNull.getInstance());
		else
		{
			if (args == null || args.length == 0)
				result.put(key, format);
			else
				result.put(key, String.format(format, args));
		}

		return result;
	}

	public final static JSONObject createSimple(String key, JSONArray aentry)
	{
		assert aentry != null : "createSimple(String, JSONArray)参数aentry不能为空！";

		JSONObject result = new JSONObject();
		result.put(key, aentry);
		return result;
	}

	/**
	 * 创建一个JSONObject, 键 - key，值 - String.format(format, args)，并添加到aentry中
	 */
	public final static void addSimple(JSONArray aentry, String key, String format, Object... args)
	{
		JSONObject oentry = JsonUtils.createSimple(key, format, args);
		aentry.add(oentry);
	}

	/**
	 * 计算aentry的长度
	 */
	public final static int lengthOf(JSONArray aentry)
	{
		int size = 0;
		if (aentry != null && !aentry.isEmpty())
		{
			for (@SuppressWarnings("unused") Object oentry: aentry)
				size++;
		}

		return size;
	}

	/**
	 * 对只返回一行记录的汇总表将dt中，读取到一个json对象中，key: dt.column.name，value: dt.[row_0, column_i].value。
	 */
	public final static JSONObject readTable(ReadonlyTable dt)
	{
		JSONObject oresult = new JSONObject();
		String[] arrColName = dt.getColumnNames();
		Object val = null;
		for(int i = 0; i < arrColName.length; i++)
		{
			val = dt.get(0, arrColName[i]);
			if (val == null)
			{
				oresult.put(arrColName[i], JSONNull.getInstance());
			}
			else
			{
				if (val instanceof String && Utils.isBlank((String)val))
				{
					oresult.put(arrColName[i], JSONNull.getInstance());
				}
				else
					oresult.put(arrColName[i], val);
			}
		}
		
		return oresult;
	}
}