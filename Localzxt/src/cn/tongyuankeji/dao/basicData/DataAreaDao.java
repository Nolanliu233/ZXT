package cn.tongyuankeji.dao.basicData;

import java.util.List;

import cn.tongyuankeji.entity.basicData.DataArea;

public interface DataAreaDao {

	/**
	 * 新增或修改地区记录
	 * @param dataArea
	 */
	public void saveOrUpdateDataArea(DataArea dataArea) throws Exception;
	
	public DataArea getDataAreaById(Integer areaId) throws Exception;
	
	public List<DataArea> getDataAreaList(String areaName) throws Exception;
	
}
