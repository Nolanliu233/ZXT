package cn.tongyuankeji.dao.basicData;

import java.util.List;

import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.entity.basicData.DataType;

public interface DataTypeDao {

	/**
	 * 新增或修改地区记录
	 * @param dataType
	 */
	public void saveOrUpdateDataType(DataType dataType) throws Exception;
	
	public DataType getDataTypeById(Integer typeId) throws Exception;
	
	public List<DataType> getDataTypeList(EnumGenericState MinStatus) throws Exception;
	
}
