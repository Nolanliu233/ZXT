package cn.tongyuankeji.dao.log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import cn.tongyuankeji.dao.BaseDAOImpl;
import cn.tongyuankeji.entity.log.SiteLog;

@Repository
public class SiteLogDaoImpl extends BaseDAOImpl<SiteLog, Integer> implements SiteLogDao{

	protected SiteLogDaoImpl() {
		super(SiteLog.class, "sysId");
	}

	@Override
	public void saveOrUpdateSiteLog(SiteLog siteLog) {
		super.getSession().saveOrUpdate(siteLog);
	}

	@Override
	public SiteLog getSiteLogById(int siteLogId) throws Exception {
		return super.getById(siteLogId, null, false);
	}

	@Override
	public List<SiteLog> getSiteLogList(int nowPage, int pageSize, int userId, Byte clientType, int companyId,
			int govId, Timestamp startTime, Timestamp endTime)  throws Exception{
		List<Criterion> criterions = new ArrayList<Criterion>();
		if(userId!=0){
			criterions.add(Restrictions.eq("userId", userId));
		}
		if(clientType!=null){
			criterions.add(Restrictions.eq("useClientType", clientType));
		}
		if(companyId!=0){
			criterions.add(Restrictions.eq("companyId", companyId));
		}
		if(govId!=0){
			criterions.add(Restrictions.eq("ownerGovId", govId));
		}
		if(startTime!=null){
			criterions.add(Restrictions.ge("createdAt", startTime));
		}
		if(endTime!=null){
			criterions.add(Restrictions.le("createdAt", endTime));
		}
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("createdAt"));
		return super.getEntityPage(criterions, orders, nowPage, pageSize);
	}

	@Override
	public int getSiteLogCount(int userId, Byte clientType, int companyId, int govId, Timestamp startTime,
			Timestamp endTime) throws Exception {
		List<Criterion> criterions = new ArrayList<Criterion>();
		if(userId!=0){
			criterions.add(Restrictions.eq("userId", userId));
		}
		if(clientType!=null){
			criterions.add(Restrictions.eq("useClientType", clientType));
		}
		if(companyId!=0){
			criterions.add(Restrictions.eq("companyId", companyId));
		}
		if(govId!=0){
			criterions.add(Restrictions.eq("ownerGovId", govId));
		}
		if(startTime!=null){
			criterions.add(Restrictions.ge("createdAt", startTime));
		}
		if(endTime!=null){
			criterions.add(Restrictions.le("createdAt", endTime));
		}
		return super.getCount(criterions);
	}

}
