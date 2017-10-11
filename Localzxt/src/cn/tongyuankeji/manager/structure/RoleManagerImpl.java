package cn.tongyuankeji.manager.structure;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cn.tongyuankeji.dao.structure.RoleDAO;
import cn.tongyuankeji.dao.structure.BackUserDAO;
import cn.tongyuankeji.entity.structure.Role;
import cn.tongyuankeji.entity.structure.BackUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import cn.tongyuankeji.common.encoder.Md5PwdEncoder;
import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.common.parameters.EnumGenderKind;
import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.common.parameters.EnumMgrReturnType;
import cn.tongyuankeji.common.parameters.EnumPersonState;
import cn.tongyuankeji.common.util.PathUtils;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.common.web.ACL;
import cn.tongyuankeji.entity.BaseEntity;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;

/**
 * 后台管理 角色
 * 
 * @author 代平 2017-05-06 创建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleManagerImpl implements RoleManager
{
	static final Logger log = LogManager.getLogger(RoleManagerImpl.class.getName());
	
	@Autowired
	private RoleDAO dao;
	
	@Autowired
	private BackUserDAO backUserDao;
	
	/*---------------------------------- 后台管理 ----------------------------------*/
	
	@Override
	public Object getByState(EnumMgrReturnType returnAs, EnumGenericState StateMin, Boolean fullJson) throws Exception
	{
		List<Role> lst = dao.getByState(StateMin);
		
		if (returnAs == EnumMgrReturnType.rawlist)
			return lst;
		
		else if (returnAs == EnumMgrReturnType.jsonArray)
			return BaseEntity.list2JsonArray(lst, fullJson, fullJson);
		
		throw new RuntimeException("not supported");
	}
	
	@Override
	public JSONObject getMask(boolean isSystemManager, boolean isSystemAutoCreate)
	{
		JSONObject orole = new JSONObject();
		
		orole.put("sysId", JSONNull.getInstance());
		orole.put("stateTitleZh", JSONNull.getInstance());
		orole.put("code", JSONNull.getInstance());
		orole.put("title", JSONNull.getInstance());
		orole.put("remarks", JSONNull.getInstance());
		
		orole.put("acl", this.convertACL2JSON(null, isSystemManager, isSystemAutoCreate));
		
		return orole;
	}
	
	@Override
	public int create(boolean isSystemManager, String title, String titleEn, String acl, String remarks, Person createUser, Trace trace) throws Exception
	{
		Document xdocMask = RoleManagerImpl.parsePermission(null); // load mask
		
		Role entry = new Role(isSystemManager);
		entry.setTitle(title);
		if (titleEn == null)
		{
			titleEn = "";
		}
		if (remarks == null)
		{
			remarks = "";
		}
		entry.setRemarks(remarks);
		entry.setACL(ACL.json2ACL(xdocMask, acl));
		
		dao.insert(entry, createUser, trace);
		
		RoleManagerImpl.refreshRole(entry);
		
		return entry.getSysId();
	}
	
	@Override
	public void update(int sysId, String title, String titleEn, String acl, String remarks, Person modifyUser, Trace trace) throws Exception
	{
		// 不使用此方法的其他编辑：State
		
		boolean hasACLChange = false;
		Role entry = dao.getById(sysId, true);
		if (entry == null)
			throw new RuntimeException(String.format("无法找到编辑的角色%s(%d)！", title, sysId));
		
		Document xdocMask = RoleManagerImpl.parsePermission(null); // load mask
		
		entry.setTitle(title);
		if (titleEn == null)
		{
			titleEn = "";
		}
		if (remarks == null)
		{
			remarks = "";
		}
		entry.setRemarks(remarks);
		if (!entry.getACL().equalsIgnoreCase(acl))
		{
			hasACLChange = true;
			entry.setACL(ACL.json2ACL(xdocMask, acl));
		}
		
		dao.update(entry, modifyUser, trace);
		
		if (hasACLChange)
			RoleManagerImpl.refreshRole(entry);
	}
	
	@Override
	public void changeState(int sysId, EnumGenericState toState, Trace trace) throws Exception
	{
		List<BackUser> boundUsers = null;
		
		Role entry = dao.getById(sysId, true);
		if (entry == null)
			throw new RuntimeException(String.format("无法找到编辑的角色(%d)！", sysId));
		
		if (!entry.getState().equals(toState.byteObject()))
		{
			if (toState.byteValue() < entry.getState())
			{
				if (toState.byteValue() == EnumGenericState.hidden.byteValue())
					boundUsers = backUserDao.getByRoleId(entry.getSysId(), EnumPersonState.registered, true); // 往下，变成hidden，不能继续用在register及以上状态的BackUser上
					
				else if (toState.byteValue() == EnumGenericState.deleted.byteValue())
					boundUsers = backUserDao.getByRoleId(entry.getSysId(), EnumPersonState.closed, true); // 往下，deleted，不能继续用在close及以上状态的BackUser上
					
				if (boundUsers.size() > 0)
					throw new RuntimeException(String.format("还有%d位用户在使用角色%s(%d)！", boundUsers.size(), entry.getTitle(), entry.getSysId()));
			}
			
			dao.updateState(entry, toState, trace);
			
			RoleManagerImpl.refreshRole(entry);
		}
	}
	
	@Override
	public JSONObject getById(int sysId) throws Exception
	{
		JSONObject orole = null;
		Role entry = dao.getById(sysId, false);
		if (entry == null)
			throw new RuntimeException(String.format("无法找到要查看角色(%d)！", sysId));
		
		JSONArray aacl = this.convertACL2JSON(entry, entry.getIsSystemManager(), false);
		orole = entry.toJson(true, false);
		orole.put("nodes", aacl);
		return orole;
	}
	
	/*---------------------------------- Helper ----------------------------------*/
	/**
	 * 新建、编辑权限。使用ACL_MASK填充所有可勾选项，如果传入entry != null，用entry.ACL勾选ischecked
	 * 
	 * @param entry
	 *            被编辑的角色。新建时，传入null
	 *        isSystemManager
	 *        	创建的是否未管理员账户
	 *        isSystemAutoCreate
	 *        	是否未系统自动创建
	 * @return 角色JSON包
	 */
	private JSONArray convertACL2JSON(Role entry, boolean isSystemManager, boolean isSystemAutoCreate) throws RuntimeException
	{
		Document xdocMask = null, xdoc = null;
		
		try
		{
			xdocMask = RoleManagerImpl.parsePermission(null);
		}
		catch (Exception ex)
		{
			throw new RuntimeException("系统错误，无法解析权限母版！");
		}
		
		if (entry != null) // edit, unless, create
		{
			try
			{
				xdoc = RoleManagerImpl.parsePermission(entry.getACL());
			}
			catch (Exception ex)
			{
				throw new RuntimeException(String.format("系统错误，无法解析权限配置%s(%d)！", entry.getTitle(), entry.getSysId()));
			}
		}
		
		// xml转json
		return ACL.ACL2Json(xdocMask, xdoc, isSystemManager, isSystemAutoCreate);
	}
	
	/**
	 * 将acl XML文本解析成XMLDocument对象
	 */
	private static Document parsePermission(String acl) throws Exception
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		
		if (Utils.isBlank(acl))
			return dBuilder.parse(new File(RoleManagerImpl.ACL_MASK_FILENAME));
		else
			return ACL.loadPermission(RoleManagerImpl.ACL_MASK_FILENAME, acl, dBuilder, XPathFactory.newInstance().newXPath());
	}
	
	/**
	 * 保存Role entity到数据库后，刷新内存中的cn.tongyuankeji.common.web.ACL.ACL_LIST map
	 * 
	 * @param savedRole
	 *            被保存修改的的角色
	 */
	public static synchronized void refreshRole(Role savedRole)
	{
		Document xdoc = null;
		
		// 启用
		if (savedRole.getState() >= EnumGenericState.active.byteValue())
		{
			try
			{
				xdoc = RoleManagerImpl.parsePermission(savedRole.getACL());
				ACL.addToMap(savedRole.getSysId(), xdoc);
			}
			catch (Exception ex)
			{
			}
		}
		
		// 停用或删除
		else
		{
			ACL.removeFromMap(savedRole.getSysId());
		}
	}
	
	/*---------------------------------- 系统初始化 ----------------------------------*/
	// 权限文件路径
	private static String ACL_MASK_FILENAME = null;
	private static String PERMISSION_FILENAME = "permission.xml";
	
	/**
	 * 网站启动时 加载所有status >= active的Role<br />
	 * 使用权限母版ACL_MASK_FILENAME
	 */
	@Override
	public void initRoleMap()
	{
		RoleManagerImpl.ACL_MASK_FILENAME = PathUtils.getWebConfigPath() + RoleManagerImpl.PERMISSION_FILENAME; // permission.xml路径初始化
		RoleManagerImpl.ACL_MASK_FILENAME = "/" + RoleManagerImpl.ACL_MASK_FILENAME.replace("\\", "/");
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document xdoc = null;
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		List<Role> lst = null;
		
		Role tmp = null;
		String acl = null;
		Boolean hasMaster = false;
		Integer masterRoleId = null;
		
		try
		{
			dBuilder = dbFactory.newDocumentBuilder();
			
			lst = dao.getByState(EnumGenericState.active);
			for (int i = 0, len = lst.size(); i < len; i++)
			{
				tmp = (Role) lst.get(i);
				acl = tmp.getACL();
				if (!Utils.isBlank(acl))
				{
					xdoc = ACL.loadPermission(RoleManagerImpl.ACL_MASK_FILENAME, acl, dBuilder, xpath);
					if (xdoc != null)
						ACL.addToMap(tmp.getSysId(), xdoc);
				}
				if(tmp.getIsSystemManager()){
					hasMaster = true;
				}
				if(hasMaster && masterRoleId == null){
					masterRoleId = tmp.getSysId();
				}
			}
			if(!hasMaster){
				masterRoleId = initCreateSystemRole();
			}
			
			List<BackUser> list = backUserDao.getByRoleId(masterRoleId, EnumPersonState.active, false);
			if(list.size() <= 0){
				BackUser backUser = new BackUser();
				backUser.setOwnerGovId(ConstantBase.DEFAULT_OWNER_GOV_ID);
				backUser.setState(EnumPersonState.active.byteValue());
				backUser.setLoginName(BackUser.SYSTEM_ADMIN_NAME);
				backUser.setUserName(BackUser.SYSTEM_ADMIN_NAME);
				backUser.setMobilePhone(ConstantBase.DEFAULT_USER_MOBILE_NUM);
				Md5PwdEncoder md5 = new Md5PwdEncoder();
				backUser.setPwd(md5.encodePassword(ConstantBase.DEFAULT_USER_PWD));
				backUser.setGender(EnumGenderKind.male.byteValue());
				backUser.setOtherPhone(ConstantBase.DEFAULT_USER_OTHER_PHONE);
				backUser.setRoleId(masterRoleId);
				backUserDao.initCreateSystemUser(backUser, BackUser.SYSTEM_ADMIN_NAME);
				log.debug("初始化创建用户："+BackUser.SYSTEM_ADMIN_NAME);
			}
		}
		catch (Exception ex)
		{
			log.fatal("装载权限失败！", ex);
			ACL.clearMap();
		}
	}
	
	//系统初始化时，若没有系统管理员角色则创建管理员角色
	private int initCreateSystemRole() throws Exception
	{
		Document xdocMask = RoleManagerImpl.parsePermission(null); // load mask
		Role entry = new Role();
		entry.setIsSystemManager(true);
		entry.setTitle("系统管理员");
		entry.setRemarks("系统初始化自动创建");
		entry.setACL(ACL.json2ACL(xdocMask, this.convertACL2JSON(null, true, true).toString()));
		
		dao.initCreateSystemRole(entry, BackUser.SYSTEM_ADMIN_NAME);
		
		RoleManagerImpl.refreshRole(entry);
		log.debug("初始化创建角色：系统管理员");
		return entry.getSysId();
	}
	
}
