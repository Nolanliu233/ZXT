package cn.tongyuankeji.dao.basicData;

import java.util.List;

import cn.tongyuankeji.entity.basicData.DataLevel;

public interface DataLevelDao {

	/**
	 * 新增或修改地区记录
	 * @param dataLevel
	 */
	public void saveOrUpdateDataLevel(DataLevel dataLevel) throws Exception;
	
	public DataLevel getDataLevelById(Integer levelId) throws Exception;
	
	public List<DataLevel> getDataLevelList(String levelName) throws Exception;
	
}
