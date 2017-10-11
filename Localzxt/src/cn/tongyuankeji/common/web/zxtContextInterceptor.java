package cn.tongyuankeji.common.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import cn.tongyuankeji.common.cache.McPerson;
import cn.tongyuankeji.common.cache.XmcManagerImpl;
import cn.tongyuankeji.common.parameters.Constant;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.common.util.RequestUtils;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.common.web.RunSettingImpl;
import cn.tongyuankeji.entity.Person;

public class zxtContextInterceptor extends HandlerInterceptorAdapter
{
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception
	{
		// TODO: 代平 remove when publish
		System.out.println("--------- query page: " + RequestUtils.getUri(req));
		
		McPerson mcp = null;
		String ext = RequestUtils.getUriPattern(req);// 请求后缀;
		String getUriName = RequestUtils.getUriName(req);//请求名称;
		
		if (ext == null  || getUriName.equals("index") || getUriName.equals("500")
				|| getUriName.equals("404") || getUriName.equals("403"))// 不需要登录的页面，如果还是有登录人，尝试刷新ping时刻
		{
			 if ((mcp = XmcManagerImpl.getByAuthKey(req)) != null)
				 req.setAttribute(Person.LOGIN_PERSON, mcp);
			
			return true;
		}
		else if (ext.equals("ofcx") || ext.equals("html"))
		{
			return true;
		}
		else if (ext.equals("ofc") || ext.equals("htm"))
		{
			// 还未登录
			if (Utils.isBlank(XmcManagerImpl.getAuthKey(req)))
			{
				renderLogin(resp, ext.equals("htm"));
				return false;
			}
			
			// 从xmc获取到的登录人，最近ping时刻已被刷新
			mcp = XmcManagerImpl.refreshByAuthKey(req, resp, ext.equals("htm"));
			if (mcp == null)
			{
				renderLogin(resp, ext.equals("htm"));
				return false;
			}
			
			req.setAttribute(Person.LOGIN_PERSON, mcp);
			return true;
		}
		return false;
	}
	
	private void renderLogin(HttpServletResponse response, boolean isStudent)
	{
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		
		JSONObject err = new JSONObject();
		err.put(ConstantBase.JSON_KEY_OK, "请登录");
		err.put(ConstantBase.JSON_KEY_URL, RunSettingImpl.getCODE_ROOT() + (isStudent ? Constant.COMMON_LOGIN : Constant.URL_SITEUSER_LOGIN));
		
		try
		{
			response.getWriter().write(err.toString());
		}
		catch (IOException e)
		{
		}
		
		// try
		// {
		// response.sendRedirect(RunSettingImpl.getCODE_ROOT() + (isStudent ? Constant.URL_STUDENT_LOGIN :
		// Constant.URL_USER_LOGIN));
		// }
		// catch (IOException ex)
		// {
		// }
	}
}
