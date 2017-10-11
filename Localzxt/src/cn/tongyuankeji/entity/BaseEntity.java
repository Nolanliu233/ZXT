package cn.tongyuankeji.entity;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.common.util.DateUtils;
import cn.tongyuankeji.common.util.Utils;

/**
 * 可转前端JSON包的Entity实例基类。如果需要使用基类实现的Json和Entity互转方法的，派生类属性应用OEEntityDesc描述getXXX()
 * 可支持自动日志的实例基类
 * 
 * @author 代平 2017-05-02 创建
 */
public abstract class BaseEntity
{
	protected BaseEntity()
	{
	}

	/**
	 * 将Entity对象转JSONObject。只对getXXX()方法有效
	 * 
	 * @param keepAll
	 *            用于排除对象上@OEEntityDesc.always = false的属性。<br />
	 *            一般编辑时，传入true，会包含Entity上所有使用了@OEEntityDesc()的getXXX属性。否则，只包含always = true的属性
	 * 
	 * @param showZh
	 *            是否启用@OEEntityDesc.makeString设置。<br />
	 *            启用时，属性值会被转成String。如果是enum类型的，会使用entityDescGet()方法取显示的中文
	 * 
	 * @return
	 *         由entry实例转成的JSON包
	 */
	public JSONObject toJson(boolean keepAll, boolean showZh) throws Exception
	{
		return BaseEntity.toJsonImpl(this, null, keepAll, showZh);
	}

	/**
	 * 将Entity对象转JSONObject，可在entityex数组中传入1～N个扩展表对象。只对getXXX()方法有效
	 * 
	 * @param keepAll
	 *            用于排除对象上@OEEntityDesc.always = false的属性。<br />
	 *            一般编辑时，传入true，会包含Entity上所有使用了@OEEntityDesc()的getXXX属性。否则，只包含always = true的属性
	 * 
	 * @param showZh
	 *            是否启用@OEEntityDesc.makeString设置。<br />
	 *            启用时，属性值会被转成String。如果是enum类型的，会使用entityDescGet()方法取显示的中文
	 * @param entityex
	 *            1～N个扩展表对象。如，entity是Student时，可传入StudentEx和StudentStat。又如entity是Course时，可传入CourseStat
	 * @return
	 *         由entry实例转成的JSON包
	 */
	public JSONObject toJsonComplex(boolean keepAll, boolean showZh, BaseEntity... entityex) throws Exception
	{
		assert entityex != null && entityex.length > 0 : "toJsonComplex(boolean, boolean, BaseEntity...)参数entityex不能为空，且长度大于0。否则，请使用toJson(boolean, boolean)！";

		JSONObject oentry = BaseEntity.toJsonImpl(this, null, keepAll, showZh);
		for (int i = 0; i < entityex.length; i++)
			BaseEntity.toJsonImpl(entityex[i], oentry, keepAll, showZh);

		return oentry;
	}

	protected static JSONObject toJsonImpl(BaseEntity entity, JSONObject oentry, boolean keepAll, boolean showZh) throws Exception
	{
		EntityDesc ann = null;
		Method[] ms = entity.getClass().getMethods();
		Method menum = null;
		Class returnType = null;
		String fieldName = null;

		if (oentry == null)
			oentry = new JSONObject();

		for (int i = 0; i < ms.length; i++)
		{
			fieldName = ms[i].getName();
			if (!fieldName.startsWith("get"))
				continue;

			//assert (Modifier.isPublic(ms[i].getModifiers()));

			fieldName = BaseEntity.getterMethodName2FieldName(fieldName);

			returnType = ms[i].getReturnType();

			if ((ann = ms[i].getAnnotation(EntityDesc.class)) != null && (ann.always() || !ann.always() == keepAll))
			{
				assert !returnType.isPrimitive() : "BaseEntity派生类的getter/setter属性不能使用primitive type做返回类型";
				
				if (showZh && ann.makeString())
				{
					Object tmp = ms[i].invoke(entity);
					if (tmp != null)
					{
						if (ann.enumType() != Object.class) // 只有指明了具体Enum类型的才能转
						{
							menum = ann.enumType().getMethod("titleOf", byte.class);
							oentry.put(fieldName, menum.invoke(null, tmp));
						}
						else
							oentry.put(fieldName, tmp.toString());
					}
					else
						oentry.put(fieldName, "");
				}
				else
				{
					if (returnType == java.lang.Byte.class
							|| returnType == java.lang.Integer.class
							|| returnType == java.lang.Long.class
							|| returnType == java.lang.Short.class
							|| returnType == java.lang.Double.class
							|| returnType == java.lang.Float.class)
					{
						Object tmp = ms[i].invoke(entity);
						if (tmp != null)
							oentry.put(fieldName, Double.valueOf(tmp.toString()));
						else
							oentry.put(fieldName, JSONNull.getInstance());
					}

					else if (returnType == java.lang.Boolean.class)
						oentry.put(fieldName, ((Boolean) ms[i].invoke(entity)));

					else if (returnType == Timestamp.class)
					{
						Timestamp tmp = (Timestamp) ms[i].invoke(entity);
						if (tmp != null)
						{
							if (ann.shortDate())
								oentry.put(fieldName, DateUtils.timestamp2DateString(tmp));
							else
								oentry.put(fieldName, DateUtils.timestamp2String(tmp));
						}
						else
							oentry.put(fieldName, JSONNull.getInstance());
					}
					else if (returnType == java.sql.Date.class)
					{
						java.sql.Date tmp = (java.sql.Date) ms[i].invoke(entity);
						if (tmp != null)
							oentry.put(fieldName, DateUtils.date2String(tmp));
						else
							oentry.put(fieldName, JSONNull.getInstance());
					}
					else if (returnType == java.util.Date.class)
					{
						java.util.Date tmp = (java.util.Date) ms[i].invoke(entity);
						if (tmp != null)
							oentry.put(fieldName, DateUtils.date2String(tmp));
						else
							oentry.put(fieldName, JSONNull.getInstance());
					}
					else
					{
						String tmp = (String) ms[i].invoke(entity);
						if (!Utils.isEmpty(tmp))
							oentry.put(fieldName, tmp); // java.lang.String.class
						else
						{
							if (ann.emptyStringUseNull())
								oentry.put(fieldName, JSONNull.getInstance());
							else
								oentry.put(fieldName, "");
						}
					}
				}
			}
		}

		return oentry;
	}

	/**
	 * 将getXXX()转出field名
	 */
	public static String getterMethodName2FieldName(String methodName)
	{
		assert !Utils.isBlank(methodName) : "getterMethodName2FieldName(String)但是methodName不能为空！";

		methodName = methodName.substring(3); /* remove leading "get" */
		return methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
	}

	/**
	 * 将链转JSON，lst中必须是BaseEntity派生类
	 * 
	 * @param lst
	 *            BaseEntity派生类，此链中允许一个实例都不包含
	 * @param keepAll
	 *            见BaseEntity.toJson()
	 * @param showZh
	 *            见BaseEntity.toJson()
	 */
	public final static JSONArray list2JsonArray(List lst, boolean keepAll, boolean showZh) throws Exception
	{
		assert lst != null : "entityList2JsonArray(List, boolean, boolean) 参数lst不能为空";
		if (!lst.isEmpty())
			assert (lst.get(0) instanceof BaseEntity) : "entityList2JsonArray(List, boolean, boolean)的参数lst必须是BaseEntity派生类！";

		JSONArray aentry = new JSONArray();

		for (Object loop : lst)
			aentry.add(((BaseEntity) loop).toJson(keepAll, showZh));

		return aentry;
	}
}
