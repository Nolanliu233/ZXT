package cn.tongyuankeji.common.parameters;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 系统常量
 * 
 * @author 代平 2017-05-02 创建
 */
public class ConstantBase
{
	static final Logger log = LogManager.getLogger(ConstantBase.class.getName());

	public final static Integer DEFAULT_OWNER_ORG_ID = 1;
	
	// 动态验证码request中属性名称
	public final static String VALI_CODE = "valiCode";
	public final static String VALI_CODE_EXPIRE = "valiCodeExpire";

	// Manager服务类返回的JSON包，特殊指令：逻辑错误、网页重定向
	public final static String JSON_KEY_OK = "success";
	public final static String JSON_KEY_ERR = "err";
	public final static String JSON_KEY_WARN = "warn";
	public final static String JSON_KEY_DATA = "data";
	public final static String JSON_KEY_REDIRECT = "redirect"; //前端不用弹窗直接跳转
	public final static String JSON_KEY_URL = "url"; //前端弹窗后跳转
	public final static String JSON_KEY_VER = "ver"; //告之前端，需要进行小版本更新（但不强制）
	public final static String JSON_KEY_ACL = "acl"; //后台方法无权限

	public final static Character FILE_SEP = '/';
	public final static Character ARG_SEP = '?';
	public final static String STR_DOT = ".";
	public final static Character CHAR_DOT = '.';
	
	/**
	 * 用户上传文件物理存储根级路径。<br />
	 * 必须与$tomcat/conf/Catalina/localhost/theimages.xml中，docbase节点值一样
	 */
	public static String getUPLOAD_PATH()
	{
		return ConstantBase.UPLOAD_PATH;
	}

	public static void setUPLOAD_PATH(String uploadRootPath)
	{
		File uploadDir = new File(uploadRootPath);
		if (!uploadDir.exists())
			log.error("user upload folder does not exists: {}", uploadRootPath);
		else
			log.info("RunSettingImpl init user upload folder: {}", uploadRootPath);

		ConstantBase.UPLOAD_PATH = uploadRootPath;
	}

	private static String UPLOAD_PATH = null; // 上传的绝对路径根目录
	
	public static String SEGMENT_HOST="SEGMENT_HOST";//信息详情页面目录
	public static String UPLOAD_VIRTUAL_PATH="UPLOAD_VIRTUAL_PATH";//用户上传文件虚拟路径（前端网页使用）
	public static String UPLOAD_CONTENTSOURCE_PATH="UPLOAD_CONTENTSOURCE_PATH";//网页去燥后所保存的html内容页面
	/**
	 * UTF-8编码
	 */
	public static final String UTF8 = "UTF-8";

	/**
	 * HTTP POST请求
	 */
	public static final String POST = "POST";
	/**
	 * HTTP GET请求
	 */
	public static final String GET = "GET";

	/*--------------------------- 默认园区ID（权限最大） ---------------------------*/
	public final static Integer DEFAULT_OWNER_GOV_ID = 1;

	public final static String DEFAULT_USER_MOBILE_NUM = "18602884804";
	public final static String DEFAULT_USER_OTHER_PHONE = "028-83375686";
	public final static String DEFAULT_USER_PWD = "a123456789";
	/*********************************** 单点登录 SSO 常量定义 *******************************************/
	/**
	 * 基本常量定义
	 */
	public final static String ENCODING = "UTF-8";
	public static final String CONTENT_TYPE = "application/json;charset=UTF-8";

	//AES算法密钥
	public final static String AES_Key = "h2wmABdfM7i3K80mAS";
}

