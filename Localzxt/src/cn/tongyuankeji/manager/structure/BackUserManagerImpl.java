package cn.tongyuankeji.manager.structure;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import cn.tongyuankeji.common.cache.McPerson;
import cn.tongyuankeji.common.cache.XmcManagerImpl;
import cn.tongyuankeji.common.fileupload.FileUploadManager;
import cn.tongyuankeji.common.fileupload.FileUploadSetting;
import cn.tongyuankeji.common.fileupload.PostUploadHandler;
import cn.tongyuankeji.common.parameters.EnumUploadType;
import cn.tongyuankeji.dao.structure.GovernmentDAO;
import cn.tongyuankeji.dao.structure.RoleDAO;
import cn.tongyuankeji.dao.structure.BackUserDAO;
import cn.tongyuankeji.entity.structure.Government;
import cn.tongyuankeji.entity.structure.Role;
import cn.tongyuankeji.entity.structure.BackUser;
import cn.tongyuankeji.common.web.GovController;
import cn.tongyuankeji.common.web.GovControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tongyuankeji.common.encoder.Md5PwdEncoder;
import cn.tongyuankeji.common.parameters.EnumGenderKind;
import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.common.parameters.EnumPersonState;
import cn.tongyuankeji.common.util.JsonUtils;
import cn.tongyuankeji.common.web.RunSettingImpl;
import cn.tongyuankeji.dao.GenericDAO;
import cn.tongyuankeji.dao.ReadonlyTable;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;

/**
 * 后台 用户管理
 * 
 * @author 代平  2017-05-15 创建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BackUserManagerImpl implements BackUserManager, PostUploadHandler
{
	@Autowired
	private BackUserDAO dao;
	
	@Autowired
	private GovernmentDAO govDao;
	
	@Autowired
	private RoleDAO roleDao;
	
	@Autowired
	private GenericDAO genDao;
	
	/*---------------------------------- 后台管理 ----------------------------------*/
	@Override
	public JSONObject getById(McPerson loginUser, int sysId) throws Exception
	{
		String errMsg = null;
		JSONObject oentry = null;
		BackUser entry = dao.getById(sysId, false);
		
		// 登录人是否可操作这个用户
		if ((errMsg = GovControllerImpl.isMyOwnerGov(loginUser, entry)) != null)
			throw new RuntimeException(errMsg);
		
		if (entry != null)
		{
			oentry = entry.toJson(true, false);
			JsonUtils.put(oentry, "thumb", FileUploadManager.getAbsoluteFilename(EnumUploadType.thumb, entry.getThumbFile()));
			
			oentry.put("defaultThumb", FileUploadManager.getAbsoluteFilename(EnumUploadType.thumb, null));
			
			return oentry;
		}
		
		return new JSONObject();
	}
	
	/**
	 * 后台机构管理界面，查询用户列表，按用户姓名升序
	 * 
	 * @param ownerGovId
	 *            用户所属园区
	 * @param State
	 *            勾选的EnumPersonState状态。不过滤状态，传入null
	 * @param fuzzy
	 *            用户名userName、手机号mobilePhone（模糊匹配）。不使用此条件时，传入null
	 * @param leafDeptIds
	 *            末级 部门id（0～N个）。不使用此条件时，传入null
	 * @param appendDept
	 *            返回的结果中，是否需要部门链
	 */
	@Override
	public JSONObject getByProperty(McPerson loginUser, int ownerGovId, EnumPersonState State,
			String fuzzy, int pageStart) throws Exception
	{
		List<ReadonlyTable> lstdt = null;
		ReadonlyTable dtUser = null;
		JSONObject oresult = new JSONObject(), oentry = null;
		JSONArray aentry = new JSONArray();
		int userId = 0;
		
		ownerGovId = GovControllerImpl.searchOwnerGovId(loginUser, ownerGovId); // 登录人可见哪些机构的用户
		
		lstdt = genDao.call("searchBackUser", pageStart*RunSettingImpl.getPAGINATION_COUNT(), RunSettingImpl.getPAGINATION_COUNT(),
				ownerGovId, State.byteObject(), fuzzy);
		
		oresult.put("count", lstdt.get(0).get(0, "RecordCount"));
		oresult.put("pageSize", RunSettingImpl.getPAGINATION_COUNT());
		dtUser = lstdt.get(1);
		
		for (int i = 0; i < dtUser.getRowCount(); i++)
		{
			oentry = new JSONObject();
			
			userId = dtUser.get(i, "UserId");
			
			oentry.put("sysId", userId);
			oentry.put("StateTitleZh", EnumPersonState.titleOf((Byte) dtUser.get(i, "UserState")));
			oentry.put("State", dtUser.get(i, "UserState"));
			oentry.put("userName", dtUser.get(i, "UserName"));
			oentry.put("fullname", dtUser.get(i, "Fullname"));
			oentry.put("createdOn", dtUser.get(i, "CreatedOn"));
			oentry.put("mobilePhone", dtUser.get(i, "MobilePhone"));
			oentry.put("gender", EnumGenderKind.titleOf((byte) dtUser.get(i, "Gender")));
			oentry.put("ownerGovTitle", dtUser.get(i, "OwnerGovTitle"));
			oentry.put("roleTitle", dtUser.get(i, "RoleTitle"));
			
			aentry.add(oentry);
		}
		
		oresult.put("info", aentry);
		
		oresult.put("execDuration", lstdt.get(3).get(0, "ExecDuration"));
		
		return oresult;
	}
	
	/**
	 * 新建用户后保存
	 * 
	 * @param userInfo
	 *            客户端传来的输入值
	 * 
	 * @param ownerGovId
	 *            所属园区Government.SysId
	 * 
	 * @param pwdClean
	 *            初始密码（明文）
	 * 
	 * @return 新建backuser.SysId
	 */
	@Override
	public int create(BackUserArg userInfo, int ownerGovId, String pwdClean, Trace trace) throws Exception
	{
		BackUser entry = null;
		String errMsg = null;
		
		// 登录人是否可为这个园区创建用户
		if ((errMsg = GovControllerImpl.isMyOwnerGov(userInfo.byUser, ownerGovId)) != null)
			throw new RuntimeException(errMsg);
		
		// 验证登录号唯一性
		if (dao.getByUserName(userInfo.userName, false) != null)
			throw new RuntimeException("用户名已被注册！");
		
		// 园区验证，并加锁
		Government gov = checkGov(ownerGovId);
		
		// 角色验证
		checkRole(userInfo.roleId);
		
		entry = new BackUser(gov);
		
		entry.setUserName(userInfo.userName);
		entry.setMobilePhone(userInfo.mobilePhone);
		entry.setEmail(userInfo.email);
		entry.setPwd(new Md5PwdEncoder().encodePassword(pwdClean));
		
		entry.setGender(userInfo.gender.byteObject());
		entry.setRoleId(userInfo.roleId);
		entry.setJobTitle(userInfo.jobTitle);
		entry.setOtherPhone(userInfo.otherPhone);
		entry.setRemarks(userInfo.remarks);
		
		return entry.getSysId();
	}
	
	/**
	 * 编辑用户后保存，可能需要更新登录缓存链，强制被编辑用户退出重新登录
	 * 
	 * @param sysId
	 *            被编辑的用户ID
	 * 
	 * @param userInfo
	 *            客户端传来的输入值
	 * @return 当编辑的是当前登录人自己，且影响到缓存时，返回true强制重新登录
	 */
	@Override
	public boolean update(int userId, BackUserArg userInfo, Trace trace) throws Exception
	{
		String errMsg = null;
		
		// 编辑时，不能修改的ownerGovId, userName
		// 使用其他方法修改的： pwd, securityQ/A, 图片, State
		
		boolean affectCache = false;
		BackUser entry = dao.getById(userId, true);
		if (entry == null)
			throw new RuntimeException(String.format("无法找到用户(%d)！", userId));
		
		// 登录人是否可操作这个园区的用户
		if ((errMsg = GovControllerImpl.isMyOwnerGov(userInfo.byUser, entry)) != null)
			throw new RuntimeException(errMsg);
		
		// 园区验证，并加锁
		Government gov = checkGov(entry.getOwnerGovId());
		
		// 角色验证
		checkRole(userInfo.roleId);
		
		entry.setMobilePhone(userInfo.mobilePhone);
		entry.setEmail(userInfo.email);
		entry.setGender(userInfo.gender.byteObject());
		entry.setRoleId(userInfo.roleId);
		entry.setJobTitle(userInfo.jobTitle);
		entry.setPidNumber(userInfo.pidNumber);
		entry.setOtherPhone(userInfo.otherPhone);
		entry.setRemarks(userInfo.remarks);
		
		
		// 影响到entry的缓存？踢出entry
		if (affectCache && userInfo.byUser.getSysId().equals(userId))
		{
			XmcManagerImpl.tryRemoveAuthentication(entry);
			return true;
		}
		
		return false;
	}
	
	/**
	 * (私企用户) 用户修改密码
	 * 
	 * @param oldPwdEncrypted
	 *            用户输入的当前密码（密文）
	 * @param newPwdEncrypted
	 *            新密码（明文）
	 */
	@Override
	public void updatePwd(Person loginUser, String oldPwdEncrypted, String newPwdClean, Trace trace) throws Exception
	{
		BackUser entry = dao.getById(loginUser.getSysId(), true);
		if (entry == null)
			throw new RuntimeException(String.format("无法找到用户(%d)！", loginUser.getSysId()));
		
		dao.updatePwd(entry, new Md5PwdEncoder().encodePassword(oldPwdEncrypted), new Md5PwdEncoder().encodePassword(newPwdClean), trace);
	}
	
	/**
	 * (私企用户) 用户修改密保安全问题
	 * 
	 * @param pwdEncrypted
	 *            用户输入的当前密码（密文）
	 * @param securityQ
	 *            密码安全问题
	 * @param securityA
	 *            密码问题回答
	 */
	@Override
	public void updateSecurity(Person loginUser, String pwdEncrypted, String securityQ, String securityA, Trace trace) throws Exception
	{
		BackUser entry = dao.getById(loginUser.getSysId(), true);
		if (entry == null)
			throw new RuntimeException(String.format("无法找到用户(%d)！", loginUser.getSysId()));
		
		dao.updateSecurity(entry, pwdEncrypted, securityQ, securityA, trace);
	}
	
	/**
	 * 用户第一次登录修改密码
	 * 
	 * @param userName
	 *            用户名
	 * @param oldPwdEncrypted
	 *            现有密码（密文）
	 * @param newPwdClean
	 *            新密码（明文）
	 */
	@Override
	public void firstUpdatePwd(String userName, String oldPwdEncrypted, String newPwdClean, Trace trace) throws Exception
	{
		// 返回的用户包含已删除的
		if (oldPwdEncrypted.equals(newPwdClean))
			throw new RuntimeException("新密码不能与旧密码相同！");
		
		BackUser user = dao.getByUserName(userName, true);
		
		// 返回的用户包含已删除的
		if (user == null || user.getState().equals(EnumPersonState.deleted.byteObject()))
			throw new RuntimeException("用户不存在！");
		
		if (!user.getPwd().equals(oldPwdEncrypted))
			throw new RuntimeException("密码错误！");
		
		// 账号状态
		if (user.getState().byteValue() < EnumPersonState.active.byteValue())
			throw new RuntimeException(String.format("用户状态是 %s！", EnumPersonState.titleOf(user.getState())));
		
		
		dao.updatePwd(user, oldPwdEncrypted, new Md5PwdEncoder().encodePassword(newPwdClean), trace);
		dao.updateLogin(user, trace);
	}
	
	/**
	 * (私企用户) 用户修改个人资料
	 * 
	 * @return 影响到缓存时，返回true强制重新登录
	 */
	@Override
	public boolean updateSelf(BackUserArg userInfo, Trace trace) throws Exception
	{
		// 编辑时，不能修改的ownerGovId, userName
		// 使用其他方法修改的： pwd, securityQ/A, 图片, State
		
		boolean affectCache = false;
		BackUser entry = dao.getById(userInfo.byUser.getSysId(), true);
		if (entry == null)
			throw new RuntimeException(String.format("无法找到用户(%d)！", userInfo.byUser.getSysId()));
		
		// 园区验证，并加锁
		Government gov = checkGov(entry.getOwnerGovId());
		
		// 角色验证
		checkRole(userInfo.roleId);
		
		entry.setMobilePhone(userInfo.mobilePhone);
		entry.setEmail(userInfo.email);
		entry.setGender(userInfo.gender.byteObject());
		entry.setJobTitle(userInfo.jobTitle);
		
		entry.setPidNumber(userInfo.pidNumber);
		entry.setOtherPhone(userInfo.otherPhone);
		
		// 影响到entry的缓存？踢出entry
		if (affectCache)
		{
			XmcManagerImpl.tryRemoveAuthentication(entry);
			return true;
		}
		
		return false;
	}
	
	/**
	 * 头像 上传完成后，修改数据库相应字段
	 */
	@Override
	public void postUpload(HttpServletRequest req, FileUploadSetting setting) throws Exception
	{
		McPerson loginStudent = null;
		Trace trace = null;
		String filename = null, fileExt = null;
		BackUser entry = null;
		
		assert setting.fileType == EnumUploadType.thumb : "不支持的文件类型！";
		
		loginStudent = XmcManagerImpl.getByAuthKey(req);
		trace = new Trace(req, loginStudent);
		entry = dao.getById((Integer) setting.recordId, true);
		if (entry == null)
			throw new RuntimeException(String.format("无法找到用户(%d)！", setting.recordId));
		
		filename = entry.getThumbFile();
		
		dao.updateFile(entry, setting.file, trace);
		
		// 数据库修改成功后，再删除老的图片
		FileUploadManager.deleteFile(setting.fileType, filename, fileExt);
		
		// 当登录人修改自己的头像时，影响到缓存，需要同步一下
		if (setting.fileType == EnumUploadType.thumb)
		{
			if (loginStudent.getSysId().equals(entry.getSysId()))
				XmcManagerImpl.updateAuthentication(entry);
		}
	}
	
	/**
	 * 修改用户状态
	 */
	@Override
	public void changeState(McPerson loginUser, int userId, EnumPersonState toState, Trace trace) throws Exception
	{
		// 涉及到的关联，只用于显示，不需要检查： Student.ActivatedBy, ClosedBy,DeletedBy
		// LessonRc.IssuedBy, ApprovedBy, ArchivedBy
		
		String errMsg = null;
		
		if (loginUser.getSysId().equals(userId))
			throw new RuntimeException("不能修改本人帐号状态！");
		
		BackUser entry = dao.getById(userId, true);
		if (entry == null)
			throw new RuntimeException(String.format("无法找到用户(%d)！", userId));
		
		// 登录人是否可操作这个用户
		if ((errMsg = GovControllerImpl.isMyOwnerGov(loginUser, entry)) != null)
			throw new RuntimeException(errMsg);
		
		if (!entry.getState().equals(toState.byteObject()))
		{
			dao.updateState(entry, toState, trace);
			
			// 状态变低，踢出entry
			if (toState.byteValue() < entry.getState())
			{
				XmcManagerImpl.tryRemoveAuthentication(entry);
			}
		}
	}
	
	/*---------------------------------- biz ----------------------------------*/
	
	/**
	 * 用户第一次登录
	 * 
	 * @param userName
	 *            用户名
	 * @param pwdEncrypted
	 *            密码（密文）
	 */
	@Override
	public JSONObject firstLogin(HttpServletResponse resp, String userName, String pwdEncrypted, Trace trace) throws Exception
	{
		JSONObject result = new JSONObject();
		BackUser user = dao.getByUserName(userName, true);
		
		// 返回的用户包含已删除的
		if (user == null || user.getState().equals(EnumPersonState.deleted.byteObject()))
			throw new RuntimeException("用户不存在！");
		
		if (!user.getPwd().equals(pwdEncrypted))
			throw new RuntimeException("用户名或密码错误！");
		
		// 账号状态
		if (user.getState().byteValue() < EnumPersonState.active.byteValue())
			throw new RuntimeException(String.format("用户状态是 %s！", EnumPersonState.titleOf(user.getState())));
		if (user.getRecentLoginAt() == null)
		{
			result.put("question", user.getSecurityQ());
			result.put("sysId", user.getSysId());
		}
		
		return result;
	}
	
	/**
	 * 用户登录
	 * 
	 * @param userName
	 *            用户名
	 * @param pwdEncrypted
	 *            密码（密文）
	 * @return 登录人BackUser.SysId
	 */
	@Override
	public JSONObject login(HttpServletResponse resp, String userName, String pwdEncrypted, Trace trace) throws Exception
	{
		BackUser user = dao.getByUserName(userName, true);
		
		// 返回的用户包含已删除的
		if (user == null || user.getState().equals(EnumPersonState.deleted.byteObject()))
			throw new RuntimeException("用户不存在！");
		
		if (!user.getPwd().equals(pwdEncrypted))
			throw new RuntimeException("用户名或密码错误！");
		
		// 账号状态
		if (user.getState().byteValue() < EnumPersonState.active.byteValue())
			throw new RuntimeException(String.format("用户状态是 %s！", EnumPersonState.titleOf(user.getState())));
		
		XmcManagerImpl.addAuthentication(resp, user);
		
		dao.updateLogin(user, trace);
		
		JSONObject oresult = new JSONObject();
		oresult.put("userId", user.getSysId());
		oresult.put("userName", user.getUserName());
		oresult.put("ownerGovId", user.getOwnerGovId());
		oresult.put("thumbFilePic", FileUploadManager.getAbsoluteFilename(EnumUploadType.thumb, user.getThumbFile()));
		
		return oresult;
	}
	
	/**
	 * 用户注销。不抛出异常
	 */
	@Override
	public void logoff(HttpServletRequest req, HttpServletResponse resp, Trace trace)
	{
		Person loginUser = (Person) req.getAttribute(Person.LOGIN_PERSON);
		
		dao.updateLogoff(loginUser, trace);
		
		XmcManagerImpl.removeAuthentication(req, resp, true);
	}
	
	/*---------------------------------- private Helper ----------------------------------*/
	private Government checkGov(int govId) throws Exception
	{
		Government gov = govDao.getById(govId, true);
		if (gov == null)
			throw new RuntimeException(String.format("无法找到用户所属园区(%d)！", govId));
		if (gov.getState() < EnumPersonState.active.byteValue())
			throw new RuntimeException(String.format("园区 %s(%d) 状态是 %s！", gov.getTitle(), gov.getSysId(), EnumPersonState.titleOf(gov.getState())));
		
		return gov;
	}
	
	private Role checkRole(Integer roleId) throws Exception
	{
		Role role = null;
		if (roleId != null)
		{
			role = roleDao.getById(roleId, true);
			if (role == null)
				throw new RuntimeException(String.format("无法找到角色(%d))！", roleId));
			if (role.getState() < EnumGenericState.active.byteValue())
				throw new RuntimeException(String.format("角色 %s(%d) 状态是 %s！", role.getTitle(), role.getSysId(), EnumGenericState.titleOf(role.getState())));
		}
		
		return role;
	}
	
}
