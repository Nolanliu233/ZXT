package cn.tongyuankeji.common.util;


import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.tongyuankeji.common.exception.ACLException;
import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.common.web.RunSettingImpl;
import org.hibernate.exception.*;;

/**
 * HttpServletResponse帮助类
 * 
 * @author 代平 2017-05-02 创建
 */
public final class ResponseUtils
{
	static final Logger log = LogManager.getLogger(ResponseUtils.class.getName());
	
	private final static String DB_TIME_OUT = "41000";
	@SuppressWarnings("unused")
	private final static String DB_TOO_LONG = "22001";
	@SuppressWarnings("unused")
	private final static String DB_INTEGRITY = "23000";
	
	/**
	 * 将异常ex转json包
	 */
	public static void renderJson(HttpServletResponse resp, Exception ex)
	{
		assert resp != null : "renderJson(HttpServletResponse, Exception)参数resp不能为空！";
		assert ex != null : "renderJson(HttpServletResponse, Exception)参数ex不能为空！";
		
		String errMsg = null;
		
		/* ********************** JDBC exception ********************* */
		if (ex instanceof GenericJDBCException)
		{
			if (DB_TIME_OUT.equals(((GenericJDBCException) ex).getSQLState().equals("")))
				errMsg = "系统忙，请稍候再试！"; // 超时
		}
		else if (ex instanceof JDBCConnectionException)
		{
			// ((org.hibernate.exception.JDBCConnectionException)ex).getSQLState()
			errMsg = "无法链接到数据库";
		}
		else if (ex instanceof LockAcquisitionException)
		{
			System.out.println("org.hibernate.exception.LockAcquisitionException");
		}
		else if (ex instanceof DataException)
		{
			errMsg = "数据过长：" + ex.getCause().getMessage();
		}
		else if (ex instanceof ConstraintViolationException)
		{
			errMsg = ((org.hibernate.exception.ConstraintViolationException) ex).getCause().getMessage();
			// if (DB_INTEGRITY.equals(((org.hibernate.exception.ConstraintViolationException)ex).getSQLState()))
			// errMsg = "...必填";
		}
		else if (ex instanceof SQLGrammarException)
		{
			System.out.println("org.hibernate.exception.SQLGrammarException");
		}
		
		/* ********************** springframework.dao exception ********************* */
		else if (ex instanceof org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException)
		{
			errMsg = "文件太大！";
		}
		
		if (errMsg == null)
		{
			if (Utils.isEmpty(ex.getMessage()))
				errMsg = ex.getClass().getName();
			else
				errMsg = ex.getMessage();
		}
		
		JSONObject o = new JSONObject();
		o.put(ConstantBase.JSON_KEY_ERR, errMsg);
		
		render(resp, "application/json;charset=UTF-8", o.toString());
	}
	
	/**
	 * ACLException专用。目前只在花页编辑器等客户端使用
	 */
	public static void renderJson(HttpServletResponse resp, ACLException aex)
	{
		assert resp != null : "renderJson(HttpServletResponse, Exception)参数resp不能为空！";
		assert aex != null : "renderJson(HttpServletResponse, ACLException)参数aex不能为空！";
		
		JSONObject o = new JSONObject();
		o.put(ConstantBase.JSON_KEY_ACL, aex.getMessage());
		render(resp, "application/json;charset=UTF-8", o.toString());
	}
	
	/**
	 * 操作成功后，对网页返回带实例的数据包。<br />
	 * success: msg<br />
	 * data: data (data == null时，填入JSONNull)
	 */
	public static void renderJson(HttpServletResponse resp, String msg, JSONObject data)
	{
		assert resp != null : "renderJson(HttpServletResponse, String, JSONObject)参数resp不能为空！";
		
		JSONObject o = new JSONObject();
		o.put(ConstantBase.JSON_KEY_OK, Utils.isBlank(msg) ? "成功" : msg);
		o.put(ConstantBase.JSON_KEY_DATA, data == null ? JSONNull.getInstance() : data);
		
		render(resp, "application/json;charset=UTF-8", o.toString());
	}
	
	/**
	 * 操作成功后，对网页返回带array的数据包。<br />
	 * success: msg<br />
	 * data: dataarray (dataarray == null时，填入JSONNull)
	 */
	public static void renderJson(HttpServletResponse resp, String msg, JSONArray dataarray)
	{
		assert resp != null : "renderJson(HttpServletResponse, String, JSONArray)参数resp不能为空！";
		
		JSONObject o = new JSONObject();
		o.put(ConstantBase.JSON_KEY_OK, Utils.isBlank(msg) ? "成功" : msg);
		o.put(ConstantBase.JSON_KEY_DATA, dataarray == null ? JSONNull.getInstance() : dataarray);
		
		render(resp, "application/json;charset=UTF-8", o.toString());
	}
	
	/**
	 * 操作成功后，返回 成功提示 文本包。<br />
	 * success: msg<br />
	 * data: JSONNull
	 */
	public static void renderSuccessJson(HttpServletResponse resp, String msgFormat, Object... args)
	{
		assert resp != null : "renderSuccessJson(HttpServletResponse, String)参数resp不能为空！";
		assert !Utils.isBlank(msgFormat) : "renderSuccessJson(HttpServletResponse, String)参数msgFormat不能为空！";
		
		JSONObject o = JsonUtils.createSimple(
				ConstantBase.JSON_KEY_OK,
				args != null && args.length > 0 ? String.format(msgFormat, args) : msgFormat);
		o.put(ConstantBase.JSON_KEY_DATA, JSONNull.getInstance());
		
		render(resp, "application/json;charset=UTF-8", o.toString());
	}
	
	/**
	 * Action层检测失败时，返回 错误提示 文本包。<br />
	 * err: msg
	 */
	public static void renderErrorJson(HttpServletResponse resp, String errFormat, Object... args)
	{
		assert resp != null : "renderErrorJson(HttpServletResponse, String)参数resp不能为空！";
		assert !Utils.isBlank(errFormat) : "renderErrorJson(HttpServletResponse, String)参数errFormat不能为空！";
		
		JSONObject o = JsonUtils.createSimple(
				ConstantBase.JSON_KEY_ERR,
				args != null && args.length > 0 ? String.format(errFormat, args) : errFormat);
		
		render(resp, "application/json;charset=UTF-8", o.toString());
	}
	
	/**
	 * 强制网页进行页面跳转包。<br />
	 * success: msg<br />
	 * redirect: url (含zxt的路径）
	 */
	public static void renderRedirectJson(HttpServletResponse resp, String msg, String url)
	{
		assert resp != null : "renderRedirectJson(HttpServletResponse, String, String)参数resp不能为空！";
		assert !Utils.isBlank(url) : "renderRedirectJson(HttpServletResponse, String, String)参数url不能为空！";
		
		JSONObject o = new JSONObject();
		o.put(ConstantBase.JSON_KEY_OK, Utils.isBlank(msg) ? "成功" : msg);
		o.put(Utils.isBlank(msg) ? ConstantBase.JSON_KEY_REDIRECT : ConstantBase.JSON_KEY_URL, RunSettingImpl.getCODE_ROOT() + url);
		
		render(resp, "application/json;charset=UTF-8", o.toString());
	}
	
	/**
	 * 错误，强制网页进行页面跳转包。<br />
	 * err: msg<br />
	 * redirect: 含zxt的路径
	 */
	public static void renderErrorRedirectJson(HttpServletResponse resp, String errMsg, String url)
	{
		assert resp != null : "renderErrorRedirectJson(HttpServletResponse, String, String)参数resp不能为空！";
		assert !Utils.isBlank(url) : "renderErrorRedirectJson(HttpServletResponse, String, String)参数url不能为空！";
		
		JSONObject o = new JSONObject();
		o.put(ConstantBase.JSON_KEY_ERR, Utils.isBlank(errMsg) ? "错误" : errMsg);
		o.put(ConstantBase.JSON_KEY_URL, url);
		
		render(resp, "application/json;charset=UTF-8", o.toString());
	}
	
	/**
	 * 告警，建议前端更新到与服务器相同的版本（但不强制。<br />
	 * err: msg<br />
	 * redirect: 含zxt的路径<br />
	 * ver: 符合服务器的版本号
	 */
	public static void renderWarnRedirectJson(HttpServletResponse resp, String errMsg, String ver, String url)
	{
		assert resp != null : "renderWarnRedirectJson(HttpServletResponse, String, String)参数resp不能为空！";
		assert !Utils.isBlank(url) : "renderWarnRedirectJson(HttpServletResponse, String, String)参数url不能为空！";
		
		JSONObject o = new JSONObject();
		o.put(ConstantBase.JSON_KEY_WARN, Utils.isBlank(errMsg) ? "注意" : errMsg);
		o.put(ConstantBase.JSON_KEY_URL, url);
		o.put(ConstantBase.JSON_KEY_VER, ver);
		
		render(resp, "application/json;charset=UTF-8", o.toString());
	}
	
	/**
	 * 发送内容。使用UTF-8编码。
	 */
	private static void render(HttpServletResponse resp, String contentType, String text)
	{
		resp.setContentType(contentType);
		resp.setHeader("Pragma", "No-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setDateHeader("Expires", 0);
		try
		{
			resp.getWriter().write(text);
		}
		catch (IOException ex)
		{
			log.error("render() failed", ex);
		}
	}
	
	/**
	 * 发送文本。使用UTF-8编码。
	 * 
	 * @param response
	 *            HttpServletResponse
	 * @param text
	 *            发送的字符串
	 */
	public static void renderText(HttpServletResponse resp, String text)
	{
		render(resp, "text/plain;charset=UTF-8", text);
	}
}