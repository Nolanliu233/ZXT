package cn.tongyuankeji.common.web;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.tongyuankeji.common.annotation.FieldRange;

/**
 * 系统配置运行时 基类
 * 
 * @author 代平 2017-05-02 创建
 */

public class RunSettingImpl
{
	static final Logger log = LogManager.getLogger(RunSettingImpl.class.getName());
	
	protected RunSettingImpl()
	{}
	
	/*----------------------------------------- 全局 -----------------------------------------*/
	/**
	 * 网站域名
	 */
	public static String getSITE_DOMAIN()
	{
		return RunSettingImpl.SITE_DOMAIN;
	}
	
	private static String SITE_DOMAIN = null;
	
	/**
	 * WEB服务器端口
	 */
	public static String getSITE_PORT()
	{
		return RunSettingImpl.SITE_PORT;
	}
	
	private static String SITE_PORT = null;
	
	/**
	 * 网站URL全路径，含http://
	 */
	public static String getSiteUrl()
	{
		return "http://" + RunSettingImpl.SITE_DOMAIN + ":" + RunSettingImpl.SITE_PORT;
	}
	
	/**
	 * 网站语言
	 */
	public static String getSITE_LOCALE()
	{
		return RunSettingImpl.SITE_LOCALE;
	}
	
	private static String SITE_LOCALE = null;
	
	/**
	 * 网站显示名称
	 */
	public static String getSITE_NAME()
	{
		return SITE_NAME;
	}
	
	private static String SITE_NAME = null;
	
	/**
	 * 用户可上传的图片文件扩展名
	 */
	public static String getUPLOAD_IMAGE_EXT()
	{
		return RunSettingImpl.UPLOAD_IMAGE_EXT;
	}
	
	private static String UPLOAD_IMAGE_EXT = null;
	
	/**
	 * 用户可上传的其它类文件扩展名
	 */
	public static String getUPLOAD_FILE_EXT()
	{
		return RunSettingImpl.UPLOAD_FILE_EXT;
	}
	
	private static String UPLOAD_FILE_EXT = null;
	
	/**
	 * 全局登陆有效时间，分钟。默认值120，范围10～1440
	 */
	public static int getSESSION_EXPIRE_MINUTE()
	{
		return RunSettingImpl.SESSION_EXPIRE_MINUTE;
	}
	
	@FieldRange(min = "10", max = "1440", defaultAs = "120")
	private static Integer SESSION_EXPIRE_MINUTE = 120;
	
	/**
	 * sitelog中，Body字段是否记录entity对象的细节
	 */
	public static boolean isENTITY_LOG_ON()
	{
		return RunSettingImpl.ENTITY_LOG_ON;
	}
	
	private static boolean ENTITY_LOG_ON = true;
	
	/**
	 * 分页时，每页显示记录条数。默认值20，范围5～100
	 */
	public static Integer getPAGINATION_COUNT()
	{
		return RunSettingImpl.PAGINATION_COUNT;
	}
	
	@FieldRange(min = "5", max = "100", defaultAs = "20")
	private static Integer PAGINATION_COUNT = 20;
	
	/**
	 * 用户上传文件虚拟路径，前端网页使用 <br />
	 * 必须与$tomcat/conf/Catalina/localhost/theimages.xml中，path节点配置一样<br />
	 * 输出时，如：AppSettingBase.getSiteUrl() + USER_FILE_VIRTUAL_PATH + sub-directory
	 */
	public static String getUPLOAD_VIRTUAL_PATH()
	{
		return RunSettingImpl.UPLOAD_VIRTUAL_PATH;
	}
	
	private static String UPLOAD_VIRTUAL_PATH = null;
	
	/**
	 * 用户上传文件临时目录
	 */
	public static String getUPLOAD_TMP_PATH()
	{
		return RunSettingImpl.UPLOAD_TMP_PATH;
	}
	
	private static String UPLOAD_TMP_PATH = null;
	
	/**
	 * 新闻公告html保存路径
	 */
	public static String getUPLOAD_NEWS_FILE()
	{
		return RunSettingImpl.UPLOAD_NEWS_FILE;
	}
	
	private static String UPLOAD_NEWS_FILE = null;
	
	/**
	 * 代码根目录
	 */
	public static String getCODE_ROOT()
	{
		return RunSettingImpl.CODE_ROOT;
	}
	
	private static String CODE_ROOT = null;
	
	/*----------------------------------------分词索引服务相关参数------------------------------------------*/
	/**
	 * 分词索引接口地址
	 * */
	public static String getSEGMENT_HOST(){
		return RunSettingImpl.SEGMENT_HOST;
	}
	
	private static String SEGMENT_HOST = null;
	
	/**
	 * 分词索代码根目录（项目名称）
	 * */
	public static String getSEGMENT_CODE_ROOT(){
		return RunSettingImpl.SEGMENT_CODE_ROOT;
	}
	
	private static String SEGMENT_CODE_ROOT = null;
	/**
	 * html去噪后的文件保存路径
	 * */
	public static String getUPLOAD_CONTENT_SOURCE_PATH(){
		return RunSettingImpl.UPLOAD_CONTENT_SOURCE_PATH;
	}
	
	private static String UPLOAD_CONTENT_SOURCE_PATH = null;
	
	/**
	 * 分词索引接口地址
	 * */
	public static String getSEGMENT_URL(){
		return "http://" + RunSettingImpl.SEGMENT_HOST + RunSettingImpl.SEGMENT_CODE_ROOT;
	}
	
	/**
	 * 去噪后的页面保存绝对路径
	 * */
	public static String getCONTENT_SOURCE_PATH(){
		return "http://" + RunSettingExImpl.getSEGMENT_HOST() + RunSettingExImpl.getUPLOAD_VIRTUAL_PATH() + 
				RunSettingImpl.getUPLOAD_CONTENT_SOURCE_PATH();
	}
	
	/*----------------------------------------- 用户、后台用户 -----------------------------------------*/
	/**
	 * 验证码有效秒数。默认值60，范围5～300
	 */
	public static Integer getVERIFICATION_CODE_SECOND()
	{
		return RunSettingImpl.VERIFICATION_CODE_SECOND;
	}
	
	@FieldRange(min = "5", max = "300", defaultAs = "3600")
	private static Integer VERIFICATION_CODE_SECOND = 60;
	
	/*----------------------------------------- 应用配置 -----------------------------------------*/
	/**
	 * 应用数据库读取的配置信息到内存缓存中
	 * 
	 * @param src
	 *            配置项
	 */
	public static boolean applySetting(RunSetting src, Integer cnt)
	{
		Field[] fs = RunSettingImpl.class.getDeclaredFields();
		
		for (int j = 0; j < fs.length; j++)
		{
			if (fs[j].getName().equals(src.getSettingKey()))
			{
				RunSettingImpl.applySettingBase(fs[j], src);
				
				log.debug("appsetting item {}: {}={}", cnt, src.getSettingKey(), src.getSettingValue());
				
				return true;
			}
		}
		
		return false;
	}
	
	private static void applySettingBase(Field f, RunSetting src)
	{
		try
		{
			if (f.getType() == Integer.class)
				f.set(null, Integer.valueOf(src.getSettingValue()));
			
			else if (f.getType() == Boolean.class)
				f.set(null, Boolean.valueOf(src.getSettingValue()));
			
			else if (f.getType() == Double.class)
				f.set(null, Double.valueOf(src.getSettingValue()));
			
			else if (f.getType() == String.class)
				f.set(null, src.getSettingValue());
			
		}
		catch (Exception ex)
		{
			log.error(String.format("apply appsetting %s failed", f.getName()), ex);
		}
	}
}