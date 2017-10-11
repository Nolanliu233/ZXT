package cn.tongyuankeji.manager.basicData;

import java.util.List;

import cn.tongyuankeji.entity.basicData.DataType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface DataTypeService {

	/**
	 * 新增或修改类别记录
	 * @param dataType
	 */
	public void saveDataType(DataType dataType) throws Exception;
	
	/**
	 * 根据ID得到类别记录
	 * @param typeId
	 * @return DataType
	 * @throws Exception
	 */
	public DataType getDataTypeById(Integer typeId) throws Exception;
	
	/**
	 * 根据ID得到JSON格式类别记录
	 * @param typeId
	 * @return DataType
	 * @throws Exception
	 */
	public JSONObject getJSONDataTypeById(Integer typeId) throws Exception;
	
	/**
	 * 根据地区名称分页查询类别列表
	 * @param typeName
	 * @return List<DataType>
	 * @throws Exception
	 */
	public List<DataType> getDataTypeList() throws Exception;
	
	/**
	 * 根据类别名称分页查询JSON格式的类别列表
	 * @param typeName(类别名称，可为空，为空传null)
	 * @return JSONArray
	 * @throws Exception
	 */
	public JSONArray getJSONDataTypeList() throws Exception;
}
