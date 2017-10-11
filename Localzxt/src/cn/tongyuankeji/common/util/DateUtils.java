package cn.tongyuankeji.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期/时间 工具类
 * 
 * @author 代平 2017-05-02 创建
 */
public class DateUtils
{
	private static SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat format4 = new SimpleDateFormat("yyyy_MM_dd"); // 从前端html页面传来的日期
	private static SimpleDateFormat format5 = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss");// 从前端html页面传来的日期
	private static SimpleDateFormat format6 = new SimpleDateFormat("yyMMddHHmmss");
	
	private final static long SECOND_MILLIS = 1000;
	private final static long MINUTE_MILLIS = SECOND_MILLIS * 60;
	private final static long HOUR_MILLIS = MINUTE_MILLIS * 60;
	private final static long DAY_MILLIS = HOUR_MILLIS * 24;
	
	/*----------------------- String转日期/时间 -----------------------*/
	
	/**
	 * String --> 日期
	 */
	public static Date string2Date(String str) throws Exception
	{
		assert !Utils.isBlank(str) : "string2Date(String)参数str不能为空！";
		
		if (Utils.isBlank(str))
			throw new AssertionError("参数str不能为空");
		
		format2.setLenient(false);
		return format2.parse(str); // 不要加Locale。如果用locale.CHINA "2014-09-12"会变成"2014年9月12日"
	}
	
	/**
	 * yyyy-MM-dd格式的String转Timestamp
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Timestamp shortString2Timestamp(String str) throws Exception{
		assert !Utils.isBlank(str) : "string2Timestamp(String)参数str不能为空！";
		if (Utils.isBlank(str))
			throw new AssertionError("参数str不能为空");
		format3.setLenient(false);
		return new Timestamp(format2.parse(str).getTime());
	}
	
	public static Timestamp cutTimestamp(Timestamp time) throws Exception{
		Date date = DateUtils.timestamp2Date(time);
		return DateUtils.shortString2Timestamp(format2.format(date));
	}
	
	/**
	 * String --> 时间
	 */
	public static Timestamp string2Timestamp(String str) throws Exception
	{
		assert !Utils.isBlank(str) : "string2Timestamp(String)参数str不能为空！";
		
		if (Utils.isBlank(str))
			throw new AssertionError("参数str不能为空");
		
		format3.setLenient(false);
		return new Timestamp(format3.parse(str).getTime());
	}
	
	/**
	 * 从前端网页的extjs控件传来的String --> 日期
	 */
	public static Date stringHTML2Date(String str) throws Exception
	{
		assert !Utils.isBlank(str) : "stringHTML2Date(String)参数str不能为空！";
		
		if (Utils.isBlank(str))
			throw new AssertionError("参数str不能为空");
		
		format4.setLenient(false);
		return format4.parse(str);
	}
	
	/**
	 * 从前端网页的extjs控件传来的String --> 时间
	 */
	public static Timestamp stringHTML2Timestamp(String str) throws Exception
	{
		assert !Utils.isBlank(str) : "stringHTML2Timestamp(String)参数str不能为空！";
		
		if (Utils.isBlank(str))
			throw new AssertionError("参数str不能为空");
		
		format5.setLenient(false);
		return new Timestamp(format5.parse(str).getTime());
	}
	
	/*----------------------- 日期/时间转String -----------------------*/
	/**
	 * 日期 --> String
	 */
	public static String date2String(Date date)
	{
		assert date != null : "date2String(Date)参数date不能为空！";
		
		if (date != null)
			return format2.format(date);
		
		return null;
	}
	
	/**
	 * java.sql时间 --> 日期String
	 */
	public static String timestamp2DateString(Timestamp time)
	{
		assert time != null : "timestamp2DateString(Timestamp)参数time不能为空！";
		
		if (time != null)
			return format2.format(time);
		return null;
	}
	
	/**
	 * java.sql时间 --> 时间String
	 */
	public static String timestamp2String(Timestamp time)
	{
		assert time != null : "timestamp2String(Timestamp)参数time不能为空！";
		
		if (time != null)
			return format3.format(time);
		return null;
	}
	
	public static String timestamp2ShortString(Timestamp time){
		assert time != null : "timestamp2String(Timestamp)参数time不能为空！";
		if (time != null)
			return format2.format(time);
		return null;
	}
	
	/**
	 * 日期 --> 年部分
	 */
	public static Integer yearPart(Date date)
	{
		String d = format2.format(date).substring(0, 4);
		return Integer.valueOf(d);
	}
	
	/*----------------------- 日期、时间 互转 -----------------------*/
	
	/**
	 * java.sql日期 --> 时间
	 */
	public static Timestamp date2Timestamp(Date date)
	{
		assert date != null : "date2Timestamp(Date)参数date不能为空！";
		
		if (date != null)
			return new Timestamp(date.getTime());
		return null;
	}
	
	/**
	 * 时间 --> java.sql日期
	 */
	public static Date timestamp2Date(Timestamp time)
	{
		assert time != null : "timestamp2Date(Timestamp)参数time不能为空！";
		
		if (time != null)
			return new Date(time.getTime());
		return null;
	}
	
	/*----------------------- 时间 转DocNum -----------------------*/
	/**
	 * java.sql时间 -- > DocNum。格式：yyMMddHHmmss+surfix
	 */
	public static String timestamp2DocNum(Timestamp time, String surfix)
	{
		assert time != null : "timestamp2DocNum(Timestamp)参数time不能为空！";
		
		if (time != null)
			return format6.format(time) + (surfix != null ? surfix : "");
		
		return surfix;
	}
	
	// DocNum --> 时间。传入的str不能包含后缀
	public static Timestamp DocNum2Timestamp(String str) throws ParseException
	{
		assert !Utils.isBlank(str) : "DocNum2Timestamp(Timestamp)参数str不能为空！";
		
		format6.setLenient(false);
		return new Timestamp(format6.parse(str).getTime());
	}
	
	/*----------------------- 找时间差 -----------------------*/
	
	/**
	 * Seconds difference = laterDate - earlierDate。任何一个值传入空时，返回0
	 */
	public static int secondsDiff(Date earlierDate, Date laterDate)
	{
		if (earlierDate == null || laterDate == null)
			return 0;
		
		return (int) ((laterDate.getTime() / SECOND_MILLIS) - (earlierDate.getTime() / SECOND_MILLIS));
	}
	
	/**
	 * Seconds difference = laterTime - earlierTime。任何一个值传入空时，返回0
	 */
	public static int secondsDiff(Timestamp earlierTime, Timestamp laterTime)
	{
		if (earlierTime == null || laterTime == null)
			return 0;
		
		return (int) ((laterTime.getTime() / SECOND_MILLIS) - (earlierTime.getTime() / SECOND_MILLIS));
	}
	
	/**
	 * minutes difference = laterDate - earlierDate。任何一个值传入空时，返回0
	 */
	public static int minutesDiff(Date earlierDate, Date laterDate)
	{
		if (earlierDate == null || laterDate == null)
			return 0;
		
		return (int) ((laterDate.getTime() / MINUTE_MILLIS) - (earlierDate.getTime() / MINUTE_MILLIS));
	}
	
	/**
	 * minutes difference = laterTime - earlierTime。任何一个值传入空时，返回0
	 */
	public static int minutesDiff(Timestamp earlierTime, Timestamp laterTime)
	{
		if (earlierTime == null || laterTime == null)
			return 0;
		
		return (int) ((laterTime.getTime() / MINUTE_MILLIS) - (earlierTime.getTime() / MINUTE_MILLIS));
	}
	
	/**
	 * Hours difference = laterDate - earlierDate。任何一个值传入空时，返回0
	 */
	public static int hoursDiff(Date earlierDate, Date laterDate)
	{
		if (earlierDate == null || laterDate == null)
			return 0;
		
		return (int) ((laterDate.getTime() / HOUR_MILLIS) - (earlierDate.getTime() / HOUR_MILLIS));
	}
	
	/**
	 * Hours difference = laterTime - earlierTime。任何一个值传入空时，返回0
	 */
	public static int hoursDiff(Timestamp earlierTime, Timestamp laterTime)
	{
		if (earlierTime == null || laterTime == null)
			return 0;
		
		return (int) ((laterTime.getTime() / HOUR_MILLIS) - (earlierTime.getTime() / HOUR_MILLIS));
	}
	
	/**
	 * Days difference = laterDate - earlierDate。任何一个值传入空时，返回0
	 */
	public static int daysDiff(Date earlierDate, Date laterDate)
	{
		if (earlierDate == null || laterDate == null)
			return 0;
		
		return (int) ((laterDate.getTime() / DAY_MILLIS) - (earlierDate.getTime() / DAY_MILLIS));
	}
	
	/**
	 * Days difference = laterTime - earlierTime。任何一个值传入空时，返回0
	 */
	public static int daysDiff(Timestamp earlierTime, Timestamp laterTime)
	{
		if (earlierTime == null || laterTime == null)
			return 0;
		
		return (int) ((laterTime.getTime() / DAY_MILLIS) - (earlierTime.getTime() / DAY_MILLIS)+1);
	}
	
	/*----------------------- 时长、秒 互转 -----------------------*/
	/**
	 * 00:00:00格式的时间数据转换为秒数
	 */
	public static Integer String2Seconds(String str)
	{
		assert !Utils.isBlank(str) : "String2Seconds(String)参数str不能为空！";
		
		int sec = 0;
		String[] parts = str.split(":");
		if (parts == null || parts.length == 0)
			return sec;
		
		try
		{
			sec = Integer.valueOf(parts[parts.length - 1]);
			if (parts.length > 1)
			{
				sec += Integer.valueOf(parts[parts.length - 2]) * 60;
				
				if (parts.length > 2)
					sec += Integer.valueOf(parts[parts.length - 3]) * 60 * 60;
			}
		}
		catch (Exception e)
		{
			return 0;
		}
		
		return sec;
	}
	
	/**
	 * 时间秒数转换为00:00:00格式的时间
	 * 
	 * @param localChinese
	 *            true - 返回文字用中文格式
	 */
	public static String Seconds2String(Integer sec, boolean localChinese)
	{
		assert sec != null : "Seconds2String(Integer)参数str不能为空！";
		
		int remain = 0;
		
		int iHour = 0, iMin = 0, iSec = 0;
		if (sec >= 3600)
			iHour = sec / 3600;
		remain = sec - iHour * 3600;
		
		if (remain >= 60)
			iMin = remain / 60;
		remain = remain - iMin * 60;
		
		iSec = remain;
		
		if (localChinese)
		{
			StringBuffer sb = new StringBuffer();
			
			if (iHour > 0)
				sb.append(String.format("%d小时", iHour));
			
			if (iMin > 0)
				sb.append(String.format("%d分钟", iMin));
			
			if (iSec > 0)
				sb.append(String.format("%d秒", iSec));
			
			return sb.toString();			
		}
		else
		{
			return String.format("%02d:%02d:%02d", iHour, iMin, iSec);
		}
	}
	
	/*----------------------- 日期加减 -----------------------*/
	/**
	 * 日期加delta天
	 */
	public static Date addDays(Date date, long delta)
	{
		assert date != null : "addDays(Date)参数date不能为空！";
		
		return new Date(date.getTime() + delta * 24 * 60 * 60 * 1000);
	}
	
	/*----------------------- web 服务器日期/时间 -----------------------*/
	public static java.sql.Timestamp now()
	{
		return new Timestamp(System.currentTimeMillis());
	}
}

