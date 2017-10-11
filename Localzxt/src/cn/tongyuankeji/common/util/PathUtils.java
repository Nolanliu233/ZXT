package cn.tongyuankeji.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/*
 * 用户获取项目路径的工具类
 */
public class PathUtils {

	
	public static String getClassPath()
	{
		String temp = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		if(temp.startsWith("/"))
		{
			temp = temp.substring(1);
		}
		try 
		{
			temp  = URLDecoder.decode(temp, "utf-8");
		} 
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return temp;
	}
	
	public static String getWebContentPath()
	{
		String tempPath = getClassPath();
		tempPath = tempPath.substring(0, tempPath.indexOf("WEB-INF"));
		
		
		return tempPath;
	}
	
	public static String getWebInfPath()
	{
		return getWebContentPath()+"WEB-INF/";
	}
	
	public static String getWebConfigPath()
	{
		return getWebInfPath()+"config/";
	}
	
	public static String getOEACLPath()
	{
		return getWebConfigPath()+"OEACL_permission.xml";
	}

	
	public static String getSSOconfigPath()
	{
		return getWebConfigPath()+"sso.properties";
	}


}
