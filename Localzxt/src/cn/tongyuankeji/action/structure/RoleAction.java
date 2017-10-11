package cn.tongyuankeji.action.structure;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import cn.tongyuankeji.common.util.EnumConverterImpl;
import cn.tongyuankeji.manager.structure.RoleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.tongyuankeji.common.annotation.ACLMapping;
import cn.tongyuankeji.common.annotation.ActionArg;
import cn.tongyuankeji.common.annotation.ActionArgs;
import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.common.parameters.EnumMgrReturnType;
import cn.tongyuankeji.common.util.ResponseUtils;
import cn.tongyuankeji.common.web.SchemaBase;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;

/**
 * 后台管理 角色
 * 
 * @author Jean 2015-05-06 创建
 */
@Controller
public class RoleAction
{
	@Autowired
	private RoleManager mgr;
	
	// 查看所有角色列表
	@RequestMapping(value = "/getRoleTable.ofc", method = RequestMethod.GET)
	@ACLMapping(subsystem = "backoffice", module = "structure", page = "role", opr = "view")
	@ActionArgs(value = {
			@ActionArg(name = "statusMin", header = "角色状态", target_type = EnumGenericState.class)
	})
	public void getRoleTable(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		
		try
		{
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());
			
			ResponseUtils.renderJson(resp, "成功",
					(JSONArray) mgr.getByState(EnumMgrReturnType.jsonArray, (EnumGenericState) args.get("statusMin"), true));
		}
		catch (Exception ex)
		{
			ResponseUtils.renderJson(resp, ex);
		}
	}
	
	// 查看某个角色详情
	@RequestMapping(value = "/getRoleInfo.ofc", method = RequestMethod.GET)
	@ACLMapping(subsystem = "backoffice", module = "structure", page = "role", opr = "view")
	@ActionArgs(value = {
			@ActionArg(name = "sysId", target_type = Integer.class)
	})
	public void getRoleInfo(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		
		try
		{
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());
			
			ResponseUtils.renderJson(resp, "成功",
					mgr.getById(
							(Integer) args.get("sysId")));
		}
		catch (Exception ex)
		{
			ResponseUtils.renderJson(resp, ex);
		}
	}
	
	// 新建角色返回供勾选的母版
	@RequestMapping(value = "/getRoleMask.ofc", method = RequestMethod.GET)
	@ACLMapping(subsystem = "backoffice", module = "structure", page = "role", opr = "edit")
	@ActionArgs(value = {
			@ActionArg(name = "isMaster", header = "是平台权限", target_type = Boolean.class)
	})
	public void getRoleMask(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		
		try
		{
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());
			
			ResponseUtils.renderJson(resp, "成功", mgr.getMask((Boolean) args.get("isMaster"), false));
		}
		catch (Exception ex)
		{
			ResponseUtils.renderJson(resp, ex);
		}
	}
	
	
	// 新建角色 保存
	@RequestMapping(value = "/createRole.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "structure", page = "role", opr = "edit")
	@ActionArgs(value = {
			@ActionArg(name = "isBird", header = "是平台权限", target_type = Boolean.class),
			@ActionArg(name = "titleZh", header = "角色名称（中文）", range = { "1", "40" }),
			@ActionArg(name = "titleEn", required = false, header = "角色名称（英文）", range = { "1", "40" }),
			@ActionArg(name = "ACL"),
			@ActionArg(name = "remarks", required = false, header = "说明", range = { "", "100" })
	})
	public void createRole(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		int sysId = 0;
		
		try
		{
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());
			
			sysId = mgr.create(
					(Boolean) args.get("isBird"),
					(String) args.get("titleZh"),
					(String) args.get("titleEn"),
					(String) args.get("ACL"),
					(String) args.get("remarks"),
					(Person) req.getAttribute(Person.LOGIN_PERSON),
					(Trace) args.get(Trace.LOG_ARG));
			
			ResponseUtils.renderJson(resp, "保存成功", mgr.getById(sysId));
		}
		catch (Exception ex)
		{
			ResponseUtils.renderJson(resp, ex);
		}
	}
	
	// 编辑角色 保存
	@RequestMapping(value = "/updateRole.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "structure", page = "role", opr = "edit")
	@ActionArgs(value = {
			@ActionArg(name = "sysId", target_type = Integer.class),
			@ActionArg(name = "titleZh", header = "角色名称（中文）", range = { "1", "40" }),
			@ActionArg(name = "titleEn", required = false, header = "角色名称（英文）", range = { "1", "40" }),
			@ActionArg(name = "ACL"),
			@ActionArg(name = "remarks", required = false, header = "说明", range = { "", "100" })
	})
	public void updateRole(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		int sysId = 0;
		
		try
		{
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());
			sysId = (Integer) args.get("sysId");
			
			mgr.update(
					sysId,
					(String) args.get("titleZh"),
					(String) args.get("titleEn"),
					(String) args.get("ACL"),
					(String) args.get("remarks"),
					(Person) req.getAttribute(Person.LOGIN_PERSON),
					(Trace) args.get(Trace.LOG_ARG));
			
			ResponseUtils.renderJson(resp, "保存成功", mgr.getById(sysId));
		}
		catch (Exception ex)
		{
			ResponseUtils.renderJson(resp, ex);
		}
	}
	
	// 修改状态
	@RequestMapping(value = "/changeRoleStatus.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "structure", page = "role", opr = "edit")
	@ActionArgs(value = {
			@ActionArg(name = "sysId", header = "角色ID", target_type = Integer.class),
			@ActionArg(name = "toStatus", header = "设置为什么状态", target_type = EnumGenericState.class)
	})
	public void changeRoleStatus(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;
		int sysId = 0;
		
		try
		{
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());
			sysId = (Integer) args.get("sysId");
			
			mgr.changeState(
					sysId,
					(EnumGenericState) args.get("toStatus"),
					(Trace) args.get(Trace.LOG_ARG));
			
			// ResponseUtils.renderJson(resp, "成功", mgr.getById(sysId));
			
		}
		catch (Exception ex)
		{
			ResponseUtils.renderJson(resp, ex);
		}
	}
	
}
