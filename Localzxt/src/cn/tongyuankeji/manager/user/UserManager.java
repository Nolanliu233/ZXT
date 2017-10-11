package cn.tongyuankeji.manager.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface UserManager
{
	/*---------------------------------- 用户注册、修改密码、安全问题、找回密码、登录 ----------------------------------*/
	
	/**
	 * 查看注册名是否可用
	 * @param loginId
	 *            MobilePhone、 ECode
	 * @return 如果有匹配的，返回User Json包，否则，返回的json包isEmpty() == true
	 */
	public abstract JSONObject getByLoginId(String loginId) throws Exception;
	
	/**
	 * 用户登录
	 * 
	 * @param loginId
	 *            登录名：eCode, mobilePhone
	 * @param pwdEncrypted
	 *            密码（action接收密文）
	 */
	public abstract void login(HttpServletRequest req,HttpServletResponse resp, String loginId, String pwdEncrypted, Trace trace) throws Exception;
	
	/**
	 * 用户注销。不抛出异常
	 */
	public abstract void logoff(HttpServletRequest req, HttpServletResponse resp, Trace trace);
	
	
	/*---------------------------------- 后台新建用户、编辑用户信息、编辑提醒设置 ----------------------------------*/
	/**
	 * （后台管理界面）企业管理 -> 用户 -> 编辑，提取用户详细信息
	 * 
	 * @param loginUser
	 *            登录的后台用户
	 * @param userId
	 *            编辑哪位用户
	 */
	public abstract JSONObject getById(Person loginUser, int userId) throws Exception;
	
	/**
	 * 个人中心 -> 修改个人资料，提取用户详细信息
	 * 
	 * @param loginPerson
	 *            登录的用户
	 */
	public abstract JSONObject getById(Person loginUser) throws Exception;
	
	public abstract void firstChangePassword(String loginId, String oldPwdEncrypted, String newPwdClean, Trace trace) throws Exception;
	
	/**
	 * 用户修改密码
	 * 
	 * @param loginPerson
	 *            登录的用户
	 * @param oldPwdEncrypted
	 *            用户输入的现有密码（密文）
	 * @param newPwdClean
	 *            新密码（明文）
	 */
	public abstract void changePassword(Person loginUser, String oldPwdEncrypted, String newPwdClean, Trace trace) throws Exception;
	
	
	public abstract void deleteUser(Integer userId) throws Exception;

	/**
	 * 用户修改个人基本信息
	 * 
	 * @param userName
	 *            姓名
	 * @param mobileNumber
	 *            用户手机号
	 * @param email
	 *            公司邮箱
	 * @param companyName
	 *            公司名称
	 * @param companyCode
	 *            公司组织结构代码证
	 * @param officPhone
	 *            公司电话
	 * @param address
	 *            公司地址
	 */
	public abstract void saveUserInfo(HttpServletRequest req,HttpServletResponse resp, 
			Person person, String userName, String mobileNumber, String email, 
			String companyName, String companyCode, String officPhone, String address) throws Exception;

	/**
	 * 用户修改个人基本信息
	 * 
	 * @param dataTypeIds
	 *            定制消息口ID
	 */
	public abstract void saveUserDataType(HttpServletRequest req, HttpServletResponse resp, Person person,
			Integer[] dataTypeIds) throws Exception;
	
	/**
	 * 根据用户ID取得定制消息口
	 * @param userId
	 * @param lock
	 * @return
	 * @throws Exception
	 */
	public JSONArray getUserDataTypeByUserId(int userId, Boolean lock) throws Exception;
}
