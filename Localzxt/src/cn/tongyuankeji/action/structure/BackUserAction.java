package cn.tongyuankeji.action.structure;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import cn.tongyuankeji.common.cache.McPerson;
import cn.tongyuankeji.common.fileupload.FileUploadManager;
import cn.tongyuankeji.common.parameters.Constant;
import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.common.parameters.EnumUploadType;
import cn.tongyuankeji.common.util.EnumConverterImpl;
import cn.tongyuankeji.manager.structure.BackUserManager;
import cn.tongyuankeji.common.web.GovController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import cn.tongyuankeji.common.annotation.ACLMapping;
import cn.tongyuankeji.common.annotation.ActionArg;
import cn.tongyuankeji.common.annotation.ActionArgs;
import cn.tongyuankeji.common.parameters.EnumPersonState;
import cn.tongyuankeji.common.util.ResponseUtils;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.common.web.ACL;
import cn.tongyuankeji.common.web.SchemaBase;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;

/**
 * 后台 用户管理
 * 
 * @author Jean 2017-05-15 创建
 */
@Controller
public class BackUserAction
{
	@Autowired
	private BackUserManager mgr;
	
	@Autowired
	private GovController orgCtrl;
	
	/**
	 * 后台用户登录
	 * 
	 * @author 代平
	 */
	@RequestMapping(value = "/backLoginAct", method = RequestMethod.POST)
	@ActionArgs(value = {
			@ActionArg(name = "loginId", header = "用户名"),
			@ActionArg(name = "userPwd", header = "密码") // 密文
	})
	public void backLoginAct(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		
		JSONObject userInfo = new JSONObject();
		try
		{
			args = SchemaBase.parseArg(req, this, EnumConverterImpl.getInstance());
			
			userInfo = mgr.firstLogin(resp, (String) args.get("loginId"),
					(String) args.get("userPwd"),
					(Trace) args.get(Trace.LOG_ARG));
			//TODO 张和 0判断是不是首次登录
			/*if (!userInfo.isEmpty())
			{
				userInfo.put("loginId", args.get("loginId"));
				ResponseUtils.renderJson(resp, "成功", userInfo);
				return;
			}*/
			
			mgr.login(resp, (String) args.get("loginId"),
					(String) args.get("userPwd"),
					(Trace) args.get(Trace.LOG_ARG));
			
			// 保存cookie记录用户名，下次登陆时回显上次登录的用户名
			Cookie cookieuser = new Cookie("EduWeb_backUser_auth_key", (String) args.get("loginId"));
			// 设置过期时间为一个月
			cookieuser.setMaxAge(30 * 24 * 3600);
			resp.addCookie(cookieuser);
			
			ResponseUtils.renderSuccessJson(resp, "backHome.ofcx");
		}
		catch (Exception e)
		{
			ResponseUtils.renderJson(resp, e);
		}
	}
	
	/**
	 * 用户登录 后台管理时，取登录人信息：用户名等简略信息、根据权限的菜单
	 * 
	 * @author Daiping
	 * @history Jean 2017-06-17 SchemalBase.validate()换成parseArg()，因为此方法无法配置权限
	 * @history Jean 2017-07-27 默认图片时，返回的路径不应该是/theimages/
	 */
	@RequestMapping(value = "/getUserSummary.ofc", method = RequestMethod.GET)
	public void getUserSummary(HttpServletRequest req, HttpServletResponse resp)
	{
		JSONObject result = null;
		Person loginUser = null;
		
		try
		{
			SchemaBase.parseArg(req, this, EnumConverterImpl.getInstance());
			
			result = new JSONObject();
			loginUser = (Person) req.getAttribute(Person.LOGIN_PERSON);
			result.put("sysId", loginUser.getSysId());
			result.put("userName", loginUser.getUserName());
			
			result.put("role", ACL.role2Menu(loginUser.getRoleId(), loginUser.getOwnerGovId(), "backoffice"));//TODO 张和 1修改了参数
			
			if(result.getJSONObject("role").isEmpty())
				ResponseUtils.renderRedirectJson(resp, "请重新登录！", "/backLogin.html");
			else{
				if (Constant.DEFAULT_THUMB_FILE.equals(loginUser.getThumbFile()))
			{
				result.put("thumbFilePic", FileUploadManager.getAbsoluteFilename(EnumUploadType.thumb, null));
			}
			else
			{
				result.put("thumbFilePic", FileUploadManager.getAbsoluteFilename(EnumUploadType.thumb, loginUser.getThumbFile()));
			}
			
			ResponseUtils.renderJson(resp, "成功", result);
			}
			
		}
		catch (Exception e)
		{
			ResponseUtils.renderJson(resp, e);
		}
	}
	
	/**
	 * 后台管理 注销用户
	 * 
	 * @author 代平  2017-06-17 改到manager中，且不需要配权限，所以调用SchemaBase.parseArg()
	 */
	@RequestMapping(value = "/backLogOff.ofc", method = RequestMethod.GET)
	public void logOff(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		
		try
		{
			args = SchemaBase.parseArg(req, this, EnumConverterImpl.getInstance());
			
			mgr.logoff(req, resp, (Trace) args.get(Trace.LOG_ARG));
			
			ResponseUtils.renderRedirectJson(resp, "成功注销", Constant.URL_SITEUSER_LOGIN);
		}
		catch (Exception ex)
		{
			ResponseUtils.renderJson(resp, ex);
		}
	}
	
	/**
	 * 后台管理，获取人员状态
	 * 
	 * @author 代平
	 * 
	 */
	@RequestMapping(value = "/getPersonDisplayState.ofc", method = RequestMethod.GET)
	public void getPersonDisplayState(HttpServletRequest req, HttpServletResponse resp)
	{
		JSONObject oresult = new JSONObject();
		
		try
		{
			// 状态
			oresult.put("allState", EnumPersonState.displayArray);
			
			ResponseUtils.renderJson(resp, "成功", oresult);
		}
		catch (Exception e)
		{
			ResponseUtils.renderJson(resp, e);
		}
	}
	
	/**
	 * 后台管理，获取用户列表
	 * 
	 * @author 代平
	 * 
	 */
	@RequestMapping(value = "/getUserTableDict.ofc", method = RequestMethod.GET)
	public void getUserTableDict(HttpServletRequest req, HttpServletResponse resp)
	{
		JSONObject oresult = new JSONObject();
		
		try
		{
			// 园区
			oresult.put("allGov", orgCtrl.getVisibleGov((McPerson) req.getAttribute(Person.LOGIN_PERSON), true, null));
			
			// 状态
			oresult.put("allState", EnumPersonState.displayArray);
			
			ResponseUtils.renderJson(resp, "成功", oresult);
		}
		catch (Exception e)
		{
			ResponseUtils.renderJson(resp, e);
		}
	}
	
	/**
	 * 后台管理，获取用户列表
	 * 
	 * @author 代平
	 * 
	 */
	@RequestMapping(value = "/getUserInfosTable.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "structure", page = "user", opr = "view")
	@ActionArgs(value = {
			@ActionArg(name = "ownerGovId", header = "所属园区", target_type = Integer.class),
			@ActionArg(name = "state", header = "用户状态", target_type = EnumPersonState.class),
			@ActionArg(name = "leafDeptIds[]", header = "末级部门ID", target_type = Integer[].class, required = false),
			@ActionArg(name = "fuzzy", required = false, header = "用户名，或注册手机", range = { "", "100" }),
			@ActionArg(name = "pageStart", target_type = Integer.class)
	})
	public void getUserInfosTable(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		
		try
		{
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());
			
			ResponseUtils.renderJson(resp, "成功",
					mgr.getByProperty(
							(McPerson) req.getAttribute(Person.LOGIN_PERSON),
							(Integer) args.get("ownerGovId"),
							(EnumPersonState) args.get("state"),
							(String) args.get("fuzzy"),
							(Integer) args.get("pageStart")));
		}
		catch (Exception e)
		{
			ResponseUtils.renderJson(resp, e);
		}
	}
	/**
	 * 后台管理-组织机构-改变用户状态
	 */
	@RequestMapping(value = "/changeUserState.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "structure", page = "user", opr = "edit")
	@ActionArgs(value = {
			@ActionArg(name = "sysId", header = "用户id", required = true, target_type = Integer.class),
			@ActionArg(name = "state", required = true, header = "用户状态", target_type = EnumPersonState.class)
	})
	public void changeUserState(HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> args = null;
		int sysId = 0;
		McPerson loginUser = null;
		
		try
		{
			args = SchemaBase.validate(request, this, EnumConverterImpl.getInstance());
			loginUser = (McPerson) request.getAttribute(Person.LOGIN_PERSON);
			
			sysId = (Integer) args.get("sysId");
			
			mgr.changeState(
					loginUser,
					sysId,
					(EnumPersonState) args.get("status"),
					(Trace) args.get(Trace.LOG_ARG));
			
		}
		catch (Exception ex)
		{
			ResponseUtils.renderJson(response, ex);
		}
	}
	
	
	/**
	 * 后台管理，获取用户信息 编辑
	 * 
	 * @author 代平
	 */
	@RequestMapping(value = "/getUserInfoEdit.ofc", method = RequestMethod.GET)
	@ACLMapping(subsystem = "backoffice", module = "structure", page = "user", opr = "view")
	@ActionArgs(value = {
			@ActionArg(name = "userId", target_type = Integer.class)
	})
	public void getUserInfoEdit(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		
		try
		{
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());
			
			ResponseUtils.renderJson(resp, "成功",
					mgr.getById((McPerson) req.getAttribute(Person.LOGIN_PERSON), (Integer) args.get("userId")));
		}
		catch (Exception e)
		{
			ResponseUtils.renderJson(resp, e);
		}
	}
	
	
	/**
	 * (私企用户) 用户修改密码
	 */
	@RequestMapping(value = "/saveUserModifyPwd.ofc", method = RequestMethod.POST)
	@ActionArgs(value = {
			@ActionArg(name = "userPwd", header = "当前密码"), // 密文
			@ActionArg(name = "newUserPwd", header = "新密码", range = { "6", "20" }, regex = Utils.PASSWORD_PATTERN) // 明文
	})
	public void saveUserModifyPwd(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		
		try
		{
			args = SchemaBase.parseArg(req, this, EnumConverterImpl.getInstance());
			
			mgr.updatePwd(
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
	 * 第一次登录用户修改密码
	 * 
	 * @param oldPwdEncrypted
	 *            现有密码（密文）
	 * @param newPwdEncrypted
	 *            新密码（密文）
	 * 
	 * @author 代平
	 */
	@RequestMapping(value = "/firstSaveUserModifyPwd.htm", method = RequestMethod.POST)
	@ActionArgs(value = {
			@ActionArg(name = "userPwd", header = "当前密码"), // 密文
			@ActionArg(name = "newUserPwd", header = "新密码", range = { "6", "20" }, regex = Utils.PASSWORD_PATTERN), // 明文
			@ActionArg(name = "loginId", header = "登录名", target_type = String.class)
	})
	public void firstSaveUserModifyPwd(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		
		try
		{
			args = SchemaBase.parseArg(req, this, EnumConverterImpl.getInstance());
			
			mgr.firstUpdatePwd(
					(String) args.get("loginId"),
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
	
}
