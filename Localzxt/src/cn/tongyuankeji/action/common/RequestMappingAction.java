package cn.tongyuankeji.action.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import cn.tongyuankeji.common.cache.XmcManagerImpl;
import cn.tongyuankeji.common.parameters.Constant;
import cn.tongyuankeji.common.parameters.EnumLogOperatorKind;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.common.web.ACL;
import cn.tongyuankeji.entity.Person;

@Controller
public class RequestMappingAction
{
	
	private String checkLogin(HttpServletRequest req, EnumLogOperatorKind operatorKind)
	{
		boolean foundCaller = false;
		String actionMethodName = null;
		StackTraceElement[] stacktrace = null;
		Person p = (Person) XmcManagerImpl.getByAuthKey(req);
		
		if (p == null)
		{
			if (operatorKind == EnumLogOperatorKind.user)
			{
				return Constant.COMMON_LOGIN;
			}
			else
			{
				return Constant.BACKMANAGE_PATH + "backLogin";
			}
		}
		
		// 用户的需要按role检查权�?????
		if (operatorKind == EnumLogOperatorKind.backuser)
		{
			// 角色
			if (p.getRoleId() == null)
				return Constant.COMMON_PATH + "noPermission";
			
			// travesal call stack to get calling method name
			stacktrace = Thread.currentThread().getStackTrace();
			for (int i = 0; i < stacktrace.length; i++)
			{
				if (foundCaller)
				{
					actionMethodName = stacktrace[i].getMethodName();
					break;
				}
				
				if (stacktrace[i].getMethodName() == "checkLogin")
					foundCaller = true;
			}
			
			// 登录进入的主页面，不检查权�?????
			if (actionMethodName.equals("backHome"))
				return null;
			
			// 检查权�?????
			if (Utils.isBlank(ACL.checkACL(p, RequestMappingAction.class, actionMethodName, true)))
				return null;
		}
		
		return null;
	}
	
	/*---------------------------------- appweb公共的页�????? htm ----------------------------------*/
	@RequestMapping(value = "/index")
	public String hello(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		return "/index";
	}
	
	//前台用户公共登录页面，根据用户所使用设备跳转到不同的登录页面
	@RequestMapping(value = "/commonLogin")
	public String commonLogin(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		return "/commonLogin";
	}
	
	@RequestMapping(value = "/login.ofc")
	public String login(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		return Constant.APPWEB_PATH + "/login";
	}
	
	/*---------------------------------- pcweb公共的页�????? htm ----------------------------------*/
	@RequestMapping(value = "/userLogin")
	public String userLogin(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		return Constant.PCWEB_PATH + "/userLogin";
	}
	
	@RequestMapping(value = "/header")
	public String header(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		return Constant.PCWEB_PATH + "/header";
	}
	
	@RequestMapping(value = "/footer")
	public String footer(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		return Constant.PCWEB_PATH + "/footer";
	}
	
	@RequestMapping(value = "/contact")
	public String contact(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.user);
		if (loginUrl != null)
			return loginUrl;
		return Constant.PCWEB_PATH + "/contact";
	}
	
	@RequestMapping(value = "/userIndex.html")
	public String userIndex(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.user);
		if (loginUrl != null)
			return loginUrl;
		return Constant.PCWEB_PATH + "/userIndex";
	}
	
	@RequestMapping(value = "/searchInfo.html")
	public String searchInfo(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.user);
		if (loginUrl != null)
			return loginUrl;
		return Constant.PCWEB_PATH + "/searchInfo";
	}
	
	@RequestMapping(value = "/infomation.html")
	public String infomation(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.user);
		if (loginUrl != null)
			return loginUrl;
		return Constant.PCWEB_PATH + "/infomation";
	}
	@RequestMapping("/information.html")
	public String information(HttpServletRequest req, HttpServletResponse resp, ModelMap model){
		String loginUrl = checkLogin(req, EnumLogOperatorKind.user);
		if (loginUrl != null)
			return loginUrl;
		return Constant.PCWEB_PATH + "/userIndex";
	}
	
	@RequestMapping("/pcLogin.html")
	public String pcLogin(HttpServletRequest req, HttpServletResponse resp, ModelMap model){
		return Constant.PCWEB_PATH + "/userLogin";
	}
	
	@RequestMapping(value="/addNew.html")
	public String addNews(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.user);
		if (loginUrl != null)
			return loginUrl;
		return Constant.PCWEB_PATH + "/newsForm";
	}
	
	@RequestMapping(value = "/newsIndex.html")
	public String newsIndex(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.user);
		if (loginUrl != null)
			return loginUrl;
		return Constant.PCWEB_PATH + "/newsIndex";
	}
	
	@RequestMapping(value = "/newsContent.html")
	public String newsContent(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.user);
		if (loginUrl != null)
			return loginUrl;
		return Constant.PCWEB_PATH + "/newsContent";
	}
	
	@RequestMapping(value = "/personCenter.html")
	public String personCenter(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.user);
		if (loginUrl != null)
			return loginUrl;
		
		return Constant.PCWEB_PATH + "/personCenter";
	}
	
	/*---------------------------------- backoffice公共的页�???? htm ----------------------------------*/
	@RequestMapping(value = "/backLogin")
	public String backLogin(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		return Constant.BACKMANAGE_PATH + "/backLogin";
	}
	/*---------------------------------- 后台登陆页面ofc ----------------------------------*/
	@RequestMapping(value = "/backHome.ofcx")
	public String backindex(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.backuser);
		if (loginUrl != null)
			return loginUrl;
		
		return Constant.BACKMANAGE_PATH + "/index";
	}
	/*---------------------------------- 后台新闻index页面 ----------------------------------*/
	@RequestMapping(value="/getBackNewsList.ofcx")
	public String backNewsIndex(HttpServletRequest request,HttpServletResponse response){
		String loginUrl = checkLogin(request, EnumLogOperatorKind.backuser);
		if (loginUrl != null)
			return loginUrl;
		return Constant.BACKMANAGE_PATH + "/backNewsIndex";
	}
	
	@RequestMapping(value = "/infoManage.ofcx")
	public String infoManage(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.backuser);
		if (loginUrl != null)
			return loginUrl;
		
		return Constant.BACKMANAGE_PATH + "/infoManage";
	}
	
	//信息列表�??
	@RequestMapping(value = "/informationList.ofcx")
	public String informationList(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.backuser);
		if (loginUrl != null)
			return loginUrl;
		
		return Constant.BACKMANAGE_PATH + "/informationList";
	}
	//编辑信息页面
	@RequestMapping(value = "/infoDetail.ofcx")
	public String infoDetail(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.backuser);
		if (loginUrl != null)
			return loginUrl;
		
		return Constant.BACKMANAGE_PATH + "/editinformation";
	}
	//预览信息页面
	@RequestMapping(value = "/infomationReview.ofcx")
	public String infomationReview(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.backuser);
		if (loginUrl != null)
			return loginUrl;
		return Constant.PCWEB_PATH + "/infomation";
	}
	@RequestMapping(value = "/siteManage.ofcx")
	public String siteManage(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.backuser);
		if (loginUrl != null)
			return loginUrl;
		
		return Constant.BACKMANAGE_PATH + "/siteManage";
	}
	
	@RequestMapping(value = "/sysAnnounce.ofcx")
	public String sysAnnounce(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.backuser);
		if (loginUrl != null)
			return loginUrl;
		
		return Constant.BACKMANAGE_PATH + "/sysAnnounce";
	}
	@RequestMapping(value = "/sysLog.ofcx")
	public String sysLog(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.backuser);
		if (loginUrl != null)
			return loginUrl;
		
		return Constant.BACKMANAGE_PATH + "/sysLog";
	}
	
	@RequestMapping(value = "/userManage.ofcx")
	public String userManage(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.backuser);
		if (loginUrl != null)
			return loginUrl;
		
		return Constant.BACKMANAGE_PATH + "/userManage";
	}
	
	@RequestMapping(value = "/permission.ofcx")
	public String permission(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.backuser);
		if (loginUrl != null)
			return loginUrl;
		
		return Constant.BACKMANAGE_PATH + "/backPermission";
	}
	
	@RequestMapping(value = "/addRoleInfo.ofcx")
	public String addRoleInfo(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.backuser);
		if (loginUrl != null)
			return loginUrl;
		
		return Constant.BACKMANAGE_PATH + "/backAddRole";
	}
	
	@RequestMapping(value = "/editRoleInfo.ofcx")
	public String editRoleInfo(HttpServletRequest req, HttpServletResponse resp, ModelMap model)
	{
		String loginUrl = checkLogin(req, EnumLogOperatorKind.backuser);
		if (loginUrl != null)
			return loginUrl;
		
		return Constant.BACKMANAGE_PATH + "/editBackRole";
	}
}
