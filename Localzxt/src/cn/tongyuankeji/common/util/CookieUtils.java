package cn.tongyuankeji.common.util;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.tongyuankeji.common.util.Utils;

/**
 * Cookie 工具类
 * 
 * @author 代平 2017-05-02 创建
 */
public class CookieUtils
{
	/**
	 * 获得cookie
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param name
	 *            cookie name
	 * @return if exist return cookie, else return null.
	 */
	public static Cookie getCookie(HttpServletRequest req, String name)
	{
		assert req != null : "getCookie(HttpServletRequest, String)参数req不能为空！";
		assert !Utils.isBlank(name) : "getCookie(HttpServletRequest, String)参数name不能为空！";
		
		Cookie[] cookies = req.getCookies();
		if (cookies != null && cookies.length > 0)
		{
			for (Cookie c : cookies)
			{
				if (c.getName().equals(name))
				{
					return c;
				}
			}
		}
		return null;
	}

	/**
	 * 添加或删除 Cookie。删除时，设置maxAge=1
	 * 
	 * @param response
	 * 
	 * @param name
	 *            名称
	 * @param value
	 *            内容
	 * @param MaxAge
	 *            过期时间（秒）。传入null时，永远不过期
	 */
	public static void addCookie(HttpServletResponse resp, String name, String value, Integer maxAge)
	{
		assert resp != null : "addCookie(HttpServletResponse, String, String, Integer)参数resp不能为空！";
		assert !Utils.isBlank(name) : "addCookie(HttpServletResponse, String, String, Integer)参数name不能为空！";
		
		Cookie cookie = new Cookie(name, value);

		if (maxAge != null)
			cookie.setMaxAge(maxAge);
		resp.addCookie(cookie);
	}

	/**
	 * 将Response中Cookie失效
	 */
	public static void deleteCookie(HttpServletResponse resp, Cookie c)
	{
		assert resp != null : "deleteCookie(HttpServletResponse, Cookie)参数resp不能为空！";
		assert c != null : "deleteCookie(HttpServletResponse, Cookie)参数c不能为空！";
		
		c.setMaxAge(1);
		resp.addCookie(c);
	}

	/**
	 * 将Request中名为name的Cookie失效
	 * 
	 * @param name
	 *            Cookie名称
	 */
	public static void deleteCookie(HttpServletRequest req, HttpServletResponse resp, String name)
	{
		assert req != null : "deleteCookie(HttpServletRequest, HttpServletResponse, String)参数req不能为空！";
		assert resp != null : "deleteCookie(HttpServletRequest, HttpServletResponse, String)参数resp不能为空！";
		assert !Utils.isBlank(name) : "deleteCookie(HttpServletRequest, HttpServletResponse, String)参数name不能为空！";
		
		Cookie c = CookieUtils.getCookie(req, name);
		if (c != null)
			CookieUtils.deleteCookie(resp, c);
	}

	/**
	 * 检查Request中是否有名为name，且值为value的Cookie
	 * 
	 * @param name
	 *            Cookie名称
	 * @param value
	 *            对比值
	 */
	public static boolean hasCookie(HttpServletRequest req, String name, String value)
	{
		assert req != null : "hasCookie(HttpServletRequest, String, String)参数req不能为空！";
		assert !Utils.isBlank(name) : "hasCookie(HttpServletRequest, String, String)参数name不能为空！";
		assert !Utils.isBlank(value) : "hasCookie(HttpServletRequest, String, String)参数value不能为空！";
		
		Cookie cookie = CookieUtils.getCookie(req, name);
		if (cookie == null)
			return false;

		if (!Utils.isBlank(value))
			return value.equals(cookie.getValue());
		else
			return true;
	}

	/**
	 * 获取名为name的Cookie值
	 * 
	 * @param name
	 *            Cookie名称
	 */
	public static String getCookieValue(HttpServletRequest req, String name)
	{
		assert req != null : "getCookieValue(HttpServletRequest, String)参数req不能为空！";
		assert !Utils.isBlank(name) : "getCookieValue(HttpServletRequest, String)参数name不能为空！";
		
		Cookie cookie = CookieUtils.getCookie(req, name);
		if (cookie != null)
			return cookie.getValue();
		else
			return null;
	}
}