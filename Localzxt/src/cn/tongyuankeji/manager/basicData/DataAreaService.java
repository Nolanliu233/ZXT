package cn.tongyuankeji.manager.basicData;

import java.util.List;

import cn.tongyuankeji.entity.basicData.DataArea;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface DataAreaService {

	/**
	 * 新增或修改地区记录
	 * @param dataArea
	 */
	public void saveDataArea(DataArea dataArea) throws Exception;
	
	/**
	 * 根据ID得到地区记录
	 * @param areaId
	 * @return DataArea
	 * @throws Exception
	 */
	public DataArea getDataAreaById(Integer areaId) throws Exception;
	
	/**
	 * 根据ID得到JSON格式的地区记录
	 * @param areaId
	 * @return JSONObject
	 * @throws Exception
	 */
	public JSONObject getJSONDataAreaById(Integer areaId) throws Exception;
	
	/**
	 * 根据地区名称分页查询地区列表
	 * @param areaName
	 * @return List<DataArea>
	 * @throws Exception
	 */
	public List<DataArea> getDataAreaList(String areaName) throws Exception;
	
	/**
	 * 根据地区名称分页查询JSON格式的地区列表
	 * @param areaName
	 * @return JSONArray
	 * @throws Exception
	 */
	public JSONArray getJSONDataAreaList(String areaName) throws Exception;
}
