package cn.tongyuankeji.manager.basicData;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tongyuankeji.dao.basicData.DataAreaDao;
import cn.tongyuankeji.entity.BaseEntity;
import cn.tongyuankeji.entity.basicData.DataArea;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Transactional(rollbackFor = Exception.class)
public class DataAreaServiceImpl implements DataAreaService{

	@Autowired
	private DataAreaDao areaDao;
	
	@Override
	public void saveDataArea(DataArea dataArea) throws Exception {
		areaDao.saveOrUpdateDataArea(dataArea);
	}

	@Override
	public DataArea getDataAreaById(Integer areaId) throws Exception {
		return areaDao.getDataAreaById(areaId);
	}
	
	@Override
	public JSONObject getJSONDataAreaById(Integer areaId) throws Exception {
		DataArea dataArea = getDataAreaById(areaId);
		return dataArea.toJson(true, false);
	}


	@Override
	public List<DataArea> getDataAreaList(String areaName) throws Exception {
		return areaDao.getDataAreaList(areaName);
	}

	@Override
	public JSONArray getJSONDataAreaList(String areaName) throws Exception {
		List<DataArea> list = getDataAreaList(areaName);
		return BaseEntity.list2JsonArray(list, true, false);
	}

}
