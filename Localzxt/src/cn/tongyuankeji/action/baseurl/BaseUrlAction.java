package cn.tongyuankeji.action.baseurl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.tongyuankeji.common.annotation.ACLMapping;
import cn.tongyuankeji.common.annotation.ActionArg;
import cn.tongyuankeji.common.annotation.ActionArgs;
import cn.tongyuankeji.common.parameters.EnumBaseUrlState;
import cn.tongyuankeji.common.util.EnumConverterImpl;
import cn.tongyuankeji.common.util.ResponseUtils;
import cn.tongyuankeji.common.web.SchemaBase;
import cn.tongyuankeji.dao.baseurl.BaseUrlDao;
import cn.tongyuankeji.dao.baseurl.BaseUrlDaoImpl;
import cn.tongyuankeji.entity.baseurl.BaseUrl;
import cn.tongyuankeji.manager.baseurl.BaseUrlManager;
import cn.tongyuankeji.manager.basicData.DataAreaService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class BaseUrlAction {
	@Autowired
	private BaseUrlManager baseUrlManager;

	
	/**
	 * 查询信息
	 */
	@RequestMapping(value = "/getBaseUrl.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "info_manage", page = "siteManage", opr = "view")
	@ActionArgs(value = { @ActionArg(name = "pageStart", header = "起始页", target_type = Integer.class, required = false),
			@ActionArg(name = "pageSize", header = "每页行数", target_type = Integer.class, required = false),
			@ActionArg(name = "siteName", header = "网站名称", required = false),
			@ActionArg(name = "siteUrl", header = "网站URl", required = false) })
	public void getBaseUrlList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> args = null;
		JSONObject json = new JSONObject();
		try {
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());
			json = baseUrlManager.selectAllBaseUrl((Integer) args.get("pageStart"), (Integer) args.get("pageSize"),
					(String) args.get("siteName"), (String) args.get("siteUrl"));
			ResponseUtils.renderJson(resp, "成功", json);
		} catch (Exception e) {
			ResponseUtils.renderJson(resp, e);
		}
	}

	/**
	 * 添加
	 */
	@RequestMapping(value = "/addData.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "info_manage", page = "siteManage", opr = "edit")
	@ActionArgs(value = { @ActionArg(name = "urlName", header = "网站名"), @ActionArg(name = "url", header = "网站URL地址"),
			@ActionArg(name = "urlArea", header = "区域"), @ActionArg(name = "urlLevel", header = "级别"),
			@ActionArg(name = "urlType", header = "所属信息口") })
	public void addBaseUrlData(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> args = null;
		try {
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());
			String urlName = (String) args.get("urlName");
			String url = (String) args.get("url");
			int urlArea =  Integer.valueOf((String) args.get("urlArea"));
			int urlLevel = Integer.valueOf((String) args.get("urlLevel"));
			int urlType = Integer.valueOf((String) args.get("urlType"));
			byte deletedType = EnumBaseUrlState.add.byteValue();
			int createRole = deletedType;
			BaseUrl baseUrl = new BaseUrl();
			baseUrl.setUrlName(urlName);
			baseUrl.setCreateRole(createRole);
			baseUrl.setUrl(url);
			baseUrl.setUrlLevel(urlLevel);
			baseUrl.setUrlType(urlType);
			baseUrl.setUrlArea(urlArea);

			baseUrlManager.addBaseUrlData(baseUrl);

			ResponseUtils.renderSuccessJson(resp, "添加成功");
		} catch (Exception e) {
			ResponseUtils.renderJson(resp, e);
		}

	}

	@RequestMapping(value = "/deleteBaseUrlData.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "info_manage", page = "siteManage", opr = "edit")
	@ActionArgs(value = { @ActionArg(name = "urlID", header = "主键") })
	public void deleteBaseUrlData(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> args = null;

		try {
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());
			int urlID = Integer.valueOf((String) args.get("urlID"));
			BaseUrl baseUrl = new BaseUrl();
			baseUrl = baseUrlManager.selectBaseUrl(urlID);
			byte deletedType = EnumBaseUrlState.deleted.byteValue();
			int createRole = deletedType;
			baseUrl.setCreateRole(createRole);
			baseUrlManager.deleteBaseUrl(baseUrl);
			ResponseUtils.renderSuccessJson(resp, "删除成功");
		} catch (Exception e) {
			ResponseUtils.renderJson(resp, e);
		}

	}

	@RequestMapping(value = "/updateData.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "info_manage", page = "siteManage", opr = "edit")
	@ActionArgs(value = { @ActionArg(name = "urlID", header = "主键"), @ActionArg(name = "urlName", header = "网站名"),
			@ActionArg(name = "url", header = "网站URL地址"), @ActionArg(name = "urlArea", header = "区域"),
			@ActionArg(name = "urlLevel", header = "级别"), @ActionArg(name = "urlType", header = "所属信息口") })
	public void updateBaseUrlData(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> args = null;
		try {
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());
			int urlID = Integer.valueOf((String) args.get("urlID"));
			String urlName = (String) args.get("urlName");
			String url = (String) args.get("url");
			int urlArea =  Integer.valueOf((String) args.get("urlArea"));
			int urlLevel = Integer.valueOf((String) args.get("urlLevel"));
			int urlType = Integer.valueOf((String) args.get("urlType"));
			BaseUrl baseUrl = new BaseUrl();
			int createRole = EnumBaseUrlState.edit.byteValue();
			baseUrl.setUrlID(urlID);
			baseUrl.setUrlName(urlName);
			baseUrl.setUrl(url);
			baseUrl.setUrlLevel(urlLevel);
			baseUrl.setUrlType(urlType);
			baseUrl.setCreateRole(createRole);
			baseUrl.setUrlArea(urlArea);

			baseUrlManager.updateBaseUrl(baseUrl);
			ResponseUtils.renderSuccessJson(resp, "修改成功");
		} catch (Exception e) {
			ResponseUtils.renderJson(resp, e);
		}

	}
	
	@RequestMapping(value = "/getSelectData.ofc")
	public void getBaseUrlSelectData(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			ResponseUtils.renderJson(resp, "成功", baseUrlManager.getSelectBaseUrl());
		} catch (Exception e) {
			ResponseUtils.renderJson(resp, e);
		}
	}

}
