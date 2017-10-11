package cn.tongyuankeji.dao.information;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import cn.tongyuankeji.common.parameters.EnumContentPageState;
import cn.tongyuankeji.common.util.DateUtils;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.dao.BaseDAOImpl;
import cn.tongyuankeji.entity.information.InformationSources;
import net.sf.json.JSONObject;

@Repository
public class InformationSourcesDaoImpl extends BaseDAOImpl<InformationSources, Integer> implements InformationSourcesDao{

	protected InformationSourcesDaoImpl() {
		super(InformationSources.class, "sysId");
	}

	@Override
	public void saveOrUpdateInfoSource(InformationSources info) {
		super.getSession().saveOrUpdate(info);
	}

	@Override
	public InformationSources getInfoSourceById(int infoSourceId, EnumContentPageState minState, Boolean isblock) throws Exception{
		//TODO 类型添加
		InformationSources infoSources = super.getById(infoSourceId, minState.byteValue(), isblock == null ? false : isblock);
		return infoSources;
	}

	@Override
	public JSONObject getInfoZhById(int infoSourceId) throws Exception{
		//查询数据中返回的字段
	    String[] resultFeild = new String[]{"sysId","title","coverUrl","path","fromWebName", "baseUrl",
	    		"cawlerTime","detailSource","releaseAt","beginAt","endAt","dynamicScore", "staticScore","browseCount", 
	    		"status", "typeName","levelName","areaName"
	    		};

		JSONObject result = new JSONObject();
		//TODO 类型添加
		Object[] list = (Object[]) super.createSQLQuery(String.format(
				"select i.sys_id, i.title, i.cover_url, i.path, i.information_from_title, i.url,"
				+ "i.cawler_time, detail_source, i.release_at, i.begin_at, i.end_at, i.dynamic_score, i.static_score, "
				+ "i.browse_count, i.state, t.TypeName, l.LevelName, a.AreaName "
				+ "from information_sources AS i "
				+ "left join datatype AS t on i.type = t.TypeID "
				+ "left join dataarea AS a on i.areaid = a.AreaID "
				+ "left join datalevel AS l on i.level = l.LevelID "
				+"where i.sys_id = %d", 
				infoSourceId
				)).getSingleResult();
		if(list == null){
			return null;
		}
		
		for(int i = 0; i < resultFeild.length; i++){
			if(list[i] instanceof Timestamp)
				result.put(resultFeild[i], DateUtils.timestamp2String((Timestamp)list[i]));
   			else if(list[i] instanceof Date)
   				result.put(resultFeild[i], DateUtils.date2String((Date)list[i]));
   			else 
   				result.put(resultFeild[i], list[i]);
		}
		return result;
	}
	
	@Override
	public List<InformationSources> getInfoSourceList(int nowPage, int pageSize,String title,Byte state,Timestamp startTime,Timestamp endTime,String orderBy) throws Exception{
		List<Criterion> criterions = new ArrayList<Criterion>();
		if(!Utils.isBlank(title)){
			criterions.add(Restrictions.like("title", title));
		}
		if(state!=null){
			criterions.add(Restrictions.eq("state", state));
		}
		if(startTime!=null){
			criterions.add(Restrictions.ge("cawlerTime", startTime));
		}
		if(endTime!=null){
			criterions.add(Restrictions.le("cawlerTime", endTime));
		}
		List<Order> orderList = new ArrayList<Order>();
		if(!Utils.isBlank(orderBy)){
			orderList.add(Order.desc(orderBy));
		}else{
			orderList.add(Order.desc("time"));
		}
		return super.getEntityPage(criterions, orderList, nowPage, pageSize);
	}

	@Override
	public int getInfoSourceCount(String title,Byte state,Timestamp startTime,Timestamp endTime) {
		try {
			List<Criterion> criterions = new ArrayList<Criterion>();
			if(!Utils.isBlank(title)){
				criterions.add(Restrictions.like("title", title));
			}
			if(state!=null){
				criterions.add(Restrictions.eq("state", state));
			}
			if(startTime!=null){
				criterions.add(Restrictions.ge("cawlerTime", startTime));
			}
			if(endTime!=null){
				criterions.add(Restrictions.le("cawlerTime", endTime));
			}
			return super.getCount(criterions);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
