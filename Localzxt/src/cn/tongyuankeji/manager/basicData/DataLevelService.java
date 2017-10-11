package cn.tongyuankeji.manager.basicData;

import java.util.List;

import cn.tongyuankeji.entity.basicData.DataLevel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface DataLevelService {

	/**
	 * 新增或修改等级记录
	 * @param dataLevel
	 */
	public void saveDataLevel(DataLevel dataLevel) throws Exception;
	
	/**
	 * 根据ID得到等级记录
	 * @param levelId
	 * @return DataLevel
	 * @throws Exception
	 */
	public DataLevel getDataLevelById(Integer levelId) throws Exception;
	
	/**
	 * 根据ID得到JSON格式等级记录
	 * @param levelId
	 * @return JSONObject
	 * @throws Exception
	 */
	public JSONObject getJSONDataLevelById(Integer levelId) throws Exception;
	
	/**
	 * 根据等级名称分页查询等级列表
	 * @param levelName
	 * @return List<DataLevel>
	 * @throws Exception
	 */
	public List<DataLevel> getDataLevelList(String levelName) throws Exception;
	
	/**
	 * 根据等级名称分页查询JSON等级列表
	 * @param levelName
	 * @return JSONArray
	 * @throws Exception
	 */
	public JSONArray getJSONDataLevelList(String levelName) throws Exception;
}
