package cn.tongyuankeji.dao.structure;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import cn.tongyuankeji.common.parameters.EnumLogDomainTable;
import cn.tongyuankeji.common.parameters.EnumLogDomainType;
import cn.tongyuankeji.entity.structure.Government;
import org.springframework.stereotype.Repository;

import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.common.parameters.EnumPersonState;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.dao.BaseDAOImpl;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.Trace;

/**
 * Government DAO
 * 
 * @author 代平 2017-05-15 创建
 */
@Repository
public class GovernmentDAOImpl extends BaseDAOImpl<Government, Integer> implements GovernmentDAO
{
	public GovernmentDAOImpl()
	{
		super(Government.class, "sysId");
	}
	
	/**
	 * 用Government.SysId找记录
	 */
	@Override
	public Government getById(int orgId, boolean lock) throws Exception
	{
		return super.getById(orgId, EnumPersonState.closed.byteObject(), lock);
	}
	
	/**
	 * 按id返回园区**正常状态**的园区作为筛选条件（只在OrgControllerImpl中使用）
	 * 
	 * @param sysId1
	 *            第一个园区id，一般放默认园区id。不使用此条件时，传入null
	 * @param sysId2
	 *            第一个园区id，一般放私企园区id。不使用此条件时，传入null
	 */
	@Override
	public List<Government> getInIds(Integer sysId1, Integer sysId2) throws Exception
	{
		ArrayList<Criterion> cris = new ArrayList<Criterion>(3);
		cris.add(Restrictions.eq("eStatus", EnumPersonState.active.byteObject()));
		if (sysId1 != null)
		{
			if (sysId2 != null)
			{
				cris.add(Restrictions.disjunction()
						.add(Restrictions.eq("sysId", sysId1))
						.add(Restrictions.eq("sysId", sysId2)));
			}
			else
				cris.add(Restrictions.eq("sysId", sysId1));
		}
		else
		{
			if (sysId2 != null)
				cris.add(Restrictions.eq("sysId", sysId2));
		}
		
		return super.getEntityPage(cris, Arrays.asList(Order.asc("eCode")), null, null);
	}
	
	
	/**
	 * 新建机构。此方法内部设置：eCode, createdAt, createdByFullname, modifiedAt, modifiedByFullname。同时创建园区默认社区
	 * 
	 * @param transientInstance
	 *            新建的对象
	 * @param createUser
	 *            创建人
	 */
	@Override
	public void insert(Government transientInstance, String indusCode, Person createUser, Trace trace) throws Exception
	{
		assert createUser != null : "insert(Government, String, Person, Trace)参数createUser不能为空！";
		assert trace != null : "insert(Government, String, Person, Trace)参数trace不能为空！";
		
		Timestamp actionAt = super.now();
		
		transientInstance.setCreateAt(actionAt);
		transientInstance.setCreatedBy(createUser.getSysId());
		transientInstance.setModifiedAt(actionAt);
		transientInstance.setModifiedBy(createUser.getSysId());
		getSession().save(transientInstance);
		
		super.createSQLQuery("INSERT INTO imgroup (Title, EStatus, OrgId, IsOrgDefault) VALUES (:t, %d, %d, TRUE)",
				EnumGenericState.active.byteObject(),
				transientInstance.getSysId())
				.setString("t", transientInstance.getTitle().length() > 20 ? transientInstance.getTitle().substring(0, 21) : transientInstance.getTitle())
				.executeUpdate();
		
		trace.setDomain(
				EnumLogDomainType.goverment.byteValue(),
				EnumLogDomainTable.organization.byteValue(),
				transientInstance.getSysId().toString(),
				"新建机构：%s(%d)",
				transientInstance.getTitle(),
				transientInstance.getSysId());
		super.addTrace(trace, transientInstance);
	}
	
	/**
	 * 编辑机构。此方法内部设置： modifiedAt, modifiedByFullname
	 */
	@Override
	public void update(Government persistentInstance, Person modifyUser, Trace trace) throws Exception
	{
		assert modifyUser != null : "update(Government, Person, Trace)参数modifyUser不能为空！";
		assert trace != null : "update(Government, Person, Trace)参数trace不能为空！";
		
		persistentInstance.setModifiedAt(super.now());
		persistentInstance.setModifiedBy(modifyUser.getSysId());
		getSession().update(persistentInstance);
		
		trace.setDomain(
				EnumLogDomainType.goverment.byteValue(),
				EnumLogDomainTable.organization.byteValue(),
				persistentInstance.getSysId().toString(),
				"编辑机构：%s(%d)",
				persistentInstance.getTitle(),
				persistentInstance.getSysId());
		super.addTrace(trace, persistentInstance);
	}
	
	/**
	 * 更新账务相关
	 */
	@Override
	public void updateAccount(Government persistentInstance) throws Exception
	{
		getSession().update(persistentInstance);
	}
	
	/**
	 * 上传或清空机构合同扫描件
	 * 
	 * @param persistentInstance
	 *            被更新的机构
	 * @param contractFile
	 *            用FileUploadManager上传的图片。清空时，传入null
	 */
	@Override
	public int updateFile(Government persistentInstance, String contractFile, String contractFileExt, Trace trace) throws Exception
	{
		assert persistentInstance != null : "updateContractFile(Government, String, String, Trace)参数persistentInstance不能为空！";
		assert trace != null : "updateContractFile(Government, String, String, Trace)参数trace不能为空！";
		
		int affected = super.createSQLQuery("UPDATE organization SET ContractFile = :c, ContractFileExt = :e WHERE SysId = :id")
				.setString("c", contractFile)
				.setString("e", contractFileExt)
				.setInteger("id", persistentInstance.getSysId())
				.executeUpdate();
		
		trace.setDomain(
				EnumLogDomainType.goverment.byteValue(),
				EnumLogDomainTable.organization.byteValue(),
				persistentInstance.getSysId().toString(),
				Utils.isEmpty(contractFile) ? "清空机构合同扫描件：%s(%d)" : "上传机构合同扫描件：%s(%d)",
				persistentInstance.getTitle(),
				persistentInstance.getSysId());
		super.addTrace(trace);
		
		return affected;
	}
	
	/**
	 * 修改机构状态。当目标状态时删除时，同时修改园区下所有社区IMGroup.EStatus=deleted。Manager已检查这个机构下没有状态高于**删除**的用户
	 */
	@Override
	public int updateeStatus(Government persistentInstance, EnumPersonState toeStatus, Trace trace) throws Exception
	{
		assert persistentInstance != null : "updateeStatus(Government, EnumPersonState, Trace)参数persistentInstance不能为空！";
		assert toeStatus != null : "updateeStatus(Government, EnumPersonState, Trace)参数toeStatus不能为空！";
		assert trace != null : "updateeStatus(Government, EnumPersonState, Trace)参数trace不能为空！";
		
		int affected = super.createSQLQuery(
				"UPDATE organization SET EStatus = %d WHERE SysId = %d AND EStatus > %d",
				toeStatus.byteValue(),
				persistentInstance.getSysId(),
				EnumPersonState.deleted.byteObject())
				.executeUpdate();
		
		if (affected > 0 && toeStatus == EnumPersonState.deleted)
		{
			// 不管这个园区有几个社区（包含默认社区），全部设置为“已删除”
			super.createSQLQuery("UPDATE imgroup SET EStatus = %d WHERE OrgId = %d", EnumPersonState.deleted.byteObject(), persistentInstance.getSysId())
					.executeUpdate();
		}
		
		trace.setDomain(
				EnumLogDomainType.goverment.byteValue(),
				EnumLogDomainTable.organization.byteValue(),
				persistentInstance.getSysId().toString(),
				(toeStatus.byteValue() < persistentInstance.getState().byteValue()) ? "冻结/关闭/删除机构：%s(%d)" : "启用/解冻机构：%s(%d)",
				persistentInstance.getTitle(),
				persistentInstance.getSysId());
		super.addTrace(trace);
		
		return affected;
	}
	
	/**
	 * 后台机构管理界面，查询机构列表，按编号升序排列
	 * 
	 * @param specialtyId
	 *            属于哪个行业。不使用此条件时，传入null
	 * @param eStatus
	 *            EnumPersonState状态
	 * @param eCode
	 *            机构编号（精确匹配）。不使用此条件时，传入null
	 * @param fuzzy
	 *            名称title、联系地址orgAddress、联系人姓名pOCFullname（模糊匹配）
	 */
	@Override
	public List<Government> getByProperty(Integer specialtyId, EnumPersonState eStatus, String eCode, String fuzzy, int pageStart, int pageSize) throws Exception
	{
		assert eStatus != EnumPersonState.deleted;
		
		return super.getEntityPage(
				formatByPropertyCriterion(specialtyId, eStatus, eCode, fuzzy),
				Arrays.asList(Order.asc("eCode")),
				pageStart,
				pageSize);
	}
	
	@Override
	public int getCountByProperty(Integer specialtyId, EnumPersonState eStatus, String eCode, String fuzzy) throws Exception
	{
		return super.getCount(formatByPropertyCriterion(specialtyId, eStatus, eCode, fuzzy));
	}
	
	private List<Criterion> formatByPropertyCriterion(Integer specialtyId, EnumPersonState eStatus, String eCode, String fuzzy)
	{
		ArrayList<Criterion> cris = new ArrayList<Criterion>(4);
		
		cris.add(Restrictions.eq("eStatus", eStatus.byteObject()));
		if (specialtyId != null)
			cris.add(Restrictions.eq("specialtyId", specialtyId));
		if (!Utils.isBlank(eCode))
			cris.add(Restrictions.eq("eCode", eCode));
		if (!Utils.isBlank(fuzzy))
			cris.add(Restrictions.disjunction()
					.add(Restrictions.like("title", fuzzy, MatchMode.ANYWHERE))
					.add(Restrictions.like("orgAddress", fuzzy, MatchMode.ANYWHERE))
					.add(Restrictions.like("pOCFullname", fuzzy, MatchMode.ANYWHERE)));
		
		return cris;
	}

}
