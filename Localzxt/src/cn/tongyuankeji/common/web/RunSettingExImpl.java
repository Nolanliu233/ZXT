package cn.tongyuankeji.common.web;

import java.lang.reflect.Field;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.tongyuankeji.common.annotation.FieldRange;
import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.common.web.RunSetting;
import cn.tongyuankeji.common.web.RunSettingImpl;

/**
 * 系统配置
 * 
 * @author 代平 2017-05-02 创建
 */

public final class RunSettingExImpl extends RunSettingImpl
{
	static final Logger log = LogManager.getLogger(RunSettingImpl.class.getName());
	
	private RunSettingExImpl()
	{}
	
	/*----------------------------------------- 全局 -----------------------------------------*/
	
	/**
	 * 列表页面显示简略介绍时，取前多少个字
	 */
	public static Integer getINTRO_WORD_COUNT()
	{
		return RunSettingExImpl.INTRO_WORD_COUNT;
	}
	
	@FieldRange(min = "10", max = "100", defaultAs = "12")
	private static Integer INTRO_WORD_COUNT = 10;
	
	
	/*----------------------------------------- 用户文件 -----------------------------------------*/
	/**
	 * 上传的“证件照”、“文章封面图片”文件大小限制，in KB。默认值100，范围1～10240
	 */
	public static int getPHOTO_FILE_KB()
	{
		return RunSettingExImpl.PHOTO_FILE_KB;
	}
	
	@FieldRange(min = "1", max = "10240"/* 10MB */, defaultAs = "100" /* 100KB */)
	private static Integer PHOTO_FILE_KB = 100;
	
	/**
	 * 上传“人员头像”图片文件大小限制，in KB。默认值20，范围1～1024
	 */
	public static int getTHUMB_FILE_KB()
	{
		return RunSettingExImpl.THUMB_FILE_KB;
	}
	
	@FieldRange(min = "1", max = "1024"/* 1MB */, defaultAs = "20" /* 20KB */)
	private static Integer THUMB_FILE_KB = 0;
	
	/**
	 * 用户证照图片存放相对路径
	 */
	public static String getPID_PATH()
	{
		return RunSettingExImpl.PID_PATH;
	}
	
	private static String PID_PATH = null;
	
	/**
	 * “用户资源”、“文章附件”文件大小限制，in KB。默认值10240，范围1～51200
	 */
	public static int getPDFILE_KB()
	{
		return RunSettingExImpl.PDFILE_KB;
	}
	
	@FieldRange(min = "1", max = "51200"/* 50MB */, defaultAs = "10240"/* 10MB */)
	private static Integer PDFILE_KB = 0;
	
	/**
	 * 用户头像图片存放相对路径
	 */
	public static String getTHUMB_PATH()
	{
		return RunSettingExImpl.THUMB_PATH;
	}
	
	private static String THUMB_PATH = null;
	
	/**
	 * 用户资源存放相对路径
	 */
	public static String getPDFILE_PATH()
	{
		return RunSettingExImpl.PDFILE_PATH;
	}
	
	private static String PDFILE_PATH = null;
	
	/**
	 * Android手机APP存放路径
	 */
	public static String getAPP_PATH()
	{
		return RunSettingExImpl.APP_PATH;
	}
	
	private static String APP_PATH = null;
	
	/**
	 * IOS手机App在苹果商店的访问路径
	 */
	public static String getAPP_IOS_PATH()
	{
		return RunSettingExImpl.APP_IOS_PATH;
	}
	
	private static String APP_IOS_PATH = null;
	
	
	/**
	 * Android手机APP版本号，用于登录时服务器检查匹配性
	 */
	public static String getAPP_VERSION()
	{
		return RunSettingExImpl.APP_VERSION;
	}
	
	public static String getAPP_VERSION_LEFT()
	{
		return RunSettingExImpl.APP_VERSION.substring(0, RunSettingExImpl.APP_VERSION.lastIndexOf(ConstantBase.STR_DOT));
	}
	
	private static String APP_VERSION = null;
	
	/**
	 * IOS手机APP版本号，用于登录时服务器检查匹配性
	 */
	public static String getAPP_IOS_VERSION()
	{
		return RunSettingExImpl.APP_IOS_VERSION;
	}
	
	public static String getAPP_IOS_VERSION_LEFT()
	{
		return RunSettingExImpl.APP_IOS_VERSION.substring(0, RunSettingExImpl.APP_IOS_VERSION.lastIndexOf(ConstantBase.STR_DOT));
	}
	
	private static String APP_IOS_VERSION = null;
	
	
	/*----------------------------------------- 应用配置 -----------------------------------------*/
	/**
	 * 应用数据库读取的配置信息到内存缓存中。处理业务相关配置（不由基类AppSettingBase）的应用
	 * 
	 * @param src
	 *            配置项
	 */
	public static void applySetting(List lstSetting)
	{
		Integer cnt = 0;
		RunSetting src = null;
		
		// getFields()获得某个类的所有的公共（public）的字段，包括父类。
		// getDeclaredFields()获得某个类的所有申明的字段，即包括public、private和proteced，但是不包括父类的申明字段
		
		Field[] fs = RunSettingExImpl.class.getDeclaredFields();
		
		for (int i = 0, len = lstSetting.size(); i < len; i++)
		{
			src = (RunSetting) lstSetting.get(i);
			
			// try apply setting to base app setting
			if (!RunSettingImpl.applySetting(src, cnt))
			{
				// if not in RunSettingImpl, try RunSettingExImpl
				for (int j = 0; j < fs.length; j++)
				{
					if (fs[j].getName().equals(src.getSettingKey()))
					{
						RunSettingExImpl.applySetting(fs[j], src);
						
						log.debug("appsetting item {}: {}={}", cnt, src.getSettingKey(), src.getSettingValue());
						cnt++;
						
						break;
					}
				}
			}
			else
				cnt++;
		}
	}
	
	// 修改配置后，应用到运行
	private static void applySetting(Field f, RunSetting src)
	{
		try
		{
			if (f.getType() == Integer.class)
				f.set(null, Integer.valueOf(src.getSettingValue()));
			
			else if (f.getType() == Boolean.class)
				f.set(null, Boolean.valueOf(src.getSettingValue()));
			
			else if (f.getType() == Double.class)
				f.set(null, Double.valueOf(src.getSettingValue()));
			
			else if (f.getType() == Short.class)
				f.set(null, Short.valueOf(src.getSettingValue()));
			
			else if (f.getType() == Byte.class)
				f.set(null, Byte.valueOf(src.getSettingValue()));
			
			else if (f.getType() == String.class)
				f.set(null, src.getSettingValue());
		}
		catch (Exception ex)
		{
			log.error("loading appsetting err", ex);
		}
	}
}