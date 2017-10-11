package cn.tongyuankeji.dao.log;

import java.sql.Timestamp;
import java.util.List;

import cn.tongyuankeji.entity.log.SiteLog;

public interface SiteLogDao {

	/**
	 * 新增或修改站点日志
	 * @param siteLog
	 */
	public void saveOrUpdateSiteLog(SiteLog siteLog);
	
	/**
	 * 根据ID得到站点日志
	 * @param siteLogId
	 * @return SiteLog
	 */
	public SiteLog getSiteLogById(int siteLogId) throws Exception;
	
	/**
	 * 分页查询站点日志列表
	 * @param nowPage(当前页)
	 * @param pageSize(每页显示条数)
	 * @param userId(用户ID)
	 * @param clientType(客户端类型)
	 * @param companyId(公司ID)
	 * @param govId(管委会ID)
	 * @return List<SiteLog>
	 */
	public List<SiteLog> getSiteLogList(int nowPage,int pageSize,int userId,Byte clientType,int companyId,int govId,Timestamp startTime,Timestamp endTime) throws Exception;

	/**
	 * 得到站点日志总数量
	 * @param userId(用户ID)
	 * @param clientType(客户端类型)
	 * @param companyId(公司ID)
	 * @param govId(管委会ID)
	 * @return count
	 * @throws Exception
	 */
	public int getSiteLogCount(int userId,Byte clientType,int companyId,int govId,Timestamp startTime,Timestamp endTime) throws Exception;
}
