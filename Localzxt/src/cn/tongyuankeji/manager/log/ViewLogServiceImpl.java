package cn.tongyuankeji.manager.log;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tongyuankeji.common.util.DateUtils;
import cn.tongyuankeji.dao.log.ViewLogDao;
import cn.tongyuankeji.entity.BaseEntity;
import cn.tongyuankeji.entity.log.ViewLog;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Transactional(rollbackFor = Exception.class)
public class ViewLogServiceImpl implements ViewLogService{

	@Autowired
	private ViewLogDao viewLogDao;
	@Override
	public void saveViewLog(ViewLog viewLog) throws Exception {
		viewLog.setViewAt(DateUtils.now());
	}

	@Override
	public ViewLog getViewLogById(int viewLogId) throws Exception {
		return viewLogDao.getViewLogById(viewLogId);
	}

	@Override
	public JSONObject getJSONViewLogById(int viewLogId) throws Exception {
		ViewLog viewLog = getViewLogById(viewLogId);
		return viewLog.toJson(true, false);
	}

	@Override
	public List<ViewLog> getViewLogList(int nowPage, int pageSize, int userId, int informationId, Timestamp startTime,
			Timestamp endTime) throws Exception {
		return viewLogDao.getViewLogList(nowPage, pageSize, userId, informationId, startTime, endTime);
	}

	@Override
	public JSONArray getJSONViewLogList(int nowPage, int pageSize, int userId, int informationId, Timestamp startTime,
			Timestamp endTime) throws Exception {
		List<ViewLog> list = getViewLogList(nowPage, pageSize, userId, informationId, startTime, endTime);
		return BaseEntity.list2JsonArray(list, true, false);
	}

}
