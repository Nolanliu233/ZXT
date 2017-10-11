package cn.tongyuankeji.dao.basicData;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.dao.BaseDAOImpl;
import cn.tongyuankeji.entity.basicData.DataType;

@Repository
public class DataTypeDaoImpl extends BaseDAOImpl<DataType, Integer> implements DataTypeDao{

	protected  DataTypeDaoImpl(){
		super(DataType.class,"typeId");
	}
	@Override
	public void saveOrUpdateDataType(DataType dataType) throws Exception{
		super.getSession().saveOrUpdate(dataType);
		
	}

	@Override
	public DataType getDataTypeById(Integer typeId) throws Exception{
		return super.getById(typeId, null, false);
	}

	@Override
	public List<DataType> getDataTypeList(EnumGenericState MinStatus) throws Exception{
		List<Criterion> criterion = new ArrayList<Criterion>();
		criterion.add(Restrictions.ge("state", MinStatus.byteValue()));
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.asc("typeId"));
		return super.getEntityList(criterion, orders, false);
	}

}
