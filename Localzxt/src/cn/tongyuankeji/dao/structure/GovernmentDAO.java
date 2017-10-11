package cn.tongyuankeji.dao.structure;

import java.util.List;

import cn.tongyuankeji.entity.structure.Government;

import cn.tongyuankeji.common.parameters.EnumPersonState;
import cn.tongyuankeji.dao.BaseDAO;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;

public interface GovernmentDAO extends BaseDAO<Government>
{
	public abstract Government getById(int orgId, boolean lock) throws Exception;
	
	/**
	 * 按id返回园区**正常状态**的园区作为筛选条件（只在OrgControllerImpl中使用）
	 * 
	 * @param sysId1
	 *            第一个园区id，一般放默认园区id。不使用此条件时，传入null
	 * @param sysId2
	 *            第一个园区id，一般放私企园区id。不使用此条件时，传入null
	 */
	public abstract List<Government> getInIds(Integer sysId1, Integer sysId2) throws Exception;
	
	
	/**
	 * 新建园区。此方法内部设置：eCode, createdAt, createdByFullname, modifiedAt, modifiedByFullname。同时创建园区默认社区
	 * 
	 * @param transientInstance
	 *            新建的对象
	 * @param createUser
	 *            创建人
	 */
	public abstract void insert(Government transientInstance, String indusCode, Person createUser, Trace trace) throws Exception;
	
	/**
	 * 编辑园区。此方法内部设置： modifiedAt, modifiedByFullname
	 */
	public abstract void update(Government entry, Person modifyUser, Trace trace) throws Exception;
	
	/**
	 * 更新账务相关
	 */
	public abstract void updateAccount(Government persistentInstance) throws Exception;
	
	/**
	 * 上传或清空园区合同扫描件
	 * 
	 * @param persistentInstance
	 *            被更新的园区
	 * @param contractFile
	 *            用FileUploadManager上传的图片。清空时，传入null
	 * @param trace
	 *            上传时，传入null。清空时，传入SchemaBase产生的trace对象
	 */
	public abstract int updateFile(Government persistentInstance, String contractFile, String contractFileExt, Trace trace) throws Exception;
	
	/**
	 * 修改园区状态。当目标状态时删除时，同时园区下所有社区IMGroup.EStatus=deleted。Manager已检查这个园区下没有状态高于**删除**的用户
	 */
	public abstract int updateeStatus(Government persistentInstance, EnumPersonState toeStatus, Trace trace) throws Exception;
	
	/**
	 * 后台园区管理界面，查询园区列表，按编号升序排列
	 * 
	 * @param specialtyId
	 *            属于哪个行业。不使用此条件时，传入null
	 * @param eStatus
	 *            EnumPersonState状态
	 * @param eCode
	 *            园区编号（精确匹配）。不使用此条件时，传入null
	 * @param fuzzy
	 *            名称Title、联系地址OrgAddress、联系人姓名POCFullname（模糊匹配）
	 */
	public abstract List<Government> getByProperty(Integer specialtyId, EnumPersonState eStatus, String eCode, String fuzzy, int pageStart, int pageSize) throws Exception;
	
	public abstract int getCountByProperty(Integer specialtyId, EnumPersonState eStatus, String eCode, String fuzzy) throws Exception;
	
	
	public final static int COL_SYSID = 0;
	public final static int COL_ESTATUS = 1;
	public final static int COL_ECODE = 2;
	public final static int COL_TITLE = 3;
	public final static int COL_CREATED_AT = 4;
	public final static int COL_POC = 5;
}
