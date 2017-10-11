package cn.tongyuankeji.dao.structure;

import java.util.List;

import cn.tongyuankeji.entity.structure.Role;

import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.dao.BaseDAO;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;

public interface RoleDAO extends BaseDAO<Role>
{
	public abstract Role getById(int sysId, boolean lock) throws Exception;

	public abstract List<Role> getByState(EnumGenericState StateMin) throws Exception;

	/**
	 * 新建角色保存。此方法内部设置：eCode, modifiedAt, modifiedByFullname
	 */
	public abstract void insert(Role transientInstance, Person createUser, Trace trace) throws Exception;

	public abstract void initCreateSystemRole(Role transientInstance, String userName) throws Exception;
	/**
	 * 编辑角色保存。此方法内部修改： modifiedAt, modifiedByFullname
	 */
	public abstract void update(Role persistentInstance, Person modifyUser, Trace trace) throws Exception;
	
	public abstract int updateState(Role persistentInstance, EnumGenericState toState, Trace trace) throws Exception;

}