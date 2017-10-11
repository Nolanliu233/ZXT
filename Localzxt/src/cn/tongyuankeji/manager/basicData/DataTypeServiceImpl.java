package cn.tongyuankeji.manager.basicData;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.dao.basicData.DataTypeDao;
import cn.tongyuankeji.entity.BaseEntity;
import cn.tongyuankeji.entity.basicData.DataType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Transactional(rollbackFor = Exception.class)
public class DataTypeServiceImpl implements DataTypeService{

	@Autowired
	private DataTypeDao typeDao;
	
	@Override
	public void saveDataType(DataType dataType) throws Exception {
		typeDao.saveOrUpdateDataType(dataType);
	}

	@Override
	public DataType getDataTypeById(Integer typeId) throws Exception {
		return typeDao.getDataTypeById(Integer.valueOf(typeId));
	}
	
	@Override
	public JSONObject getJSONDataTypeById(Integer typeId) throws Exception {
		DataType dataType = getDataTypeById(typeId);
		return dataType.toJson(true, false);
	}

	@Override
	public List<DataType> getDataTypeList() throws Exception {
		return typeDao.getDataTypeList(EnumGenericState.deleted);
	}

	@Override
	public JSONArray getJSONDataTypeList() throws Exception {
		List<DataType> list = getDataTypeList();
		return BaseEntity.list2JsonArray(list, true, false);
	}

}
