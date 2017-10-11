package cn.tongyuankeji.dao.basicData;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.dao.BaseDAOImpl;
import cn.tongyuankeji.entity.basicData.DataLevel;

@Repository
public class DataLevelDaoImpl extends BaseDAOImpl<DataLevel, Integer> implements DataLevelDao{

	protected  DataLevelDaoImpl(){
		super(DataLevel.class,"levelId");
	}
	@Override
	public void saveOrUpdateDataLevel(DataLevel dataLevel) throws Exception{
		super.getSession().saveOrUpdate(dataLevel);
		
	}

	@Override
	public DataLevel getDataLevelById(Integer levelId) throws Exception{
		return super.getById(levelId, null, false);
	}

	@Override
	public List<DataLevel> getDataLevelList(String levelName) throws Exception{
		List<Criterion> criterion = new ArrayList<Criterion>();
		if(!Utils.isBlank(levelName)){
			criterion.add(Restrictions.like("levelName", levelName));
		}
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.asc("levelId"));
		return super.getEntityList(criterion, orders, false);
	}

}
