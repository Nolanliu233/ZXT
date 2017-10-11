package cn.tongyuankeji.dao.basicData;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.dao.BaseDAOImpl;
import cn.tongyuankeji.entity.basicData.DataArea;

@Repository
public class DataAreaDaoImpl extends BaseDAOImpl<DataArea, Integer> implements DataAreaDao{

	protected  DataAreaDaoImpl(){
		super(DataArea.class,"areaId");
	}
	@Override
	public void saveOrUpdateDataArea(DataArea dataArea) throws Exception{
		super.getSession().saveOrUpdate(dataArea);
		
	}

	@Override
	public DataArea getDataAreaById(Integer areaId) throws Exception{
		return super.getById(areaId, null, false);
	}

	@Override
	public List<DataArea> getDataAreaList(String areaName) throws Exception{
		List<Criterion> criterion = new ArrayList<Criterion>();
		if(!Utils.isBlank(areaName)){
			criterion.add(Restrictions.like("areaName", areaName));
		}
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.asc("areaId"));
		return super.getEntityList(criterion, orders, false);
	}

}
