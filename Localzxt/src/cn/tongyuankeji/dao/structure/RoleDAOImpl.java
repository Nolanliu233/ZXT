package cn.tongyuankeji.dao.structure;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import cn.tongyuankeji.common.parameters.EnumLogDomainTable;
import cn.tongyuankeji.common.parameters.EnumLogDomainType;
import cn.tongyuankeji.entity.structure.Role;
import org.springframework.stereotype.Repository;

import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.dao.BaseDAOImpl;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;

/**
 * Role DAO
 * 
 * @author 代平 2017-05-04 创建
 */
@Repository
public class RoleDAOImpl extends BaseDAOImpl<Role, Integer> implements RoleDAO
{
	public RoleDAOImpl()
	{
		super(Role.class, "sysId");
	}

	@Override
	public Role getById(int sysId, boolean lock) throws Exception
	{
		return super.getById(sysId, EnumGenericState.hidden.byteObject(), lock);
	}

	@Override
	public List<Role> getByState(EnumGenericState StateMin) throws Exception
	{
		assert StateMin != null : "getByState(EnumGenericState)参数 StateMin不能为空！";

		return super.getEntityPage(new Criterion[] { Restrictions.ge("state", StateMin.byteObject()) }, null, null, null);
	}

	/**
	 * 新建角色保存。此方法内部设置：eCode, modifiedAt, modifiedByFullname
	 */
	@Override
	public void insert(Role transientInstance, Person createUser, Trace trace) throws Exception
	{
		assert createUser != null : "insert(Role, Person, Trace)参数createUser不能为空！";
		assert trace != null : "insert(Role, Person, Trace)参数trace不能为空！";

		transientInstance.setCode(this.getNexteCode());
		transientInstance.setModifiedAt(super.now());
		transientInstance.setModifiedByFullname(createUser.getUserName());
		transientInstance.setModifiedBy(createUser.getSysId());
		getSession().save(transientInstance);

		trace.setDomain(
				EnumLogDomainType.role.byteValue(),
				EnumLogDomainTable.role.byteValue(),
				transientInstance.getSysId().toString(),
				"新建角色：%s(%d)",
				transientInstance.getTitle(),
				transientInstance.getSysId());
		super.addTrace(trace, transientInstance);
	}
	
	/**
	 * 新建角色保存。此方法内部设置：eCode, modifiedAt, modifiedByFullname
	 */
	@Override
	public void initCreateSystemRole(Role transientInstance, String userName) throws Exception
	{
		transientInstance.setCode(this.getNexteCode());
		transientInstance.setModifiedAt(super.now());
		transientInstance.setModifiedBy(0);
		transientInstance.setModifiedByFullname(userName);
		getSession().save(transientInstance);
	}

	/**
	 * 编辑角色保存。此方法内部修改： modifiedAt, modifiedByFullname
	 */
	@Override
	public void update(Role persistentInstance, Person modifyUser, Trace trace) throws Exception
	{
		assert modifyUser != null : "update(Role, Person, Trace)参数modifyUser不能为空！";
		assert trace != null : "update(Role, Person, Trace)参数trace不能为空！";

		persistentInstance.setModifiedAt(super.now());
		persistentInstance.setModifiedByFullname(modifyUser.getUserName());
		getSession().update(persistentInstance);

		trace.setDomain(
				EnumLogDomainType.role.byteValue(),
				EnumLogDomainTable.role.byteValue(),
				persistentInstance.getSysId().toString(),
				"编辑角色：%s(%d)",
				persistentInstance.getTitle(),
				persistentInstance.getSysId());
		super.addTrace(trace, persistentInstance);
	}

	@Override
	public int updateState(Role persistentInstance, EnumGenericState toState, Trace trace) throws Exception
	{
		assert persistentInstance != null : "updateState(Role, EnumGenericState, Trace)参数persistentInstance不能为空！";
		assert toState != null : "updateState(Role, EnumGenericState, Trace)参数toState不能为空！";
		assert trace != null : "updateState(Role, EnumGenericState, Trace)参数trace不能为空！";

		int affected = super.createSQLQuery(
				"UPDATE role SET state = %d WHERE sys_Id = %d AND state > %d", 
				toState.byteValue(), 
				persistentInstance.getSysId(),
				EnumGenericState.deleted.byteObject())
				.executeUpdate();

		trace.setDomain(
				EnumLogDomainType.role.byteValue(),
				EnumLogDomainTable.role.byteValue(),
				persistentInstance.getSysId().toString(),
				(toState.byteValue() < persistentInstance.getState().byteValue()) ? "停用/删除角色：%s(%d)" : "启用角色：%s(%d)",
				persistentInstance.getTitle(),
				persistentInstance.getSysId());
		super.addTrace(trace);

		return affected;
	}

	private String getNexteCode() throws Exception
	{
		return super.getNexteCode("code", null, 3);
	}
}