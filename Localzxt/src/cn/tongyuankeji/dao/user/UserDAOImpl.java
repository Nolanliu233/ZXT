package cn.tongyuankeji.dao.user;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import cn.tongyuankeji.entity.user.User;

import org.springframework.stereotype.Repository;

import cn.tongyuankeji.common.parameters.EnumUserState;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.dao.AbstractDAOImpl;
import cn.tongyuankeji.dao.BaseDAOImpl;

/**
 * User DAO，必须实现
 * @author 代平 2017-05-04 创建
 */
@Repository
public class UserDAOImpl extends BaseDAOImpl<User, Integer> implements UserDAO
{
	public UserDAOImpl()
	{
		super(User.class, "sysId");
	}
	
	/**
	 * 根据id查找用户
	 */
	@Override
	public User getById(int userId, EnumUserState stateMin, boolean lock) throws Exception
	{
		return super.getById(userId, stateMin.byteObject(), lock);
	}
	
	/**
	 * 用登录字段查找用户：注册手机号、学号，不排除deleted状态的
	 * 
	 * @param loginId
	 *            注册手机号、学号
	 */
	@Override
	public User getByLoginId(String loginId, boolean lock) throws Exception
	{
		assert !Utils.isBlank(loginId) : "getByLoginId(String, boolean, boolean)参数loginId不能为空！";
		return super.getFirst(
				new Criterion[]
				{
				Restrictions.disjunction()
						.add(Restrictions.eq("loginName", loginId))
						.add(Restrictions.eq("mobileNumber", loginId))
				},
				null,
				lock);
	}
	
	/**
	 * 按Student.SysId取多个用户
	 */
	@Override
	public List<User> getByIds(Integer[] userIds, EnumUserState stateMin, boolean lock) throws Exception
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
	 * 用户注册，同时创建StudentStat。StudentEx由Manager单独保存。<br />
	 * 方法内部设置：ECode、CreatedAt
	 */
	@Override
	public void insert(User transientInstance) throws Exception
	{
		super.getSession().save(transientInstance);
		
		// 同时创建UserEx，数据库中其他字段默认值为null
//		super.createSQLQuery("INSERT INTO user_ex (SysId) VALUES (%d)", transientInstance.getSysId())
//				.executeUpdate();
	}
	
	/**
	 * 用户修改个人信息保存。StudentEx更新由manager执行
	 */
	@Override
	public void update(User persistentInstance) throws Exception
	{
		getSession().update(persistentInstance);
	}
	
	/**
	 * 用户登录后，更新数据库最新登录XXX。不抛出异常
	 */
	@Override
	public int updateLogin(User persistentInstance)
	{
		assert persistentInstance != null : "updateLogin(User, Trace)参数persistentInstance不能为空！";
		int affected = 0;
		
		try
		{
			affected = super.createSQLQuery("UPDATE user SET lastLogin_at = %s WHERE sys_id = %d", AbstractDAOImpl.SQL_NOW(), persistentInstance.getSysId())
					.executeUpdate();
		}
		catch (HibernateException ex)
		{
			// 这种错误可忽略
		}
		
		return affected;
	}
	
	
	/**
	 * 用户修改密码，或找回密码时填写新密码
	 * 
	 * @param oldPwdEncrypted
	 *            修改密码时，用户输入的现有密码（密文）必填；找回密码时，传入null
	 * @param newPwdEncrypted
	 *            新密码（密文）
	 */
	@Override
	public int updatePwd(User persistentInstance, String oldPwdEncrypted, String newPwdEncrypted) throws Exception
	{
		assert persistentInstance != null : "updatePwd(User, String, String, Trace)参数persistentInstance不能为空！";
		assert !Utils.isBlank(oldPwdEncrypted) : "updatePwd(User, String, String, Trace)参数studoldPwdEncryptedentInputOldPwd不能为空！";
		assert !Utils.isBlank(newPwdEncrypted) : "updatePwd(User, String, String, Trace)参数newPwdEncrypted不能为空！";
		
		if (!Utils.isBlank(oldPwdEncrypted))
		{
			if (!persistentInstance.getPwd().equals(oldPwdEncrypted))
				throw new RuntimeException("当前密码输入错误！");
		}
		
		int affected = super.createSQLQuery("UPDATE user SET pwd = :p WHERE sys_id = %d", persistentInstance.getSysId())
				.setString("p", newPwdEncrypted)
				.executeUpdate();
		
		return affected;
	}
	
	/**
	 * 上传或清空头像后，修改数据库相应字段
	 * 
	 * @param filename
	 *            用FileUploadManager上传的图片。清空时，传入null
	 */
	@Override
	public int updateFile(User persistentInstance, String filename, String fileExt) throws Exception
	{
		assert persistentInstance != null : "updateFile(User, String, String, Trace)参数persistentInstance不能为空！";
		
		int affected = super.createSQLQuery("UPDATE user SET ThumbFile = :f WHERE SysId = %d", persistentInstance.getSysId())
				.setString("f", filename)
				.executeUpdate();
		
		return affected;
	}
	
	@Override
	public void deleteUser(User user) throws Exception
	{
		super.createSQLQuery("DELETE user WHERE sys_id =:userId")
		.setParameter("userId", user.getSysId())
		.executeUpdate();
	}
}
