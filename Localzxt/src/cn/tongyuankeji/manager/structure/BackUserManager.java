package cn.tongyuankeji.manager.structure;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import cn.tongyuankeji.common.cache.McPerson;

import cn.tongyuankeji.common.parameters.EnumPersonState;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;

public interface BackUserManager
{
	public abstract JSONObject getById(McPerson loginUser, int sysId) throws Exception;
	
	/**
	 * 后台机构管理界面，查询机用户表，按用户姓名升序
	 * @param loginUser
	 *            登录用户
	 * @param state
	 *            勾选的EnumPersonState状态。不过滤状态，传入null
	 * @param fuzzy
	 *            用户名userName、手机号mobilePhone（模糊匹配）。不使用此条件时，传入null
	 */
	public abstract JSONObject getByProperty(McPerson loginUser, int ownerGovId, EnumPersonState state,
			String fuzzy, int pageStart) throws Exception;
	
	/**
	 * 新建机构后保存
	 * 
	 * @param userInfo
	 *            客户端传来的输入值
	 * 
	 * @param ownerGovId
	 *            所属机构Govanization.SysId
	 * 
	 * @param pwdClean
	 *            初始密码（明文）
	 * @return 新建siteuser.SysId
	 */
	public abstract int create(BackUserArg userInfo, int ownerGovId, String pwdClean, Trace trace) throws Exception;
	
	/**
	 * 编辑用户后保存，可能需要更新登录缓存链，强制被编辑用户退出重新登录
	 * 
	 * @param sysId
	 *            被编辑的用户ID
	 * 
	 * @param userInfo
	 *            客户端传来的输入值
	 * 
	 * @return 当编辑的是当前登录人自己，且影响到缓存时，返回true强制重新登录
	 */
	public abstract boolean update(int userId, BackUserArg userInfo, Trace trace) throws Exception;
	
	/**
	 * (私企用户) 用户修改密码
	 * 
	 * @param oldPwdEncrypted
	 *            用户输入的当前密码（密文）
	 * @param newPwdEncrypted
	 *            新密码（明文）
	 */
	public abstract void updatePwd(Person loginUser, String oldPwdEncrypted, String newPwdClean, Trace trace) throws Exception;
	
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
	public abstract void updateSecurity(Person loginUser, String pwdEncrypted, String securityQ, String securityA, Trace trace) throws Exception;
	
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
	public abstract void firstUpdatePwd(String userName, String oldPwdEncrypted, String newPwdClean, Trace trace) throws Exception;
	
	/**
	 * (私企用户) 用户修改个人资料
	 * 
	 * @return 影响到缓存时，返回true强制重新登录
	 */
	public abstract boolean updateSelf(BackUserArg userInfo, Trace trace) throws Exception;
	
	
	/**
	 * 修改用户状态
	 */
	public abstract void changeState(McPerson loginUser, int userId, EnumPersonState toState, Trace trace) throws Exception;
	
	/*---------------------------------- biz ----------------------------------*/
	/**
	 * 用户登录
	 * 
	 * @param userName
	 *            用户名
	 * @param pwd
	 *            密码（密文）
	 */
	public abstract JSONObject firstLogin(HttpServletResponse resp, String userName, String pwdEncrypted, Trace trace) throws Exception;
	
	/**
	 * 用户登录
	 * 
	 * @param userName
	 *            用户名
	 * @param pwd
	 *            密码（密文）
	 * @return 登录人BackUser.SysId
	 */
	public abstract JSONObject login(HttpServletResponse resp, String userName, String pwdEncrypted, Trace trace) throws Exception;
	
	/**
	 * 用户注销。不抛出异常
	 */
	public abstract void logoff(HttpServletRequest req, HttpServletResponse resp, Trace trace);
}
