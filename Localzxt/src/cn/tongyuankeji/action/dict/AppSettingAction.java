package cn.tongyuankeji.action.dict;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.tongyuankeji.common.util.EnumConverterImpl;
import cn.tongyuankeji.manager.dict.AppSettingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.tongyuankeji.common.annotation.ACLMapping;
import cn.tongyuankeji.common.annotation.ActionArg;
import cn.tongyuankeji.common.annotation.ActionArgs;
import cn.tongyuankeji.common.util.ResponseUtils;
import cn.tongyuankeji.common.web.SchemaBase;
import cn.tongyuankeji.entity.Trace;

/**
 * 后台 系统配置
 * 
 * @author 代平  2017-05-16 创建
 */
@Controller
public class AppSettingAction
{
	@Autowired
	private AppSettingManager mgr;

	@RequestMapping(value = "/getAppSettingTable.ofc", method = RequestMethod.GET)
	@ACLMapping(subsystem = "backoffice", module = "system", page = "app_setting", opr = "edit")
	public void getAppSettingTable(HttpServletRequest req, HttpServletResponse resp)
	{
		try
		{
			SchemaBase.validate(req, this, EnumConverterImpl.getInstance());

			ResponseUtils.renderJson(resp, "成功", mgr.getByScope());
		}
		catch (Exception ex)
		{
			ResponseUtils.renderJson(resp, ex);
		}
	}

	@RequestMapping(value = "/saveAppSetting.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "system", page = "app_setting", opr = "edit")
	@ActionArgs(value = {
			@ActionArg(name = "app_setting", header = "系统配置")
	})
	public void saveAppSetting(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String, Object> args = null;

		try
		{
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());

			mgr.saveAll(
					(String) args.get("app_setting"),
					(Trace) args.get(Trace.LOG_ARG));

			ResponseUtils.renderJson(resp, "保存成功", mgr.getByScope());
		}
		catch (Exception ex)
		{
			ResponseUtils.renderJson(resp, ex);
		}
	}
}
