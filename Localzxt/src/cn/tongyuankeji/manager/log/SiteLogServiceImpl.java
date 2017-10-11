package cn.tongyuankeji.manager.log;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tongyuankeji.common.util.DateUtils;
import cn.tongyuankeji.dao.log.SiteLogDao;
import cn.tongyuankeji.entity.BaseEntity;
import cn.tongyuankeji.entity.log.SiteLog;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author 刘磊
 * @date 2017年8月23日
 * @Description
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SiteLogServiceImpl implements SiteLogService{

	@Autowired
	private SiteLogDao siteLogDao;
	
	@Override
	public void saveSiteLog(SiteLog siteLog) {
		//设置时间
		siteLog.setCreatedAt(DateUtils.now());
		siteLogDao.saveOrUpdateSiteLog(siteLog);
	}

	@Override
	public SiteLog getSiteLogById(int siteLogId) throws Exception {
		return siteLogDao.getSiteLogById(siteLogId);
	}

	@Override
	public JSONObject getJSONSiteLogById(int siteLogId) throws Exception {
		SiteLog siteLog = getSiteLogById(siteLogId);
		return siteLog.toJson(true, true);
	}

	@Override
	public List<SiteLog> getSiteLogList(int nowPage, int pageSize, int userId, Byte clientType, int companyId,
			int govId, Timestamp startTime, Timestamp endTime) throws Exception {
		return siteLogDao.getSiteLogList(nowPage, pageSize, userId, clientType, companyId, govId, startTime, endTime);
	}

	@Override
	public JSONArray getJSONSiteLogList(int nowPage, int pageSize, int userId, Byte clientType, int companyId,
			int govId, Timestamp startTime, Timestamp endTime) throws Exception {
		List<SiteLog> list = getSiteLogList(nowPage, pageSize, userId, clientType, companyId, govId, startTime, endTime);
		JSONArray jsonArray = BaseEntity.list2JsonArray(list, true, true);
		return jsonArray;
	}

	@Override
	public int getSiteLogCount(int userId, Byte clientType, int companyId, int govId, Timestamp startTime,
			Timestamp endTime) throws Exception {
		return siteLogDao.getSiteLogCount(userId, clientType, companyId, govId, startTime, endTime);
	}

}
