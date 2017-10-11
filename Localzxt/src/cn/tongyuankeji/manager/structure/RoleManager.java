package cn.tongyuankeji.manager.structure;

import net.sf.json.JSONObject;

import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.common.parameters.EnumMgrReturnType;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;

public interface RoleManager
{
	public abstract Object getByState(EnumMgrReturnType returnAs, EnumGenericState StateMin, Boolean fullJson) throws Exception;

	public abstract JSONObject getMask(boolean isSystemManager, boolean isSystemAutoCreate);

	public abstract int create(boolean isSystemManager, String title, String titleEn, String acl, String remarks, Person createUser, Trace trace) throws Exception;

	public abstract void update(int sysId, String title, String titleEn, String acl, String remarks, Person modifyUser, Trace trace) throws Exception;

	public abstract void changeState(int sysId, EnumGenericState toState, Trace trace) throws Exception;

	public abstract JSONObject getById(int sysId) throws Exception;

	public abstract void initRoleMap();
}
