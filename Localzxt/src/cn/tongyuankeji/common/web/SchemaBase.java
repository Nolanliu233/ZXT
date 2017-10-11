package cn.tongyuankeji.common.web;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.tongyuankeji.common.annotation.ActionArg;
import cn.tongyuankeji.common.annotation.ActionArgs;
import cn.tongyuankeji.common.encoder.simpleEncoder;
import cn.tongyuankeji.common.exception.ACLException;
import cn.tongyuankeji.common.util.DateUtils;
import cn.tongyuankeji.common.util.EnumConverter;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;

/**
 * action方法检查访问权限 工具类
 * 
 * @author 代平 2017-05-10 创建
 */
public class SchemaBase
{
	/**
	 * Portal、用户个人中心子系统Action方法调用，解析request中传入参数到返回的map中 。 发生错误时，抛出异常
	 * 
	 * @param callingObj
	 *            调用此方法的实体对象
	 * 
	 * @return
	 *         用request.getParameter解析调用方法所需参数，<br />
	 *         [0] - 参数名(定义在ActionArg中)，[1] - 参数值(总是wrapper类型)。 ActionArg中没有定义的参数，不包含在这个map里面。
	 */
	public static Map<String, Object> parseArg(HttpServletRequest req, Object callingObj, EnumConverter cvr) throws Exception
	{
		assert req != null : "parseArg(HttpServletRequest, Object, EnumConverter) 参数req不能为空";
		assert callingObj != null : "parseArg(HttpServletRequest, Object, EnumConverter) 参数callingObj不能为空";
		assert cvr != null : "parseArg(HttpServletRequest, Object, EnumConverter) 参数cvr不能为空";

		/*
		 * TODO 1. 是否包含有害char
		 */

		// 找调用我的方法，一般还是很快，只循环3次：getStackTrace、validate、calling-method
		boolean foundCaller = false;
		String actionMethodName = null;
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stacktrace.length; i++)
		{
			if (foundCaller)
			{
				actionMethodName = stacktrace[i].getMethodName();
				break;
			}

			if (stacktrace[i].getMethodName() == "parseArg")
				foundCaller = true;
		}

		// 解析传入参数
		return parseArg(req, callingObj, actionMethodName, cvr, false);
	}

	/**
	 * 后台管理子系统Action方法调用，检查登录、用户状态、权限、园区，并解析request中传入参数到返回的map中 。 发生错误时，抛出异常
	 * 
	 * @param callingObj
	 *            调用此方法的实体对象
	 * 
	 * @return
	 *         用request.getParameter解析调用方法所需参数，<br />
	 *         [0] - 参数名(定义在ActionArg中)，[1] - 参数值(总是wrapper类型)。 ActionArg中没有定义的参数，不包含在这个map里面。
	 */
	public static Map<String, Object> validate(HttpServletRequest req, Object callingObj, EnumConverter cvr) throws Exception
	{
		assert req != null : "validate(HttpServletRequest, Object, EnumConverter) 参数req不能为空";
		assert callingObj != null : "validate(HttpServletRequest, Object, EnumConverter) 参数callingObj不能为空";
		assert cvr != null : "validate(HttpServletRequest, Object, EnumConverter) 参数cvr不能为空";

		String errMsg = null;

		/*
		 * TODO: 0908 是否包含有害char
		 */

		// 找调用我的方法，一般还是很快，只循环3次：getStackTrace、validate、calling-method
		boolean foundCaller = false;
		String actionMethodName = null;
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stacktrace.length; i++)
		{
			if (foundCaller)
			{
				actionMethodName = stacktrace[i].getMethodName();
				break;
			}

			if (stacktrace[i].getMethodName() == "validate")
				foundCaller = true;
		}

		// 登录否、用户状态、操作权限
		if (!Utils.isBlank(errMsg = ACL.checkACL((Person) req.getAttribute(Person.LOGIN_PERSON), callingObj.getClass(), actionMethodName, false)))
			throw new ACLException(errMsg);

		// 解析传入参数
		return parseArg(req, callingObj, actionMethodName, cvr, false);
	}

	/**
	 * 被支付平台调用时，使用此方法解析HttpRequest参数。 发生错误时，抛出异常
	 * 
	 * @param callingObj
	 *            调用此方法的实体对象
	 * 
	 * @return
	 *         用request.getParameter解析调用方法所需参数，<br />
	 *         [0] - 参数名(定义在ActionArg中)，[1] - 参数值(总是wrapper类型)。 ActionArg中没有定义的参数，不包含在这个map里面。
	 */
	public static Map<String, Object> parseCleanArg(HttpServletRequest req, Object callingObj, EnumConverter cvr) throws Exception
	{
		assert req != null : "parseCleanArg(HttpServletRequest, Object, EnumConverter) 参数req不能为空";
		assert callingObj != null : "parseCleanArg(HttpServletRequest, Object, EnumConverter) 参数callingObj不能为空";
		assert cvr != null : "parseCleanArg(HttpServletRequest, Object, EnumConverter) 参数cvr不能为空";

		/*
		 * todo 1. 是否包含有害char
		 */

		// 找调用我的方法，一般还是很快，只循环3次：getStackTrace、validate、calling-method
		boolean foundCaller = false;
		String actionMethodName = null;
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stacktrace.length; i++)
		{
			if (foundCaller)
			{
				actionMethodName = stacktrace[i].getMethodName();
				break;
			}

			if (stacktrace[i].getMethodName() == "parseCleanArg")
				foundCaller = true;
		}

		// 解析传入参数
		return parseArg(req, callingObj, actionMethodName, cvr, true);
	}

	/**
	 * 被APP调用时，检查登录、用户状态、权限，并解析request中传入参数到返回的map中。 发生错误时，抛出异常
	 * 
	 * @param callingObj
	 *            调用此方法的实体对象
	 * 
	 * @return
	 *         用request.getParameter解析调用方法所需参数，<br />
	 *         [0] - 参数名(定义在ActionArg中)，[1] - 参数值(总是wrapper类型)。 ActionArg中没有定义的参数，不包含在这个map里面。
	 */
	public static Map<String, Object> validateClean(HttpServletRequest req, Object callingObj, EnumConverter cvr) throws Exception
	{
		assert req != null : "validateClean(HttpServletRequest, Object, EnumConverter) 参数req不能为空";
		assert callingObj != null : "validateClean(HttpServletRequest, Object, EnumConverter) 参数callingObj不能为空";
		assert cvr != null : "validateClean(HttpServletRequest, Object, EnumConverter) 参数cvr不能为空";

		String errMsg = null;

		/*
		 * TODO: 0908 是否包含有害char
		 */

		// 找调用我的方法，一般还是很快，只循环3次：getStackTrace、validate、calling-method
		boolean foundCaller = false;
		String actionMethodName = null;
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stacktrace.length; i++)
		{
			if (foundCaller)
			{
				actionMethodName = stacktrace[i].getMethodName();
				break;
			}

			if (stacktrace[i].getMethodName() == "validateClean")
				foundCaller = true;
		}

		// 登录否、用户状态、操作权限
		if (!Utils.isBlank(errMsg = ACL.checkACL((Person) req.getAttribute(Person.LOGIN_PERSON), callingObj.getClass(), actionMethodName, false)))
			throw new ACLException(errMsg);

		// 解析传入参数
		return parseArg(req, callingObj, actionMethodName, cvr, true);
	}

	/*----------------------------------------- Helper  -----------------------------------------*/
	// 解析参数时，GET协议：isCutPrm = true，明文，false，混淆文；POST协议，总是明文
	static Map<String, Object> parseArg(HttpServletRequest req, Object callingObj, String actionMethodName, EnumConverter cvr, Boolean isCutPrm) throws Exception
	{
		Map<String, Object> args = new HashMap<String, Object>();
		ActionArg[] anns = null;
		Class ttype = null;
		boolean isConfusion = false;
		String parmValue = null;

		// 1. 总是先加trace
		args.put(Trace.LOG_ARG, new Trace(req, actionMethodName));

		// 是存get，且在URL中带参数的，参数值是混淆后的值
		if (!isCutPrm && req.getMethod().toUpperCase().equals("GET")
				&& (req.getContentType() == null || !req.getContentType().toUpperCase().equals("APPLICATION/JSON")))
			isConfusion = true;

		// 3. 用方法参数定义ActionArg[]取request.parameters
		assert callingObj.getClass().getMethod(actionMethodName, HttpServletRequest.class, HttpServletResponse.class) != null : "应使用标准Action方法定义，只传入参数：HttpServletRequest, HttpServletResponse";

		Method m = callingObj.getClass().getMethod(actionMethodName, HttpServletRequest.class, HttpServletResponse.class);
		if (!m.isAnnotationPresent(ActionArgs.class))
			return args;

		anns = m.getAnnotation(ActionArgs.class).value();
		assert anns.length > 0 : "你是不是忘了在方法上加@ActionArgs？";

		// 4. 循环转换
		for (ActionArg ann : anns)
		{
			ttype = (ann.target_type() == null) ? ann.type() : ann.target_type(); // 没有target-type时，使用type
			if (ttype.isArray())
				args.put(ann.name(), convertArray(ann, ttype, req.getParameterValues(ann.name()), cvr));
			else
			{
				parmValue = req.getParameter(ann.name());

				if (isConfusion && !Utils.isBlank(parmValue))
					parmValue = simpleEncoder.StringDecode(simpleEncoder.PARAM_ENCODE_KEY, parmValue);
				args.put(ann.name(), convert(ann, ttype, parmValue, cvr, isCutPrm));
			}
		} // foreach request parameter

		return args;
	}

	static Object convert(ActionArg ann, Class ttype, String value, EnumConverter cvr, Boolean isCutPrm) throws Exception
	{
		String tmp = null;

		// 1. 值是否必填？非必填的，且request.parameter没有值的，等下在转换的时候args中放入null
		if (Utils.isBlank(value) && ann.required())
			throw new RuntimeException(String.format("请输入%s！", Utils.isBlank(ann.header()) ? ann.name() : ann.header()));

		tmp = value;

		// 3. 转换
		if (ttype == java.lang.Boolean.class)
			return Utils.isBlank(tmp) ? null : Boolean.parseBoolean(tmp);

		else if (ttype == java.util.Date.class)
			return Utils.isBlank(tmp) ? null : makeDate(ann, tmp);

		else if (ttype == java.sql.Timestamp.class)
			return Utils.isBlank(tmp) ? null : makeTimestamp(ann, tmp);

		else if (ttype == java.lang.Integer.class)
			return Utils.isBlank(tmp) ? null : new Integer(makeNumber(ann, tmp).intValue());

		else if (ttype == java.lang.Long.class)
			return Utils.isBlank(tmp) ? null : makeNumber(ann, tmp);

		else if (ttype == java.lang.Short.class)
			return Utils.isBlank(tmp) ? null : new Short(makeNumber(ann, tmp).shortValue());

		else if (ttype == java.lang.Byte.class)
			return Utils.isBlank(tmp) ? null : new Byte(makeNumber(ann, tmp).byteValue());

		else if (ttype == java.lang.Double.class)
			return Utils.isBlank(tmp) ? null : new Double(makeFraction(ann, tmp).doubleValue());

		else if (ttype == java.lang.Float.class)
			return Utils.isBlank(tmp) ? null : new Float(makeFraction(ann, tmp).floatValue());

		else if (ttype.isEnum())
			return cvr.toEnum(ttype, tmp);

		else
			return Utils.isBlank(tmp) ? null : makeString(ann, tmp, isCutPrm);
	}

	private static String makeString(ActionArg ann, String value, Boolean isCutPrm) throws Exception
	{
		// 3. 目标类型校验
		// 4. 精度校验
		// 5. 目标值范围，长度？

		// 文本长度
		if (ann.range() != null && ann.range().length == 2)
		{
			if (!Utils.isBlank(ann.range()[0])
					&& value.length() < Integer.parseInt(ann.range()[0]))
			{
				if (isCutPrm)
				{
					StringBuffer space = new StringBuffer();
					for (int i = 0; i < (Integer.parseInt(ann.range()[0]) - value.length()); i++)
					{
						space.append(" ");
					}
					value += space;
				}
				else
				{
					throw new RuntimeException(!Utils.isBlank(ann.rangeFailMsg()) ? ann.rangeFailMsg() : String.format("输入的%s长度太少，最少应输入%s个字符！", ann.header(), ann.range()[0]));
				}
			}

			if (!Utils.isBlank(ann.range()[1])
					&& value.length() > Integer.parseInt(ann.range()[1]))
			{
				if (isCutPrm)
				{
					value = value.substring(0, Integer.parseInt(ann.range()[1]));
				}
				else
				{
					throw new RuntimeException(!Utils.isBlank(ann.rangeFailMsg()) ? ann.rangeFailMsg() : String.format("输入的%s长度太多，最多可输入%s个字符！", ann.header(), ann.range()[1]));
				}
			}

		}

		// 正则表达式校验？
		if (!Utils.isBlank(ann.regex()))
		{
			if (!Utils.isValidRegex(value, ann.regex()))
				throw new RuntimeException(!Utils.isBlank(ann.regexFailMsg()) ? ann.regexFailMsg() : String.format("输入的%s格式不正确！", ann.header()));
		}

		return value;
	}

	private static java.util.Date makeDate(ActionArg ann, String value) throws Exception
	{
		// 长度 日期格式：yyyy-MM-dd
		if (value.length() != 10)
			throw new RuntimeException(String.format("输入的%s不是有效日期！", ann.header()));

		java.util.Date tmpdate = DateUtils.string2Date(value);

		// 日期范围
		if (ann.range() != null && ann.range().length == 2)
		{
			if (!Utils.isBlank(ann.range()[0])
					&& tmpdate.before(DateUtils.string2Date(ann.range()[0])))
				throw new RuntimeException(!Utils.isBlank(ann.rangeFailMsg()) ? ann.rangeFailMsg() : String.format("%s应大于等于%s！", ann.header(), ann.range()[0]));

			if (!Utils.isBlank(ann.range()[1])
					&& tmpdate.after(DateUtils.string2Date(ann.range()[1])))
				throw new RuntimeException(!Utils.isBlank(ann.rangeFailMsg()) ? ann.rangeFailMsg() : String.format("%s应大于等于%s！", ann.header(), ann.range()[1]));
		}

		return tmpdate;
	}

	private static java.sql.Timestamp makeTimestamp(ActionArg ann, String value) throws Exception
	{
		// 长度 日期格式：yyyy-MM-dd
		//		if (pair[1].length() != 10)
		//			throw new RuntimeException(String.format("输入的%s不是有效日期！", ann.header()));

		java.sql.Timestamp tmptime = DateUtils.string2Timestamp(value);

		// 范围
		if (ann.range() != null && ann.range().length == 2)
		{
			if (!Utils.isBlank(ann.range()[0])
					&& tmptime.before(DateUtils.string2Timestamp(ann.range()[0])))
				throw new RuntimeException(!Utils.isBlank(ann.rangeFailMsg()) ? ann.rangeFailMsg() : String.format("%s应大于等于%s！", ann.header(), ann.range()[0]));

			if (!Utils.isBlank(ann.range()[1])
					&& tmptime.after(DateUtils.string2Timestamp(ann.range()[1])))
				throw new RuntimeException(!Utils.isBlank(ann.rangeFailMsg()) ? ann.rangeFailMsg() : String.format("%s应大于等于%s！", ann.header(), ann.range()[1]));
		}

		return tmptime;
	}

	protected static Long makeNumber(ActionArg ann, String value) throws Exception
	{
		if (!Utils.isNumber(value))
			throw new RuntimeException(String.format("请输入数字：%s！", ann.header()));

		// 精度
		if (!Utils.isValidScale(value, (byte) 0))
			throw new Exception(String.format("%s精度不正确！", ann.header()));

		Long tmpLong = Long.valueOf(value);

		// 值范围		
		if (ann.range() != null && ann.range().length == 2)
		{
			if (!Utils.isBlank(ann.range()[0])
					&& tmpLong < Long.parseLong(ann.range()[0]))
				throw new RuntimeException(!Utils.isBlank(ann.rangeFailMsg()) ? ann.rangeFailMsg() : String.format("%s值应大于等于%s！", ann.header(), ann.range()[0]));

			if (!Utils.isBlank(ann.range()[1])
					&& tmpLong > Long.parseLong(ann.range()[1]))
				throw new RuntimeException(!Utils.isBlank(ann.rangeFailMsg()) ? ann.rangeFailMsg() : String.format("%s值应小于等于%s！", ann.header(), ann.range()[1]));
		}

		return tmpLong;
	}

	private static Double makeFraction(ActionArg ann, String value) throws Exception
	{
		if (!Utils.isNumber(value))
			throw new RuntimeException(String.format("请输入数字：%s！", ann.header()));

		// 精度
		if (!Utils.isBlank(ann.scale())
				&& !Utils.isValidScale(value, Byte.valueOf(ann.scale()).byteValue()))
			throw new Exception(!Utils.isBlank(ann.scaleFailMsg()) ? ann.scaleFailMsg() : String.format("%s精度不正确！", ann.header()));

		Double tmpDouble = Double.valueOf(value);

		// 值范围
		if (ann.range() != null && ann.range().length == 2)
		{
			if (!Utils.isBlank(ann.range()[0])
					&& tmpDouble < Double.valueOf(ann.range()[0]))
				throw new RuntimeException(!Utils.isBlank(ann.rangeFailMsg()) ? ann.rangeFailMsg() : String.format("%s值应大于等于%s！", ann.header(), ann.range()[0]));

			if (!Utils.isBlank(ann.range()[1])
					&& tmpDouble > Double.valueOf(ann.range()[1]))
				throw new RuntimeException(!Utils.isBlank(ann.rangeFailMsg()) ? ann.rangeFailMsg() : String.format("%s值应小于等于%s！", ann.header(), ann.range()[1]));
		}

		return tmpDouble;
	}

	private static Object[] convertArray(ActionArg ann, Class ttype, String[] values, EnumConverter cvr) throws Exception
	{
		// 2. 值是否必填？非必填的，且request.parameter没有值的，等下在转换的时候args中放入null
		if ((values == null || values.length == 0) && ann.required())
			throw new Exception(String.format("请输入%s！", ann.header()));

		if (values == null)
			return values;

		// 3. 数组长度范围				
		if (ann.range() != null && ann.range().length == 2)
		{
			if (!Utils.isBlank(ann.range()[0])
					&& values.length < Integer.parseInt(ann.range()[0]))
				throw new RuntimeException(!Utils.isBlank(ann.rangeFailMsg()) ? ann.rangeFailMsg() : String.format("%s数组长度应大于等于%s！", ann.header(), ann.range()[0]));

			if (!Utils.isBlank(ann.range()[1])
					&& values.length > Integer.parseInt(ann.range()[1]))
				throw new RuntimeException(!Utils.isBlank(ann.rangeFailMsg()) ? ann.rangeFailMsg() : String.format("%s数组长度应小于等于%s！", ann.header(), ann.range()[1]));
		}

		if (ttype == Integer[].class)
		{
			Integer[] tmparrVal = null;
			if (values != null)
			{
				tmparrVal = new Integer[values.length];
				for (int i = 0; i < values.length; i++)
					tmparrVal[i] = Integer.valueOf(values[i]);
			}

			return tmparrVal;
		}

		else if (ttype == Long[].class)
		{
			Long[] tmparrVal = null;
			if (values != null)
			{
				tmparrVal = new Long[values.length];
				for (int i = 0; i < values.length; i++)
					tmparrVal[i] = Long.valueOf(values[i]);
			}
			return tmparrVal;
		}

		else if (ttype == Short[].class)
		{
			Short[] tmparrVal = new Short[values.length];
			for (int i = 0; i < values.length; i++)
				tmparrVal[i] = Short.valueOf(values[i]);

			return tmparrVal;
		}

		else if (ttype == Byte[].class)
		{
			Byte[] tmparrVal = new Byte[values.length];
			for (int i = 0; i < values.length; i++)
				tmparrVal[i] = Byte.valueOf(values[i]);

			return tmparrVal;
		}

		else if (ttype == Float[].class)
		{
			Float[] tmparrVal = new Float[values.length];
			for (int i = 0; i < values.length; i++)
				tmparrVal[i] = Float.valueOf(values[i]);

			return tmparrVal;
		}

		else if (ttype == Double[].class)
		{
			Double[] tmparrVal = new Double[values.length];
			for (int i = 0; i < values.length; i++)
				tmparrVal[i] = Double.valueOf(values[i]);

			return tmparrVal;
		}

		else if (ttype == Boolean[].class)
		{
			Boolean[] tmparrVal = new Boolean[values.length];
			for (int i = 0; i < values.length; i++)
				tmparrVal[i] = Boolean.valueOf(values[i]);

			return tmparrVal;
		}

		else if (ttype == String[].class)
			return values;

		else if (ann.isEnumArray())
			return cvr.toArray(ttype, values);

		else
			return values;
	}
}