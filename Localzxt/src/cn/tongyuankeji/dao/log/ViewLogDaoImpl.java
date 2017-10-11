package cn.tongyuankeji.dao.log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import cn.tongyuankeji.dao.BaseDAOImpl;
import cn.tongyuankeji.entity.log.ViewLog;

public class ViewLogDaoImpl extends BaseDAOImpl<ViewLog, Integer> implements ViewLogDao{

	protected ViewLogDaoImpl() {
		super(ViewLog.class, "sysId");
	}

	@Override
	public void saveViewLog(ViewLog viewLog)  throws Exception{
		super.getSession().saveOrUpdate(viewLog);
	}

	@Override
	public ViewLog getViewLogById(int viewLogId)  throws Exception{
		return super.getById(viewLogId, null, false);
	}

	@Override
	public List<ViewLog> getViewLogList(int nowPage, int pageSize, int userId, int informationId, Timestamp startTime,
			Timestamp endTime)  throws Exception{
		List<Criterion> criterions = new ArrayList<Criterion>();
		if(userId!=0){
			criterions.add(Restrictions.eq("userId", userId));
		}
		if(informationId!=0){
			criterions.add(Restrictions.eq("informationId", informationId));
		}
		if(startTime!=null){
			criterions.add(Restrictions.ge("viewAt", startTime));
		}
		if(endTime!=null){
			criterions.add(Restrictions.le("viewAt", endTime));
		}
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("viewAt"));
		return super.getEntityPage(criterions, orders, nowPage, pageSize);
	}

}
