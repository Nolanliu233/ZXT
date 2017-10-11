package cn.tongyuankeji.manager.baseurl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tongyuankeji.common.parameters.EnumBaseUrlState;
import cn.tongyuankeji.common.web.RunSettingImpl;
import cn.tongyuankeji.dao.baseurl.BaseUrlDao;
import cn.tongyuankeji.entity.BaseEntity;
import cn.tongyuankeji.entity.baseurl.BaseUrl;
import cn.tongyuankeji.manager.basicData.DataAreaService;
import cn.tongyuankeji.manager.basicData.DataLevelService;
import cn.tongyuankeji.manager.basicData.DataTypeService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Transactional(rollbackFor = Exception.class)
public class BaseUrlMangerImpl implements BaseUrlManager {
	
	@Autowired
	private BaseUrlDao baseUrlDao;
	
	@Autowired
	private DataAreaService dataArea;
	
	@Autowired
	private DataLevelService dataLevel;
	
	@Autowired
	private DataTypeService dataType;

	@Override
	public JSONObject selectAllBaseUrl(Integer pageStart, Integer pageSize,String siteName,String siteUrl) throws Exception {
		List<BaseUrl> lBaseUrls = null;
		JSONObject json = new JSONObject();
		JSONArray jsondata = new JSONArray();
		int count = baseUrlDao.getCount(siteName, siteUrl,EnumBaseUrlState.deleted);
		pageSize = pageSize == null ? RunSettingImpl.getPAGINATION_COUNT() : pageSize;
		int totalCount = 0;
		if (count % pageSize == 0) {
			totalCount = count / pageSize;
		} else {
			totalCount = count / pageSize + 1;
		}
		pageStart = pageStart * pageSize;
		lBaseUrls = baseUrlDao.selectAllBaseUrl(pageStart, pageSize, siteName, siteUrl,EnumBaseUrlState.deleted);
		
		jsondata = BaseEntity.list2JsonArray(lBaseUrls, true, false);
		String jsondataString = jsondata.toString();
		json.put("jsondataString", jsondataString);
		json.put("pageCount", totalCount);
		json.put("pageSize", pageSize);
		json.put("count", count);
		return json;
	}

	@Override
	public int getCount(String siteName,String siteUrl) throws Exception {
		return baseUrlDao.getCount(siteName,siteUrl,EnumBaseUrlState.deleted);
	}

	@Override
	public BaseUrl selectBaseUrl(int UrlID) throws Exception {
		return baseUrlDao.selectBaseUrl(UrlID);
	}

	@Override
	public void addBaseUrlData(BaseUrl baseUrl) throws Exception {
		baseUrlDao.saveOrUpdate(baseUrl);
	}

	@Override
	public void deleteBaseUrl(BaseUrl baseUrl) throws Exception {
		baseUrlDao.saveOrUpdate(baseUrl);
	}

	@Override
	public void updateBaseUrl(BaseUrl baseUrl) throws Exception {
		baseUrlDao.saveOrUpdate(baseUrl);
	}
	
	@Override
	public JSONObject getSelectBaseUrl() throws Exception {
		String areaData = dataArea.getJSONDataAreaList("").toString();
		String levelData = dataLevel.getJSONDataLevelList("").toString();
		String typeData = dataType.getJSONDataTypeList().toString();
		JSONObject json = new JSONObject();
		json.put("levelData", levelData);
		json.put("areaData", areaData);
		json.put("typeData", typeData);
		return json;
	}
}
