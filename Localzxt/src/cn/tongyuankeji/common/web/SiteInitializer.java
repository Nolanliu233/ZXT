package cn.tongyuankeji.common.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import cn.tongyuankeji.common.cache.XmcManager;
import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.dao.AbstractDAOImpl;
//import cn.tongyuankeji.common.cache.XmcManager;
//import cn.tongyuankeji.manager.dictionary.AppSettingManager;
//import cn.tongyuankeji.manager.structure.RoleManager;
//import org.springframework.context.ApplicationContext;
//import org.springframework.web.context.ContextLoader;
//import cn.tongyuankeji.common.parameters.ConstantBase;
//import cn.tongyuankeji.dao.AbstractDAOImpl;
import cn.tongyuankeji.manager.dict.AppSettingManager;
import cn.tongyuankeji.manager.structure.RoleManager;

public class SiteInitializer extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final Logger log = LogManager.getLogger(SiteInitializer.class.getName());
	
	public SiteInitializer()
	{
	}

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		// Spring 中获取servletContext及WebApplicationContext
		log.info("Initialize system ...");

		AbstractDAOImpl.setIS_MYSQL(config.getInitParameter("dbms").equalsIgnoreCase("mysql"));
		AbstractDAOImpl.setLOG_TABLE_NAME(config.getInitParameter("logTableName"));
		ConstantBase.setUPLOAD_PATH(config.getInitParameter("UPLOAD_PATH"));

		// 初始化系统全局变量数据结构
		ApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		AppSettingManager settingMgr = (AppSettingManager) context.getBean("AppSettingManager");
		settingMgr.initRunSetting();

		// 装载权限
		RoleManager roleMgr = (RoleManager) context.getBean("RoleManager");
		roleMgr.initRoleMap();

		XmcManager cacheMgr = (XmcManager)context.getBean("XmcManager");
		cacheMgr.initCache();

		log.info("Initialize system Done");
	}
}