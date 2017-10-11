package cn.tongyuankeji.manager.basicData;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tongyuankeji.dao.basicData.DataLevelDao;
import cn.tongyuankeji.entity.BaseEntity;
import cn.tongyuankeji.entity.basicData.DataLevel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Transactional(rollbackFor = Exception.class)
public class DataLevelServiceImpl implements DataLevelService{

	@Autowired
	private DataLevelDao levelDao;
	
	@Override
	public void saveDataLevel(DataLevel dataLevel) throws Exception {
		levelDao.saveOrUpdateDataLevel(dataLevel);
	}

	@Override
	public DataLevel getDataLevelById(Integer levelId) throws Exception {
		return levelDao.getDataLevelById(Integer.valueOf(levelId));
	}

	@Override
	public JSONObject getJSONDataLevelById(Integer levelId) throws Exception {
		DataLevel dataLevel = getDataLevelById(levelId);
		return dataLevel.toJson(true, false);
	}
	
	@Override
	public List<DataLevel> getDataLevelList(String levelName) throws Exception {
		return levelDao.getDataLevelList(levelName);
	}

	@Override
	public JSONArray getJSONDataLevelList(String levelName) throws Exception {
		List<DataLevel> list = getDataLevelList(levelName);
		return BaseEntity.list2JsonArray(list, true, false);
	}

}
