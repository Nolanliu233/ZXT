package cn.tongyuankeji.manager.user;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tongyuankeji.common.cache.McPerson;
import cn.tongyuankeji.common.cache.XmcManagerImpl;
import cn.tongyuankeji.common.encoder.Md5PwdEncoder;
import cn.tongyuankeji.common.parameters.EnumClientType;
import cn.tongyuankeji.common.parameters.EnumCompanyState;
import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.common.parameters.EnumSiteLogKind;
import cn.tongyuankeji.common.parameters.EnumUserState;
import cn.tongyuankeji.common.util.RequestUtils;
import cn.tongyuankeji.common.web.SchemaBase;
import cn.tongyuankeji.dao.basicData.DataTypeDao;
import cn.tongyuankeji.dao.structure.CompanyDAO;
import cn.tongyuankeji.dao.user.UserDAO;
import cn.tongyuankeji.dao.user.UserDataTypeDAO;
import cn.tongyuankeji.entity.BaseEntity;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;
import cn.tongyuankeji.entity.log.SiteLog;
import cn.tongyuankeji.entity.structure.Company;
import cn.tongyuankeji.entity.user.User;
import cn.tongyuankeji.entity.user.UserDatatype;
import cn.tongyuankeji.manager.log.SiteLogService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author 代平 2017-05-06 创建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserManagerImpl implements UserManager
{
	static final Logger log = LogManager.getLogger(UserManagerImpl.class.getName());
	
	@Autowired
	private UserDAO userDao;
	
	@Autowired
	private SiteLogService siteLogService;
	
	@Autowired
	private CompanyDAO companyDao;
	
	@Autowired
	private UserDataTypeDAO userDataTypeDao;
	
	@Autowired
	private DataTypeDao dataTypeDao;
	/*----------------------------------登录 ----------------------------------*/
	
	/**
	 * 检查用户名是否有效：根据〔手机号，邮件，学号〕唯一定位用户
	 * 
	 * @param loginId
	 *            MobilePhone、 ECode
	 * @return 如果有匹配的，返回User Json包，否则，返回的json包isEmpty() == true
	 */
	@Override
	public JSONObject getByLoginId(String loginId) throws Exception
	{
		User entry = userDao.getByLoginId(loginId, false);
		
		// 返回的用户包含已删除的
		if (entry != null && entry.getState() > EnumUserState.deleted.byteObject())
		{
			JSONObject oentry = entry.toJson(true, false);
			return oentry;
		}
		
		return new JSONObject();
	}
	
	
	/**
	 * 用户登录
	 * 
	 * @param loginId
	 *            登录名：eCode, mobilePhone
	 * @param pwdEncrypted
	 *            密码（action接收密文）
	 */
	@Override
	public void login(HttpServletRequest req,HttpServletResponse resp, String loginId, String pwdEncrypted, Trace trace) throws Exception
	{
		User user = userDao.getByLoginId(loginId, true);
		
		// 返回的用户包含已删除的
		if (user == null || user.getState().equals(EnumUserState.deleted.byteObject()))
			throw new RuntimeException("用户不存在！");
		
		if (!user.getPwd().equals(pwdEncrypted))
			throw new RuntimeException("登录名或密码错误！");
		
		// 检查账号 状态
		if (user.getState().byteValue() < EnumUserState.registered.byteValue())
			throw new RuntimeException("用户帐号不可用！");
		
		List<UserDatatype> dataTypes = userDataTypeDao.getByUserId(user.getSysId(), false);
		if(dataTypes != null){
			Integer[] dataTypeIds = new Integer[dataTypes.size()];
			for(int i = 0; i < dataTypes.size(); i++){
				dataTypeIds[i] = dataTypes.get(i).getDatatypeId();
			}
			user.setDataTypes(dataTypeIds);
		}
		XmcManagerImpl.addAuthentication(resp, user);
		
		//添加site_log日志
		SiteLog siteLog = new SiteLog();
		//设置访问ip
		siteLog.setCreatedIp(RequestUtils.getIpAddr(req));
		//设置访问客户端
		siteLog.setUseClientType(EnumClientType.browser.byteObject());
		siteLog.setKind(EnumSiteLogKind.login.byteObject());
		siteLog.setUserId(user.getSysId());
		siteLog.setCompanyId(user.getCompanyId());
		siteLog.setOwnerGovId(user.getOwnerGovId());
		siteLog.setUseDeviceType("");
		siteLog.setBody("用户登录");
		siteLogService.saveSiteLog(siteLog);
		userDao.updateLogin(user);
	}
	
	/**
	 * 用户注销。不抛出异常
	 */
	@Override
	public void logoff(HttpServletRequest req, HttpServletResponse resp, Trace trace)
	{
		McPerson mcPerson = XmcManagerImpl.getByAuthKey(req);
		User user;
		try {
			user = userDao.getById(mcPerson.getSysId(), EnumUserState.active, false);
			//添加site_log日志
			SiteLog siteLog = new SiteLog();
			//设置访问ip
			siteLog.setCreatedIp(RequestUtils.getIpAddr(req));
			//设置访问客户端
			siteLog.setUseClientType(EnumClientType.browser.byteObject());
			siteLog.setKind(EnumSiteLogKind.login.byteObject());
			siteLog.setUserId(user.getSysId());
			siteLog.setCompanyId(user.getCompanyId());
			siteLog.setOwnerGovId(user.getOwnerGovId());
			siteLog.setUseDeviceType("");
			siteLog.setBody("用户注销");
			siteLogService.saveSiteLog(siteLog);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		XmcManagerImpl.removeAuthentication(req, resp, true);
	}
	
	/*---------------------------------- 后台新建用户、编辑用户信息、编辑提醒设置、修改密码、安全问题、找回密码  ----------------------------------*/
	/**
	 * （后台管理界面）企业管理 -> 用户 -> 编辑，提取用户详细信息
	 * 
	 * @param loginUser
	 *            登录的后台用户
	 * @param userId
	 *            编辑哪位用户
	 */
	@Override
	public JSONObject getById(Person loginUser, int userId) throws Exception
	{
		User entry = checkUser(userId, false, EnumUserState.registered, EnumUserState.frozen, EnumUserState.active);
		
		JSONObject oentry = entry.toJson(true, true);
		
		return oentry;
	}
	
	/**
	 * 个人中心 -> 修改个人资料，提取用户详细信息
	 * 
	 * @param loginPerson
	 *            登录的用户
	 */
	@Override
	public JSONObject getById(Person loginUser) throws Exception
	{
		User entry = checkUser(loginUser.getSysId(), false, EnumUserState.registered, EnumUserState.frozen, EnumUserState.active);
		
		JSONObject oentry = entry.toJson(false, true);
		Company company = companyDao.getCompanyById(entry.getCompanyId(), EnumCompanyState.closed, false);
		if(company != null){
			oentry.putAll(company.toJson(false, true));
		}
		JSONArray dataTypes = BaseEntity.list2JsonArray(dataTypeDao.getDataTypeList(EnumGenericState.active), false, true);
		oentry.put("dataTypes", dataTypes);
		
		List<UserDatatype> userDataTypeList = userDataTypeDao.getByUserId(loginUser.getSysId(), false);
		if(userDataTypeList != null && userDataTypeList.size() > 0){
			oentry.put("userDataTypes", BaseEntity.list2JsonArray(userDataTypeList, false, true));
		}
		return oentry;
	}
	
	
	/**
	 * 用户第一次登陆时修改密码
	 * 
	 * @param loginPerson
	 *            登录用户
	 * @param oldPwdEncrypted
	 *            用户输入的现有密码（密文）
	 * @param newPwdClean
	 *            新密码（明文）
	 */
	@Override
	public void firstChangePassword(String loginId, String oldPwdEncrypted, String newPwdClean, Trace trace) throws Exception
	{
		User user = userDao.getByLoginId(loginId, true);
		
		// 返回的用户包含已删除的
		if (user == null || user.getState().equals(EnumUserState.deleted.byteObject()))
			throw new RuntimeException("用户不存在！");
		
		if (!user.getPwd().equals(oldPwdEncrypted))
			throw new RuntimeException("登录名或密码错误！");
		
		// 检查账号 状态
		if (user.getState().byteValue() < EnumUserState.registered.byteValue())
			throw new RuntimeException("用户帐号不可用！");
		
		userDao.updatePwd(user, oldPwdEncrypted, new Md5PwdEncoder().encodePassword(newPwdClean));
		
		userDao.updateLogin(user);
		
	}
	
	/**
	 * 用户修改密码
	 * 
	 * @param loginPerson
	 *            登录用户
	 * @param oldPwdEncrypted
	 *            用户输入的现有密码（密文）
	 * @param newPwdClean
	 *            新密码（明文）
	 */
	@Override
	public void changePassword(Person loginUser, String oldPwdEncrypted, String newPwdClean, Trace trace) throws Exception
	{
		User user = checkUser(loginUser.getSysId(), true, EnumUserState.registered, EnumUserState.frozen, EnumUserState.active);
		
		userDao.updatePwd(user, oldPwdEncrypted, new Md5PwdEncoder().encodePassword(newPwdClean));
		
	}
	
	
	private User checkUser(int userId, boolean lock, EnumUserState... alloweState) throws Exception
	{
		boolean foundState = false;
		User user = userDao.getById(userId, EnumUserState.closed, lock);
		if (user == null)
			throw new RuntimeException(String.format("无法找到用户(%d)！", userId));
		
		// 用户当前状态是否在alloweState中？
		if (alloweState != null && alloweState.length > 0)
		{
			for (EnumUserState sta : alloweState)
			{
				if (user.getState().equals(sta.byteValue()))
				{
					foundState = true;
					break;
				}
			}
			
			if (!foundState)
				throw new RuntimeException("您的 账户状态不允许此操作！");
		}
		
		return user;
	}
	
	@Override
	public void deleteUser(Integer userId) throws Exception
	{
		User user = userDao.getById(userId, EnumUserState.deleted, true);
		
		userDao.deleteUser(user);
		
	}

	/**
	 * 用户修改个人基本信息
	 * 	1、修改用户信息
	 * 	2、修改企业信息
	 * 	3、刷新缓存
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
	@Override
	public void saveUserInfo(HttpServletRequest req,HttpServletResponse resp, Person person, String userName, String mobileNumber, String email, String companyName,
			String companyCode, String officPhone, String address) throws Exception {

		User user = userDao.getById(person.getSysId(), EnumUserState.active, true);
		if(user == null)
			throw new RuntimeException("未找到所登录的用户信息，请重新登录后再尝试此操作！");
		
		Company company = companyDao.getCompanyById(user.getCompanyId(), EnumCompanyState.draft, true);

		if(company == null)
			throw new RuntimeException("未找到登录用户所属企业，或该企业已被关闭，请重新登录后再尝试此操作！");
		
		user.setUserName(userName);
		if(!mobileNumber.equals(user.getMobileNumber())){
			User checkUser = userDao.getByLoginId(mobileNumber, false);
			if(checkUser != null)
				throw new RuntimeException("该手机号已被注册，请选择其他手机号码！");
			user.setMobileNumber(mobileNumber);
		}
		userDao.update(user);
		
		//更新缓存数据
		XmcManagerImpl.updateAuthentication(user);
		
		company.setTitle(companyName);
		company.setEmail(email);
		company.setCompanyCode(companyCode);
		company.setOfficPhone(officPhone);
		company.setAddress(address);
		companyDao.saveOrUpdate(company);
	}

	/**
	 * 用户修改个人基本信息
	 * 	查询目前用户所有定制消息口，循环查询已有的，没有的再已有的基础上做更改或者添加
	 * 
	 * @param dataTypeIds
	 *            定制消息口ID
	 */
	@Override
	public void saveUserDataType(HttpServletRequest req, HttpServletResponse resp, Person person, Integer[] dataTypeIds)
			throws Exception {
		User user = userDao.getById(person.getSysId(), EnumUserState.active, false);
		if(user == null)
			throw new RuntimeException("未找到操作用户，或该用户已被删除！");
		List<UserDatatype> userDataTypes = userDataTypeDao.getByUserId(person.getSysId(), true);
		List<UserDatatype> matchTypes = new ArrayList<UserDatatype>();
		dataTypeIds = dataTypeIds == null ? new Integer[]{} : dataTypeIds;
		Boolean isMatch = false;
		//循环找出删除的定制消息口，并删除
		for(UserDatatype udt : userDataTypes){
			isMatch = false;
			for(Integer typeId : dataTypeIds){
				if(typeId == udt.getDatatypeId()){
					isMatch = true;
					break;
				}
			}
			if(!isMatch){
				userDataTypeDao.delete(udt);
			} else {
				matchTypes.add(udt);
			}
		}
		//循环找出新增的定制消息口，并新增
		UserDatatype userDataType = null;
		for(Integer typeId : dataTypeIds){
			isMatch = false;
			for(UserDatatype udt : matchTypes){
				if(typeId == udt.getDatatypeId()){
					isMatch = true;
					break;
				}
			}
			if(!isMatch){
				userDataType = new UserDatatype();
				userDataType.setDatatypeId(typeId);
				userDataType.setUserId(person.getSysId());
				userDataTypeDao.saveOrUpdate(userDataType);
			}
		}
		user.setDataTypes(dataTypeIds);
		//更新缓存数据
		XmcManagerImpl.updateAuthentication(user);
	}


	/**
	 * 根据用户ID取得定制消息口
	 * @param userId
	 * @param lock
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONArray getUserDataTypeByUserId(int userId, Boolean lock) throws Exception {
		JSONArray jsonArray = userDataTypeDao.getJSONByUserId(userId, false);
		return jsonArray;
	}

}
