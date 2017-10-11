package cn.tongyuankeji.dao.structure;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import cn.tongyuankeji.common.parameters.EnumLogDomainTable;
import cn.tongyuankeji.common.parameters.EnumLogDomainType;
import cn.tongyuankeji.common.parameters.EnumLogOperatorKind;
import cn.tongyuankeji.entity.structure.BackUser;
import org.springframework.stereotype.Repository;

import cn.tongyuankeji.common.parameters.EnumPersonState;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.dao.AbstractDAOImpl;
import cn.tongyuankeji.dao.BaseDAOImpl;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;

/**
 * BackUser DAO
 * 
 * @author 代平 2017-05-11 创建
 */
@Repository
public class BackUserDAOImpl extends BaseDAOImpl<BackUser, Integer> implements BackUserDAO
{
	public BackUserDAOImpl()
	{
		super(BackUser.class, "sysId");
	}
	
	/**
	 * 根据id查找用户
	 */
	@Override
	public BackUser getById(int userId, boolean lock) throws Exception
	{
		return super.getById(userId, EnumPersonState.closed.byteObject(), lock);
	}
	
	/**
	 * 用户名字段查找用户，不排除已删除的
	 */
	@Override
	public BackUser getByUserName(String userName, boolean lock) throws Exception
	{
		assert !Utils.isBlank(userName) : "getByUsername(String, boolean)参数loginId不能为空！";
		
		return super.getFirst(
				new Criterion[] { Restrictions.eq("loginName", userName) },
				null,
				lock);
	}
	
	
	/**
	 * 返回BackUser.RoleId使用了roleId的BackUser记录
	 * 
	 * @param stateMin
	 *            只关心哪些BackUser.state及以上状态的
	 */
	@Override
	public List<BackUser> getByRoleId(int roleId, EnumPersonState stateMin, boolean lock) throws Exception
	{
		assert stateMin != null : "getByRoleId(int, EnumPersonState, boolean)参数stateMin不能为空！";
		
		return super.getEntityList(
				new Criterion[]
				{
						Restrictions.eq("roleId", new Integer(roleId)),
						Restrictions.ge("state", stateMin.byteObject())
				},
				null,
				lock);
	}
	
	/**
	 * 返回BackUser.OwnerGovId使用了ownerGovId的记录
	 * 
	 * @param stateMin
	 *            只关心哪些BackUser.state及以上状态的
	 */
	@Override
	public List<BackUser> getByGovernmentId(int ownerGovId, EnumPersonState stateMin, boolean lock) throws Exception
	{
		assert stateMin != null : "getByGovernmentId(int, EnumPersonState, boolean)参数stateMin不能为空！";
		
		return super.getEntityList(
				new Criterion[]
				{
						Restrictions.eq("ownerGovId", ownerGovId),
						Restrictions.ge("state", stateMin.byteObject())
				},
				null,
				lock);
	}
	
	
	/**
	 * 按BackUser.SysId取多个用户
	 */
	@Override
	public List<BackUser> getByIds(Integer[] userIds, EnumPersonState stateMin, boolean lock) throws Exception
	{
		if (stateMin != null)
		{
			return super.getEntityList(
					new Criterion[]
					{
							Restrictions.in("sysId", userIds),
							Restrictions.ge("state", stateMin.byteObject())
					},
					null,
					lock);
		}
		else
		{
			return super.getEntityList(
					new Criterion[] { Restrictions.in("sysId", userIds) },
					null,
					lock);
		}
	}
	
	/**
	 * 新建用户<br />
	 * 此方法内部设置：createdAt, createdByFullname
	 * 
	 * @param lstLeafDept
	 *            用户关联的末级部门，可能为空。外面manager已检查是末级部门，且加锁
	 */
	@Override
	public void insert(BackUser transientInstance, Person createUser, Trace trace) throws Exception
	{
		assert createUser != null : "insert(BackUser, Person, Trace)参数createUser不能为空！";
		assert trace != null : "insert(BackUser, Person, Trace)参数trace不能为空！";
		
		transientInstance.setCreatedAt(super.now());
		transientInstance.setCreatedByFullname(createUser.getUserName());
		getSession().save(transientInstance);
		
		trace.setDomain(
				EnumLogDomainType.back_user.byteValue(),
				EnumLogDomainTable.back_user.byteValue(),
				transientInstance.getSysId().toString(),
				"新建用户：%s(%d)",
				transientInstance.getUserName(),
				transientInstance.getSysId());
		super.addTrace(trace, transientInstance);
	}
	
	
	/**
	 * 新建用户<br />
	 * 此方法内部设置：createdAt, createdByFullname
	 * 
	 * @param lstLeafDept
	 *            用户关联的末级部门，可能为空。外面manager已检查是末级部门，且加锁
	 */
	@Override
	public void initCreateSystemUser(BackUser transientInstance, String userName) throws Exception
	{
		assert userName != null : "insert(BackUser, userName)参数userName不能为空！";
		
		transientInstance.setCreatedAt(super.now());
		transientInstance.setCreatedByFullname(userName);
		getSession().save(transientInstance);
	}
	/**
	 * 编辑用户
	 */
	@Override
	public void update(BackUser persistentInstance, Trace trace) throws Exception
	{
		assert trace != null : "update(BackUser, Trace)参数trace不能为空！";
		
		getSession().update(persistentInstance);
		
		trace.setDomain(
				EnumLogDomainType.back_user.byteValue(),
				EnumLogDomainTable.back_user.byteValue(),
				persistentInstance.getSysId().toString(),
				"编辑用户：%s(%d)",
				persistentInstance.getUserName(),
				persistentInstance.getSysId());
		super.addTrace(trace, persistentInstance);
	}
	
	/**
	 * 用户登录后，更新数据库最新登录XXX。不抛出异常
	 */
	@Override
	public int updateLogin(BackUser persistentInstance, Trace trace)
	{
		assert persistentInstance != null : "updateLogin(BackUser, Trace)参数persistentInstance不能为空！";
		assert trace != null : "updateLogin(BackUser, Trace)参数trace不能为空！";
		
		int affected = 0;
		
		try
		{
			affected = super.createSQLQuery("UPDATE back_user SET recent_login_at = %s, recent_login_IP = :p WHERE sys_Id = %d", AbstractDAOImpl.SQL_NOW(), persistentInstance.getSysId())
					.setString("p", trace.getActionIP())
					.executeUpdate();
		}
		catch (HibernateException ex)
		{
			// 这种错误可忽略
		}
		
		trace.setDomain(
				EnumLogDomainType.back_user.byteValue(),
				EnumLogDomainTable.back_user.byteValue(),
				persistentInstance.getSysId().toString(),
				"用户登录：%s(%d)",
				persistentInstance.getUserName(),
				persistentInstance.getSysId());
		trace.relaceOperatorWhenLogin(persistentInstance);
		super.addTrace(trace);
		
		return affected;
	}
	
	/**
	 * 用户注销。不抛出异常
	 */
	@Override
	public void updateLogoff(Person persistentInstance, Trace trace)
	{
		assert trace != null : "updateLogoff(Student, Integer, Trace)参数trace不能为空！";
		
		trace.setDomain(
				EnumLogDomainType.back_user.byteValue(),
				EnumLogDomainTable.back_user.byteValue(),
				persistentInstance.getSysId().toString(),
				"用户注销：%s(%d)",
				persistentInstance.getUserName(),
				persistentInstance.getSysId());
		super.addTrace(trace);
	}
	
	/**
	 * 后台用户（自己）修改密码
	 * 
	 * @param oldPwdEncrypted
	 *            用户输入的当前密码（密文）
	 * @param newPwdEncrypted
	 *            新密码（密文）
	 */
	@Override
	public int updatePwd(BackUser persistentInstance, String oldPwdEncrypted, String newPwdEncrypted, Trace trace) throws Exception
	{
		assert persistentInstance != null : "updatePwd(BackUser, String, String, Trace)参数persistentInstance不能为空！";
		assert !Utils.isBlank(oldPwdEncrypted) : "updatePwd(BackUser, String, String, Trace)参数oldPwdEncrypted不能为空！";
		assert !Utils.isBlank(newPwdEncrypted) : "updatePwd(BackUser, String, String, Trace)参数newPwdEncrypted不能为空！";
		assert trace != null : "updatePwd(BackUser, String, String, Trace)参数trace不能为空！";
		
		if (!oldPwdEncrypted.equals(persistentInstance.getPwd()))
			throw new RuntimeException("当前密码输入错误！");
		
		int affected = super.createSQLQuery("UPDATE backuser SET Pwd = :p WHERE SysId = %d", persistentInstance.getSysId())
				.setString("p", newPwdEncrypted)
				.executeUpdate();
		
		trace.setDomain(
				EnumLogDomainType.back_user.byteValue(),
				EnumLogDomainTable.back_user.byteValue(),
				persistentInstance.getSysId().toString(),
				"修改密码：%s(%d)",
				persistentInstance.getUserName(),
				persistentInstance.getSysId());
		super.addTrace(trace);
		
		return affected;
	}
	
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
	@Override
	public int updateSecurity(BackUser persistentInstance, String pwdEncrypted, String securityQ, String securityA, Trace trace) throws Exception
	{
		assert persistentInstance != null : "updateSecurity(BackUser, String, String, String, Trace)参数persistentInstance不能为空！";
		assert !Utils.isBlank(securityQ) : "updateSecurity(BackUser, String, String, String, Trace)参数securityQ不能为空！";
		assert !Utils.isBlank(securityA) : "updateSecurity(BackUser, String, String, String, Trace)参数securityA不能为空！";
		assert trace != null : "updateSecurity(BackUser, String, String, String, Trace)参数trace不能为空！";
		
		if (!pwdEncrypted.equals(persistentInstance.getPwd()))
			throw new RuntimeException("当前密码输入错误！");
		
		int affected = super.createSQLQuery("UPDATE backuser SET SecurityQ = :q, SecurityA = :a WHERE SysId = %d", persistentInstance.getSysId())
				.setString("q", securityQ)
				.setString("a", securityA)
				.executeUpdate();
		
		trace.setDomain(
				EnumLogDomainType.back_user.byteValue(),
				EnumLogDomainTable.back_user.byteValue(),
				persistentInstance.getSysId().toString(),
				"修改密码安全问题：%s(%d)",
				persistentInstance.getUserName(),
				persistentInstance.getSysId());
		super.addTrace(trace);
		
		return affected;
	}
	
	/**
	 * 上传或清空用户头像
	 * 
	 * @param filename
	 *            用FileUploadManager上传的图片。清空时，传入null
	 */
	@Override
	public int updateFile(BackUser persistentInstance, String filename, Trace trace) throws Exception
	{
		assert persistentInstance != null : "updateFile(BackUser, String, String, Trace)参数persistentInstance不能为空！";
		assert trace != null : "updateFile(BackUser, String, String, Trace)参数trace不能为空！";
		
		int affected = super.createSQLQuery("UPDATE backuser SET ThumbFile = :f WHERE SysId = %d", persistentInstance.getSysId())
				.setString("f", filename)
				.executeUpdate();
		
		trace.setDomain(
				EnumLogDomainType.back_user.byteValue(),
				EnumLogDomainTable.back_user.byteValue(),
				persistentInstance.getSysId().toString(),
				Utils.isEmpty(filename) ? "清空用户头像图片：%s(%d)" : "上传用户头像图片：%s(%d)",
				persistentInstance.getUserName(),
				persistentInstance.getSysId());
		super.addTrace(trace);
		
		return affected;
	}
	
	/**
	 * 修改用户状态。当目标状态时删除时，同时删除与所有社区对应关系IMPush
	 */
	@Override
	public int updateState(BackUser persistentInstance, EnumPersonState toState, Trace trace) throws Exception
	{
		assert persistentInstance != null : "updatestate(BackUser, EnumPersonState, Trace)参数persistentInstance不能为空！";
		assert toState != null : "updatestate(BackUser, EnumPersonState, Trace)参数toState不能为空！";
		assert trace != null : "updatestate(BackUser, EnumPersonState, Trace)参数trace不能为空！";
		
		int affected = super.createSQLQuery(
				"UPDATE backuser SET State = %d WHERE SysId = %d AND Status > %d",
				toState.byteValue(),
				persistentInstance.getSysId(),
				EnumPersonState.deleted.byteObject())
				.executeUpdate();
		
		if (affected > 0 && toState == EnumPersonState.deleted)
			super.exec("syncIMMember", false, persistentInstance.getSysId(), EnumLogOperatorKind.user.byteObject());
		
		trace.setDomain(
				EnumLogDomainType.back_user.byteValue(),
				EnumLogDomainTable.back_user.byteValue(),
				persistentInstance.getSysId().toString(),
				(toState.byteValue() < persistentInstance.getState().byteValue()) ? "冻结/关闭/删除用户：%s(%d)" : "启用/解冻用户：%s(%d)",
				persistentInstance.getUserName(),
				persistentInstance.getSysId());
		super.addTrace(trace);
		
		return affected;
	}
}
