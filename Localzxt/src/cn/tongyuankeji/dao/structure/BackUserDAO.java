package cn.tongyuankeji.dao.structure;

import java.util.List;

import cn.tongyuankeji.entity.structure.BackUser;
import cn.tongyuankeji.common.parameters.EnumPersonState;
import cn.tongyuankeji.dao.BaseDAO;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;

public interface BackUserDAO extends BaseDAO<BackUser>
{
	/**
	 * 根据id查找用户
	 */
	public abstract BackUser getById(int userId, boolean lock) throws Exception;
	
	/**
	 * 用户名字段查找用户，不排除已删除的
	 */
	public abstract BackUser getByUserName(String userName, boolean lock) throws Exception;
	
	
	/**
	 * 返回BackUser.RoleId使用了roleId的BackUser记录
	 * 
	 * @param stateMin
	 *            只关心哪些BackUser.state及以上状态的
	 */
	public abstract List<BackUser> getByRoleId(int roleId, EnumPersonState stateMin, boolean lock) throws Exception;
	
	/**
	 * 返回BackUser.OwnerGovId使用了ownerGovId的记录
	 * 
	 * @param stateMin
	 *            只关心哪些BackUser.state及以上状态的
	 */
	public abstract List<BackUser> getByGovernmentId(int ownerGovId, EnumPersonState stateMin, boolean lock) throws Exception;
	
	
	public final static int COL_SYSID = 0;
	public final static int COL_USERNAME = 1;
	public final static int COL_FULLNAME = 2;
	public final static int COL_GENDER = 3;
	public final static int COL_OWNER_ORG_ID = 4;
	public final static int COL_ESTATUS = 5;
	
	
	public final static int COL_DEPT_TITLE = 3;
	
	/**
	 * 按BackUser.SysId取多个用户
	 */
	public abstract List<BackUser> getByIds(Integer[] userIds, EnumPersonState stateMin, boolean lock) throws Exception;
	
	/**
	 * 新建用户<br />
	 * 此方法内部设置：createdAt, createdByFullname
	 */
	public abstract void insert(BackUser transientInstance, Person createUser, Trace trace) throws Exception;
	
	/**
	 * 编辑用户
	 * 
	 */
	public abstract void update(BackUser persistentInstance, Trace trace) throws Exception;
	
	/**
	 * 用户登录后，更新数据库最新登录XXX。不抛出异常
	 */
	public abstract int updateLogin(BackUser persistentInstance, Trace trace);
	
	/**
	 * 用户注销。不抛出异常
	 */
	public abstract void updateLogoff(Person persistentInstance, Trace trace);
	
	/**
	 * 上传或清空用户头像
	 * 
	 * @param filename
	 *            用FileUploadManager上传的图片。清空时，传入null *
	 * @param trace
	 *            上传时，传入null。清空时，传入SchemaBase产生的trace对象
	 */
	public abstract int updateFile(BackUser persistentInstance, String filename, Trace trace) throws Exception;
	
	/**
	 * 修改用户状态。当目标状态时删除时，同时删除与所有社区对应关系IMPush
	 */
	public abstract int updateState(BackUser persistentInstance, EnumPersonState toState, Trace trace) throws Exception;
	
	/**
	 * 后台用户（自己）修改密码
	 * 
	 * @param oldPwdEncrypted
	 *            用户输入的当前密码（密文）
	 * @param newPwdEncrypted
	 *            新密码（密文）
	 */
	public abstract int updatePwd(BackUser persistentInstance, String oldPwdEncrypted, String newPwdEncrypted, Trace trace) throws Exception;
	
	/**
	 * 后台用户（自己）修改密保安全问题
	 * 
	 * @param pwdEncrypted
	 *            用户输入的当前密码（密文）
	 * @param securityQ
	 *            密码安全问题
	 * @param securityA
	 *            密码问题回答
	 */
	public abstract int updateSecurity(BackUser persistentInstance, String pwdEncrypted, String securityQ, String securityA, Trace trace) throws Exception;

	public abstract void initCreateSystemUser(BackUser transientInstance, String userName) throws Exception;
}