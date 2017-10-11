package cn.tongyuankeji.dao.news;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.common.parameters.EnumNewsOrderBy;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.dao.BaseDAOImpl;
import cn.tongyuankeji.entity.news.News;

@Repository
public class NewsDaoImpl extends BaseDAOImpl<News, Integer> implements NewsDao{

	protected NewsDaoImpl() {
		super(News.class, "newsId");
	}

	@Override
	public void saveOrUpdateNews(News news) throws Exception {
		super.getSession().saveOrUpdate(news);
	}

	@Override
	public News getNewsById(int newsId,EnumGenericState stateMin,Boolean isLock) throws Exception {
		return super.getById(newsId, stateMin.byteObject(), isLock==null?false:isLock);
	}

	@Override
	public List<News> getNewsList(Integer nowPage, Integer pageSize, String title,
			Byte state, Byte type,Byte orderBy,Boolean orderByGe,
			Timestamp startTime,Timestamp endTime) throws Exception {
		List<Criterion> criterions = new ArrayList<Criterion>();
		if(!Utils.isBlank(title)){
			criterions.add(Restrictions.like("title", "%"+title+"%"));
		}
		if(state!=null){
			criterions.add(Restrictions.ge("state", state));
		}
		if(type!=null){
			criterions.add(Restrictions.eq("type", type));
		}
		if(orderByGe){
			criterions.add(Restrictions.le("orderBy", orderBy));
		}else{
			if(orderBy!=null)
			criterions.add(Restrictions.eq("orderBy", orderBy));
		}
		if(startTime!=null){
			criterions.add(Restrictions.ge("docCreateTime", startTime));
		}
		if(endTime!=null){
			criterions.add(Restrictions.le("docCreateTime", endTime));
		}
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("docCreateTime"));
		orders.add(Order.asc("orderBy"));
		return super.getEntityPage(criterions, orders, nowPage, pageSize);
	}

	@Override
	public int getNewsCount(String title,Byte state,Byte type, 
			Byte orderBy,Boolean orderByGe,
			Timestamp startTime,Timestamp endTime) throws Exception {
		List<Criterion> criterions = new ArrayList<Criterion>();
		if(!Utils.isBlank(title)){
			criterions.add(Restrictions.like("title", "%"+title+"%"));
		}
		if(state!=null){
			criterions.add(Restrictions.ge("state", state));
		}
		if(type!=null){
			criterions.add(Restrictions.eq("type", type));
		}
		if(orderByGe){
			criterions.add(Restrictions.le("orderBy", orderBy));
		}else{
			if(orderBy!=null)
			criterions.add(Restrictions.eq("orderBy", orderBy));
		}
		if(startTime!=null){
			criterions.add(Restrictions.ge("docCreateTime", startTime));
		}
		if(endTime!=null){
			criterions.add(Restrictions.le("docCreateTime", endTime));
		}
		return super.getCount(criterions);
	}
	
}
