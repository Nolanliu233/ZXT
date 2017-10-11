package cn.tongyuankeji.common.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.ParserException;

import cn.tongyuankeji.common.parameters.ConstantBase;

/**
 * Static helper methods for strings.
 * 
 * @author 代平 2017-05-02 创建
 */
public class Utils
{
	static final Logger log = LogManager.getLogger(Utils.class.getName());
	
	/*------------------------------ 常用正则表达式------------------------------*/
	
	/**
	 * 邮箱 regx
	 */
	public static final String EMAIL_PATTERN = "^\\w+(\\.\\w+)*@\\w+(\\.\\w+)+$";
	
	/**
	 * URL regx
	 */
	public static final String URL_PATTERN = "^(http|https?|ftp|file|rtmp|rtsp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	
	/**
	 * 手机号 regx
	 */
	public static final String MOBILE_PATTERN = "^0?(1[0-9][0-9]|15[012356789]|18[012356789]|14[57]|17[678])[0-9]{8}$";
	
	/**
	 * IP regx
	 */
	public static final String IP_PATTERN = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";
	
	/**
	 * 密码 regx。正确格式为：以字母开头，长度在6~20之间，只能包含字符、数字和下划线。
	 */
	public static final String PASSWORD_PATTERN = "^[a-zA-Z]\\w{5,17}$";
	
	/**
	 * 固定电话 regx。区号，总机号，分机号之间以“-”隔开
	 */
	public static final String TELEPHONE_PATTERN = "^(0[0-9]{2,3}\\-)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?$";
	
	/**
	 * 用户名 rgex
	 */
	public static final String USERNAME = "^[0-9a-zA-Z\\.\\-@_]+$";
	
	/**
	 * 邮编 rgex（仅适用于中国）
	 */
	public static final String POSTCODE_PATTERN = "[1-9]\\d{5}(?!\\d)";
	
	/*------------------------------ 字符串的比较 ------------------------------*/
	
	/**
	 * 检查传入的字符串str是否为 null，或空字符""。全空格时，返回false。
	 */
	public static boolean isEmpty(final String str)
	{
		return str == null || str.length() == 0;
	}
	
	/**
	 * 检查传入的字符串str是否为 null，或空字符""，或全部是whitespace。
	 */
	public static boolean isBlank(final String str)
	{
		if (isEmpty(str))
			return true;
		
		for (char c : str.toCharArray())
		{
			if (!Character.isWhitespace(c))
				return false;
		}
		
		return true;
	}
	
	/**
	 * 检查传入的str是否只包含数字，小数点、正号、负号。<br />
	 * 正号 和 负号 只能出现在第一个字符位，且只能出现一次<br />
	 * 小数点 只能出现在非第一个字符位，非最后一个字符位，且只能出现一次
	 */
	public static boolean isNumber(String str)
	{
		int plusCnt = 0, minusCnt = 0, dotCnt = 0;
		
		if (!Utils.isBlank(str))
		{
			char arr[] = str.toCharArray();
			
			for (int i = 0; i < arr.length; i++)
			{
				if (!Character.isDigit(arr[i]) && arr[i] != '.'
						&& arr[i] != '-' && arr[i] != '-')
					return false;
				
				if (arr[i] == '.')
				{
					if (i == 0 || i == arr.length - 1)
						return false;
					
					dotCnt++;
					
					if (dotCnt > 1)
						return false;
				}
				
				if (arr[i] == '+')
				{
					if (i != 0)
						return false;
					
					plusCnt++;
					if (plusCnt > 1)
						return false;
				}
				
				if (arr[i] == '-')
				{
					if (i != 0)
						return false;
					
					minusCnt++;
					if (minusCnt > 1)
						return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 检查传入的str是否只包含英文字母（一般作为密码前缀的检查）
	 */
	public static boolean isLetter(String str)
	{
		if (Utils.isBlank(str))
			return false;
		
		char arr[] = str.toCharArray();
		
		for (int i = 0; i < arr.length; i++)
		{
			if (!Character.isLetter(arr[i]))
				return false;
		}
		
		return true;
	}
	
	/**
	 * 检查字符串str1和字符串str2内容是否相等。 <br />
	 * str1和str2都为null，都为""时，都为全whitespace时，返回true
	 * 
	 * @param ignoreCase
	 *            是否忽略大小写
	 */
	public static boolean isEqualString(String str1, String str2, boolean ignoreCase)
	{
		if (Utils.isBlank(str1) && Utils.isBlank(str2))
			return true;
		
		if ((Utils.isBlank(str1) && !Utils.isBlank(str2))
				|| (!Utils.isBlank(str1) && Utils.isBlank(str2)))
			return false;
		
		if (ignoreCase)
			return str1.toUpperCase().equals(str2.toUpperCase());
		else
			return str1.equals(str2);
	}
	
	/**
	 * 对比v1和v2是否值相等。 v1和v2都是null时，返回true。<br />
	 * 对比String的，不能使用此方法，应使用isEqualString(String, String, boolean)
	 */
	public static boolean isEqualValue(Object v1, Object v2)
	{
		if (v1 == null && v2 == null)
			return true;
		else
		{
			if (v1 != null)
				return v1.equals(v2);
			else
				return v2.equals(v1);
		}
	}
	
	/**
	 * str中是否包含search文本。
	 * 
	 * @param str
	 *            被检查的字符串。如果前后包含空格，会先被去除。如果传入null，返回false。
	 * @param search
	 *            要查找的字符串。如果前后包含空格，会先被去除。
	 * @param ignoreCase
	 *            是否忽略大小写
	 */
	public static boolean isContainString(String str, String search, boolean ignoreCase)
	{
		assert !Utils.isBlank(search) : "isContainString(String, String, boolean)的参数search不能为 null，空字符\"\"，或全部是空格！";
		
		if (Utils.isBlank(str))
			return false;
		
		if (ignoreCase)
		{
			return str.trim().toUpperCase()
					.indexOf(search.trim().toUpperCase()) >= 0;
		}
		else
		{
			return str.trim().indexOf(search.trim()) >= 0;
		}
	}
	
	/**
	 * args中是否包含search。
	 * 
	 * @param search
	 *            要查找的字符串。如果前后包含空格，会先被去除。
	 * @param args
	 *            被检查的字符串数组（0～N个）。如果前后包含空格，会先被去除。如果传入null或length == 0，返回false。
	 */
	public static boolean isContainString(String search, boolean ignoreCase, String... args)
	{
		assert !Utils.isBlank(search) : "isContainString(String, boolean, String...)的参数search不能为 null，空字符\"\"，或全部是空格！";
		
		if (args == null || args.length == 0)
			return false;
		
		for (int i = 0; i < args.length; i++)
		{
			if (Utils.isContainString(args[i], search, ignoreCase))
				return true;
		}
		
		return false;
	}
	
	/**
	 * arg中是否有值等于search。<br />
	 * 对比String的，不能使用此方法，应使用isContainString(String, boolean, String...)
	 * 
	 * @param search
	 *            要对比的值。
	 * @param args
	 *            被检查的对象数组（0～N个）。如果传入null或args.length == 0，返回false。
	 */
	public static boolean isContain(Object search, Object... args)
	{
		if (args == null || args.length == 0)
			return false;
		
		for (int i = 0; i < args.length; i++)
		{
			if (search.equals(args[i]))
				return true;
		}
		
		return false;
	}
	
	/**
	 * 检查lst中有值等于search
	 * 
	 * @param lst
	 *            被检查的链。
	 * @param search
	 *            要对比的值。
	 */
	public static boolean isContain(List lst, Object search)
	{
		if (lst.isEmpty())
			return false;
		
		for (Iterator itr = lst.iterator(); itr.hasNext();)
		{
			if (itr.next().equals(search))
				return true;
		}
		
		return false;
	}
	
	/**
	 * 判断args是否为null，是否每个元素都是null。不能用在String[]上
	 */
	public static <T> boolean isArrayNull(T... args)
	{
		if (args == null || args.length == 0)
			return true;
		
		for (int i = 0; i < args.length; i++)
		{
			if (args[i] != null)
				return false;
		}
		
		return true;
	}
	
	/**
	 * 在字符串str中找args中任意一个。返回值小于0时，没找到
	 * 
	 * @param str
	 *            被搜索的文字
	 * @param fromBegining
	 *            true - 从前往后找；false - 从后往前找
	 * @param args
	 *            被查找的字符
	 * @return 大于等于0时，第一个匹配上的位置；小于0时，没找到
	 */
	public static int indexOfAny(String str, boolean fromBegining, char... args)
	{
		assert args != null && args.length > 0 : "indexOfAny(String, char...)参数args不能为空，且长度应大于0！";
		
		int idx;
		
		if (str == null)
			return -1;
		
		for (int i = 0; i < args.length; i++)
		{
			if (fromBegining)
			{
				if ((idx = str.indexOf(args[i])) >= 0)
					return idx;
			}
			else
			{
				if ((idx = str.lastIndexOf(args[i])) >= 0)
					return idx;
			}
		}
		
		return -1;
	}
	
	/*------------------------------ 传入参数业务检查 ------------------------------*/
	/**
	 * 检查val后面的小数点位数是否是0~scale位。
	 * 
	 * @param val
	 *            被检查的数字字符串
	 * @param scale
	 *            小数点后最多可用有几位？
	 */
	public static boolean isValidScale(String val, byte scale)
	{
		assert !Utils.isBlank(val) : "isValidScale(String, byte)的参数val不能为 null，空字符\"\"，或全部是空格！";
		
		char[] arr = val.toCharArray();
		for (int i = 0; i < arr.length; i++)
		{
			if (arr[i] == '.')
				return arr.length - i - 1 <= scale;
		}
		
		return true;
	}
	
	/**
	 * 验证val是否符合regex
	 */
	public static boolean isValidRegex(String val, String regex)
	{
		assert !Utils.isBlank(val) : "isValidRegex(String, String)的参数val不能为 null，空字符\"\"，或全部是空格！";
		assert !Utils.isBlank(regex) : "isValidRegex(String, String)的参数regex不能为 null，空字符\"\"，或全部是空格！";
		
		Pattern ptn = Pattern.compile(regex);
		return ptn.matcher(val).matches();
	}
	
	/*------------------------------ 字符串补长度 ------------------------------*/
	
	/**
	 * 为str补零。一般用于产生ECode。
	 * 
	 * @param str
	 *            被补零的字符串，允许传入null
	 * @param desireLength
	 *            补足到多长
	 * @param isLeading
	 *            true - 在前面补，false - 在后面补
	 */
	public static String amendZero(String str, int desireLength, boolean isLeading)
	{
		if (Utils.isBlank(str))
			str = "";
		
		return Utils.amendCharacter(str, '0', desireLength, isLeading);
	}
	
	/**
	 * 为str补足ch。一般用于SQL字符串比较。
	 * 
	 * @param str
	 *            被补零的字符串
	 * @param ch
	 *            用什么字符补
	 * @param desireLength
	 *            补足到多长
	 * @param isLeading
	 *            true - 在前面补，false - 在后面补
	 */
	public static String amendCharacter(String str, char ch, int desireLength, boolean isLeading)
	{
		assert str != null : "amendCharacter(String, char, int, boolean)的参数str不能为 null，空字符\"\"，或全部是空格！";
		
		String amend = null;
		if (str.length() < desireLength)
		{
			char[] chs = new char[desireLength - str.length()];
			for (int i = 0; i < chs.length; i++)
				chs[i] = ch;
			amend = new String(chs);
		}
		
		if (amend == null)
			return str;
		else if (isLeading)
			return amend + str;
		else
			return str + amend;
	}
	
	/*------------------------------ 字符串拼接 ------------------------------*/
	
	/**
	 * 将args中字符串，用分隔符delimiter粘接在一起，返回一个新的字符串。一般用于将组合字符串（如，sysId）保存到数据库中。<br />
	 * 当args为null时，或length == 0时，返回null。当args中所有元素都是null、空字符""，或全部是空格时，返回null
	 * 
	 * @param delimiter
	 *            分隔符。默认情况是　英文逗号
	 * @param args
	 *            被粘接的字符串，0到N个。遇到null、空字符""，或全部是空格时，跳过
	 */
	public static String concat(char delimiter, String... args)
	{
		StringBuffer sb = null;
		if (args != null && args.length > 0)
		{
			sb = new StringBuffer();
			
			for (int i = 0; i < args.length; i++)
			{
				if (!Utils.isBlank(args[i]))
				{
					if (sb.length() > 0)
						sb.append(delimiter);
					
					sb.append(args[i]);
				}
			}
			
			return sb.length() > 0 ? sb.toString() : null;
		}
		
		return null;
	}
	
	/*------------------------------ 字符串与Array、List互转 ------------------------------*/
	
	/**
	 * 将args中值，用delimiter拼接。<br />
	 * 当args为null时，或length == 0时，返回emptyDefault。当args中所有元素都是null、空字符""，或全部是空格时，返回emptyDefault
	 * 
	 * @param T
	 *            必须是java.lang.Object派生类
	 */
	public static <T> String array2String(char delimiter, String emptyDefault, T... args)
	{
		StringBuffer sb = null;
		if (args != null && args.length > 0)
		{
			sb = new StringBuffer();
			
			for (T element : args)
			{
				if (sb.length() > 0)
					sb.append(delimiter);
				sb.append(element.toString());
			}
			
			return sb.length() > 0 ? sb.toString() : emptyDefault;
		}
		
		return emptyDefault;
	}
	
	/**
	 * 将lst中值，用delimiter拼接。<br />
	 * 当lst是null或size() == 0时，返回emptyDefault。当lst中所有元素都是null、空字符""，或全部是空格时，返回emptyDefault<br />
	 * 
	 * @param T
	 *            必须是java.lang.Object派生类
	 */
	public static <T> String list2String(char delimiter, String emptyDefault, List<T> lst)
	{
		StringBuffer sb = null;
		if (lst != null && lst.size() > 0)
		{
			sb = new StringBuffer();
			for (T element : lst)
			{
				if (sb.length() > 0)
					sb.append(delimiter);
				sb.append(element.toString());
			}
			
			return sb.length() > 0 ? sb.toString() : emptyDefault;
		}
		
		return emptyDefault;
	}
	
	/**
	 * 将args中值，用delimiter拼接，每个元素用单引号包裹，一般用于组合成数据库查询IN字段。<br />
	 * 当args为null时，或length == 0时，返回emptyDefault。当args中所有元素都是null、空字符""，或全部是空格时，返回emptyDefault
	 */
	public static String array2SqlString(char delimiter, String emptyDefault, String... args)
	{
		StringBuffer sb = null;
		if (args != null && args.length > 0)
		{
			sb = new StringBuffer();
			
			for (String element : args)
			{
				if (sb.length() > 0)
					sb.append(delimiter);
				sb.append("'" + element.toString() + "'");
			}
			
			return sb.length() > 0 ? sb.toString() : emptyDefault;
		}
		
		return emptyDefault;
	}
	
	/**
	 * 将String[]转化成type的目标数据类型。例如String[]转Integer[]，String[]转Boolean[]
	 * 
	 * @param args
	 *            被转换的String[]。当传入null，或length == 0，返回null
	 */
	public static <T> T[] convertStringArray(String... args) throws Exception
	{
		if (args == null || args.length == 0)
			return null;
		
		Class<?> t = args[0].getClass();
		T[] result = (T[]) Array.newInstance(t, args.length);
		Constructor ctor = t.getConstructor(String.class); // 例如：new Integer(String)
		
		for (int i = 0; i < args.length; i++)
			result[i] = (T) ctor.newInstance(args[i]);
		
		return result;
	}
	
	/**
	 * str是由delimiter分割的字符串，使用delimiter切割后，转换成type的目标数据类型
	 * 
	 * @param str
	 *            被切割的字符串。传入null，或length == 0时，返回null
	 * 
	 * @param delimiter
	 *            分隔符
	 */
	public static <T> T[] string2Array(String str, String delimiter) throws Exception
	{
		assert !Utils.isBlank(delimiter) : "string2Array(String, String)参数delimiter不能为空、空字符\"\"，或全部是空格！";
		
		if (Utils.isBlank(str))
			return null;
		
		String[] arr = str.split(delimiter);
		return Utils.convertStringArray(arr);
	}
	
	/*------------------------------ Array、List帮助方法  ------------------------------*/
	/**
	 * 在有多个重复名称属性的类上，帮助设置值
	 * 
	 * @param target
	 *            对象实例，如Student
	 * @param fieldName
	 *            成员变量名称，首字母大写。如："SpecialtyId"
	 * @param valType
	 *            成员变量类型。如：Integer.class
	 * @param val
	 *            一连串的值。注意长度要和同名变量个数相同，且按顺序排。<br />
	 *            如：传入1,5,3,null,null会调用到Student.setSpecialty1(1),
	 *            .setSpecialty2(5)，.setSpecialty3(3)，.setSpecialty4(null)，.setSpecialty5(null)
	 */
	public static <T> void setFields(Object target, String fieldName, Class<?> valType, T... val)
	{
		assert val != null && val.length > 0 : "setFields(Object, String, Class<?>, T[])参数val不能为空，且长度大于0！";
		
		Method mth = null;
		Class<?> clz = target.getClass();
		Integer idx = 0;
		String mthName = null;
		
		try
		{
			for (idx = 1; idx <= val.length; idx++)
			{
				mthName = "set" + fieldName + idx.toString();
				
				mth = clz.getMethod(mthName, valType);
				mth.invoke(target, val[idx - 1]);
			}
		}
		catch (NoSuchMethodException ex)
		{
			log.error("{} Type has no such method defined: {}", clz.getName(), mthName);
		}
		catch (IllegalAccessException | InvocationTargetException iex)
		{
			log.error("{} Type.{} does not accept: {}", clz.getName(), mthName, (val[idx - 1] == null ? "null" : val[idx - 1].toString()));
		}
	}
	
	/**
	 * list转array。当arr是null或length == 0时，返回null。
	 * 
	 * @param arrayLength
	 *            返回的array的长度。当需要用null补足到此长度时，传入值，且应大于等于lst的长度。传入null时，不补。
	 */
	public static <T> T[] list2Array(List<T> lst, Integer arrayLength)
	{
		if (arrayLength != null)
			assert arrayLength >= lst.size() : "list2Array(List<T>, Integer)中传入的arrayLength必须大于等于lst.size()";
		
		if (lst == null || lst.size() == 0)
			return null;
		
		Class<?> t = lst.get(0).getClass();
		T[] result = (T[]) Array.newInstance(t, lst.size());
		
		int idx = 0;
		for (Iterator<T> itr = lst.iterator(); itr.hasNext();)
		{
			result[idx] = (T) itr.next();
			idx++;
		}
		
		if (arrayLength != null)
		{
			for (; idx < arrayLength; idx++)
				result[idx] = null;
		}
		
		return result;
	}
	
	/*------------------------------ 文件名、java类名帮助方法 ------------------------------*/
	public static String getCleanClassName(Object obj)
	{
		return getCleanClassName(obj.getClass());
	}
	
	/**
	 * 取得java类的名称，去掉package路径
	 */
	public static String getCleanClassName(Class<?> type)
	{
		String name = type.getName();
		int dotIdx = name.lastIndexOf(".");
		if (dotIdx >= 0)
			name = name.substring(dotIdx + 1, name.length());
		return name;
	}
	
	/**
	 * 去掉路径，只保留文件名（含后缀名）
	 * 
	 * @param fid
	 *            一般是FileUploadServlet返回的fid，或FileUploadManager.getAbsoluteFilename()结果，含路径和文件
	 */
	public static String getCleanFilename(String fid)
	{
		// 找最后一个路径分隔符
		int idx = fid.lastIndexOf(ConstantBase.FILE_SEP);// Utils.indexOfAny(fid, false, ConstantBase.FILE_SEP,
															// ConstantBase.PATH_SEP);
		if (idx >= 0)
		{
			fid = fid.substring(idx + 1);
			
			// 文件名后是否有参数？
			idx = fid.indexOf(ConstantBase.ARG_SEP);
			if (idx >= 0)
				fid = fid.substring(0, idx);
		}
		
		return fid;
	}
	
	/**
	 * 将fileName切割成文件名和后缀名
	 */
	public static String[] splitFilename(String filename)
	{
		int dot = -1;
		
		filename = Utils.getCleanFilename(filename);
		
		if ((dot = filename.lastIndexOf('.')) < 0)
			return new String[] { filename, "" };
		else
			return new String[] { filename.substring(0, dot), filename.substring(dot + 1) };
	}
	
	/********************* 文字剪切 *********************/
	/**
	 * 剪切文本。如果进行了剪切，则在文本后加上"..."
	 * 
	 * @param s
	 *            剪切对象。
	 * @param len
	 *            编码小于256的作为一个字符，大于256的作为两个字符。
	 * @return
	 */
	public static String textCut(String s, int len, String append)
	{
		if (s == null)
		{
			return null;
		}
		int slen = s.length();
		if (slen <= len)
		{
			return s;
		}
		// 最大计数（如果全是英文）
		int maxCount = len * 2;
		int count = 0;
		int i = 0;
		for (; count < maxCount && i < slen; i++)
		{
			if (s.codePointAt(i) < 256)
			{
				count++;
			}
			else
			{
				count += 2;
			}
		}
		if (i < slen)
		{
			if (count > maxCount)
			{
				i--;
			}
			if (!Utils.isBlank(append))
			{
				if (s.codePointAt(i - 1) < 256)
				{
					i -= 2;
				}
				else
				{
					i--;
				}
				return s.substring(0, i) + append;
			}
			else
			{
				return s.substring(0, i);
			}
		}
		else
		{
			return s;
		}
	}
	
	public static String htmlCut(String s, int len, String append)
	{
		String text = Utils.html2Text(s, len * 2);
		return textCut(text, len, append);
	}
	
	/**
	 * 分割并且去除空格
	 * 
	 * @param str
	 *            待分割字符串
	 * @param sep
	 *            分割符
	 * @param sep2
	 *            第二个分隔符
	 * @return 如果str为空，则返回null。
	 */
	public static String[] splitAndTrim(String str, String sep, String sep2)
	{
		if (Utils.isBlank(str))
		{
			return null;
		}
		if (!Utils.isBlank(sep2))
		{
			str = org.apache.commons.lang3.StringUtils.replace(str, sep2, sep);
		}
		String[] arr = org.apache.commons.lang3.StringUtils.split(str, sep);
		// trim
		for (int i = 0, len = arr.length; i < len; i++)
		{
			arr[i] = arr[i].trim();
		}
		return arr;
	}
	
	/**
	 * html转文本
	 */
	public static String html2Text(String html, int len)
	{
		try
		{
			Lexer lexer = new Lexer(html);
			Node node;
			StringBuilder sb = new StringBuilder(html.length());
			while ((node = lexer.nextNode()) != null)
			{
				if (node instanceof TextNode)
				{
					sb.append(node.toHtml());
				}
				if (sb.length() > len)
				{
					break;
				}
			}
			return sb.toString();
		}
		catch (ParserException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 文本转html
	 * 
	 * @param txt
	 * @return
	 */
	public static String txt2htm(String txt)
	{
		if (Utils.isBlank(txt))
		{
			return txt;
		}
		StringBuilder sb = new StringBuilder((int) (txt.length() * 1.2));
		char c;
		boolean doub = false;
		for (int i = 0; i < txt.length(); i++)
		{
			c = txt.charAt(i);
			if (c == ' ')
			{
				if (doub)
				{
					sb.append(' ');
					doub = false;
				}
				else
				{
					sb.append("&nbsp;");
					doub = true;
				}
			}
			else
			{
				doub = false;
				switch (c)
				{
					case '&':
						sb.append("&amp;");
						break;
					case '<':
						sb.append("&lt;");
						break;
					case '>':
						sb.append("&gt;");
						break;
					case '"':
						sb.append("&quot;");
						break;
					case '\n':
						sb.append("<br/>");
						break;
					default:
						sb.append(c);
						break;
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 电子邮件开头用 * 替换，如12345678@iti.com替换成****5678@iti.com，321@qq.com替换成**1@qq.com
	 */
	public static String replaceEmail(String email)
	{
		char[] chs;
		String acc = null;
		if (!Utils.isBlank(email))
		{
			acc = email.substring(0, email.indexOf("@"));
			
			if (acc.length() >= 4)
			{
				chs = new char[4];
				acc = acc.substring(4, acc.length());
			}
			else
			{
				chs = new char[2];
				acc = acc.substring(2, acc.length());
			}
			
			for (int i = 0; i < chs.length; i++)
				chs[i] = '*';
			
			acc = new String(chs) + acc;
			
			return acc + email.substring(email.indexOf("@"));
		}
		else
		{
			return "****";
		}
	}
	
	/**
	 * 电话号码中间用 * 替换，如13912345678替换成13*******89，85501234替换成85****34
	 */
	public static String replacePhoneNumber(String phone)
	{
		char[] chs;
		if (!Utils.isBlank(phone) && phone.length() > 4)
		{
			chs = new char[phone.length() - 4];
			for (int i = 0; i < chs.length; i++)
				chs[i] = '*';
			
			return phone.substring(0, 2) + new String(chs) + phone.substring(phone.length() - 2, phone.length());
		}
		else
		{
			return "****";
		}
	}
	
	/**
	 * 身份证号码中间用 * 替换，如5101234567897896225替换为5101**********6225
	 */
	public static String replacePidNumber(String pidNumber)
	{
		char[] chs;
		if (!Utils.isBlank(pidNumber) && pidNumber.length() > 8)
		{
			chs = new char[pidNumber.length() - 8];
			for (int i = 0; i < chs.length; i++)
				chs[i] = '*';
			
			return pidNumber.substring(0, 4) + new String(chs) + pidNumber.substring(pidNumber.length() - 4, pidNumber.length());
		}
		else
		{
			return "********";
		}
	}
	

	//去除字符串中的空格、回车、换行符、制表符
	public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
}