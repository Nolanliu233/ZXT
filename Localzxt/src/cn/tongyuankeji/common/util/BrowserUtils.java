package cn.tongyuankeji.common.util;


import javax.servlet.http.HttpServletRequest;

import nl.bitwalker.useragentutils.UserAgent;

import cn.tongyuankeji.common.encoder.Md5PwdEncoder;

/**
 * 验证浏览器基本信息
 * 
 * @author 代平 2017-05-02 创建
 */
public class BrowserUtils
{
	/**
	 * @Description 获取浏览器客户端信息签名值
	 */
	public static String getUserAgent(HttpServletRequest request, String value)
	{
		assert request != null : "getUserAgent(HttpServletRequest, String)参数request不能为空！";
		assert !Utils.isBlank(value) : "getUserAgent(HttpServletRequest, String)参数value不能为空！";
		
		StringBuffer sf = new StringBuffer();
		sf.append(value);
		sf.append("-");
		/**
		 * 混淆浏览器版本信息
		 */
		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("user-agent"));
		sf.append(userAgent.getBrowser());
		sf.append("-");
		sf.append(userAgent.getBrowserVersion());

		/**
		 * MD5 浏览器版本信息
		 */
		return Md5PwdEncoder.toMD5(sf.toString());
	}

	/**
	 * @Description 请求浏览器是否合法
	 *              (只校验客户端信息不校验domain)
	 * @param userAgent
	 *            浏览器客户端信息
	 */
	public static boolean isLegalUserAgent(HttpServletRequest request, String value, String userAgent)
	{
		assert request != null : "isLegalUserAgent(HttpServletRequest, String, String)参数request不能为空！";
		assert !Utils.isBlank(value) : "isLegalUserAgent(HttpServletRequest, String, String)参数value不能为空！";
		assert !Utils.isBlank(userAgent) : "isLegalUserAgent(HttpServletRequest, String, String)参数userAgent不能为空！";
		
		String rlt = getUserAgent(request, value);

		if (rlt.equalsIgnoreCase(userAgent))
			return true;

		return false;
	}
}
