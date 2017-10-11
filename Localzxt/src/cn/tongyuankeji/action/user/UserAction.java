package cn.tongyuankeji.action.user;


import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.tongyuankeji.common.annotation.ActionArg;
import cn.tongyuankeji.common.annotation.ActionArgs;
import cn.tongyuankeji.common.parameters.Constant;
import cn.tongyuankeji.common.util.EnumConverterImpl;
import cn.tongyuankeji.common.util.ResponseUtils;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.common.web.SchemaBase;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;
import cn.tongyuankeji.manager.user.UserManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author 代平 2017-05-06 创建
 * @history 代平 2017-11-07 将手机app功能移到AppAction中
 */
@Controller
public class UserAction
{
	@Autowired
	private UserManager mgr;
	
	
	public final static String FIND_PWD_STU = "_find_pwd_user";
	
	
	/********************************** 登录、注销 **********************************/
	/**
	 * 保存 用户登录
	 * 
	 * @author 代平
	 * */
	@RequestMapping(value = "/userLoginAct", method = RequestMethod.POST)
	@ActionArgs(value = {
			@ActionArg(name = "loginId", header = "登录账号", range = { "1", "100" }),
			@ActionArg(name = "passWord", header = "密码") // 密文
	})
	public void userLoginAct(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		try
		{
			// 获取参数
			args = SchemaBase.parseArg(req, this,
					EnumConverterImpl.getInstance());
			mgr.login(req,resp, (String) args.get("loginId"),
					(String) args.get("passWord"),
					(Trace) args.get(Trace.LOG_ARG));
			// 保存cookie记录用户名
			Cookie cookieuser = new Cookie("zxt_user_auth_key", (String) args.get("loginId"));
			// 设置过期时间为一个月
			cookieuser.setMaxAge(30 * 24 * 3600);
			resp.addCookie(cookieuser);
			Cookie cookieIsLogin = new Cookie("user_is_login", "1");
			resp.addCookie(cookieIsLogin);
			ResponseUtils.renderSuccessJson(resp, "登录成功");
		}
		catch (Exception ex)
		{
			ResponseUtils.renderJson(resp, ex);
		}
	}
	
	/**
	 * 注销 用户
	 * 
	 * @author 代平
	 */
	@RequestMapping(value = "/userLogOff.htm", method = RequestMethod.GET)
	public void userLogOff(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		
		try
		{
			args = SchemaBase.parseArg(req, this, EnumConverterImpl.getInstance());
			
			mgr.logoff(req, resp, (Trace) args.get(Trace.LOG_ARG));
			// 删除判断是否登陆的cookie
			Cookie cookie = new Cookie("user_is_login", null);
			cookie.setMaxAge(0);
			resp.addCookie(cookie);
			ResponseUtils.renderRedirectJson(resp, "成功注销", Constant.URL_USER_LOGIN);
		}
		catch (Exception ex)
		{
			ResponseUtils.renderJson(resp, ex);
		}
	}
	
	/**
	 * 获取用户信息 编辑
	 */
	@RequestMapping(value = "/getUserInfoEdit.htm", method = RequestMethod.GET)
	public void getUserInfoEdit(HttpServletRequest req,
			HttpServletResponse resp)
	{
		try
		{
			Person p = (Person) req.getAttribute(Person.LOGIN_PERSON);
			ResponseUtils.renderJson(resp, "成功", mgr.getById(p));
		}
		catch (Exception e)
		{
			ResponseUtils.renderJson(resp, e);
		}
	}
	
	/**
	 * 保存 用户修改密码
	 * 
	 * @author 代平
	 */
	@RequestMapping(value = "/saveUserModifyPwd.htm", method = RequestMethod.POST)
	@ActionArgs(value = {
			@ActionArg(name = "userPwd", header = "密码"), // 密文
			@ActionArg(name = "newUserPwd", header = "新密码", range = { "6", "20" }, regex = Utils.PASSWORD_PATTERN) })
	// 明文
	public void saveUserModifyPwd(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		
		try
		{
			// 获取页面传递参数
			args = SchemaBase.parseArg(req, this,
					EnumConverterImpl.getInstance());
			
			mgr.changePassword(
					(Person) req.getAttribute(Person.LOGIN_PERSON),
					(String) args.get("userPwd"),
					(String) args.get("newUserPwd"),
					(Trace) args.get(Trace.LOG_ARG));
			
			ResponseUtils.renderSuccessJson(resp, "保存成功");
		}
		catch (Exception ex)
		{
			ResponseUtils.renderJson(resp, ex);
		}
	}
	
	/**
	 * 获取用户简略信息：header，个人中心右上
	 * 
	 * @author 代平
	 */
	@RequestMapping(value = "/getUserSummary.htm", method = RequestMethod.GET)
	public void getUserSummary(HttpServletRequest req, HttpServletResponse resp)
	{
		try
		{
			Person person = (Person) req.getAttribute(Person.LOGIN_PERSON);
			ResponseUtils.renderJson(resp, "成功", JSONObject.fromObject(person));
		}
		catch (Exception e)
		{
			ResponseUtils.renderJson(resp, e);
		}
	}
	
	
	/**
	 * 获取用户详细信息：个人中心信息修改页数据
	 * 
	 * @author 代平
	 */
	@RequestMapping(value = "/getUserInfo.htm", method = RequestMethod.GET)
	public void getUserInfo(HttpServletRequest req, HttpServletResponse resp)
	{
		try
		{
			Person person = (Person) req.getAttribute(Person.LOGIN_PERSON);
			ResponseUtils.renderJson(resp, "成功", mgr.getById(person));
		}
		catch (Exception e)
		{
			ResponseUtils.renderJson(resp, e);
		}
	}
	
	/**
	 * 保存用户基本信息
	 * 
	 * @author 代平
	 */
	@RequestMapping(value = "/saveUser.htm", method = RequestMethod.POST)
	@ActionArgs(value = {
			@ActionArg(name = "userName", header = "姓名"), 
			@ActionArg(name = "mobileNumber", header = "手机号", regex=Utils.MOBILE_PATTERN, regexFailMsg="手机号填写错误"),
			@ActionArg(name = "email", header = "邮箱", regex=Utils.EMAIL_PATTERN, regexFailMsg="电子邮箱填写错误", required =false), 
			@ActionArg(name = "title", header = "公司名称"),  
			@ActionArg(name = "companyCode", header = "机构代码证", required =false), 
			@ActionArg(name = "officPhone", header = "公司电话", regex=Utils.TELEPHONE_PATTERN, regexFailMsg="公司电话填写错误", required =false), 
			@ActionArg(name = "address", header = "公司地址", required =false)
	})
	public void saveUser(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		
		try
		{
			// 获取页面传递参数
			args = SchemaBase.parseArg(req, this,
					EnumConverterImpl.getInstance());
			Person person = (Person) req.getAttribute(Person.LOGIN_PERSON);
			mgr.saveUserInfo(
					req, resp,
					person, 
					(String)args.get("userName"), 
					(String)args.get("mobileNumber"), 
					(String)args.get("email"), 
					(String)args.get("title"), 
					(String)args.get("companyCode"), 
					(String)args.get("officPhone"), 
					(String)args.get("address"));
			ResponseUtils.renderSuccessJson(resp, "成功");
		}
		catch (Exception e)
		{
			ResponseUtils.renderJson(resp, e);
		}
	}
	
	/**
	 * 保存用户定制消息口
	 * 
	 * @author 代平
	 */
	@RequestMapping(value = "/saveDataType.htm", method = RequestMethod.POST)
	@ActionArgs(value = {
			@ActionArg(name = "dataTypeIds[]", header = "定制消息口", target_type = Integer[].class, required = false)
	})
	public void saveDataType(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		
		try
		{
			// 获取页面传递参数
			args = SchemaBase.parseArg(req, this,
					EnumConverterImpl.getInstance());
			Person person = (Person) req.getAttribute(Person.LOGIN_PERSON);
			mgr.saveUserDataType(
					req, resp,
					person, 
					(Integer[])args.get("dataTypeIds[]"));
			ResponseUtils.renderSuccessJson(resp, "成功");
		}
		catch (Exception e)
		{
			ResponseUtils.renderJson(resp, e);
		}
	}
	
	@RequestMapping(value="/getUserDateType.htm",method = RequestMethod.POST)
	public void getUserDataType(HttpServletRequest request,HttpServletResponse response){
		try {
			Person person = (Person) request.getAttribute(Person.LOGIN_PERSON);
			JSONArray jsonArray = mgr.getUserDataTypeByUserId(person.getSysId(), false);
			ResponseUtils.renderJson(response, "成功", jsonArray);
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
	}
	
	@RequestMapping(value = "/deleteUser.htm", method = RequestMethod.GET)
	public void deleteUser(HttpServletRequest req, HttpServletResponse resp)
	{
		try
		{
			mgr.deleteUser(2);
			ResponseUtils.renderJson(resp, "成功", mgr.getByLoginId("user_001"));
		}
		catch (Exception e)
		{
			ResponseUtils.renderJson(resp, e);
		}
	}
}
