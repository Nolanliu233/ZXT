package cn.tongyuankeji.manager.log;

import java.sql.Timestamp;
import java.util.List;

import cn.tongyuankeji.entity.log.ViewLog;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface ViewLogService {

	/**
	 * 新增访问日志
	 * @param viewLog
	 * @throws Exception
	 */
	public void saveViewLog(ViewLog viewLog) throws Exception;
	
	/**
	 * 根据ID得到访问日志记录
	 * @param viewLogId
	 * @return ViewLog
	 */
	public ViewLog getViewLogById(int viewLogId) throws Exception;

	/**
	 * 得到JSON格式的访问日志
	 * @param viewLogId
	 * @return JSONObject
	 * @throws Exception
	 */
	public JSONObject getJSONViewLogById(int viewLogId) throws Exception;
	
	/**
	 * 分页查询访问日志列表
	 * @param nowPage(当前页)
	 * @param pageSize(每页显示数量)
	 * @param userId(用户ID)
	 * @param informationId(信息ID)
	 * @param startTime(查询开始时间)
	 * @param endTime(查询结束时间)
	 * @return List<ViewLog>
	 */
	public List<ViewLog> getViewLogList(int nowPage,int pageSize,int userId,int informationId,Timestamp startTime,Timestamp endTime) throws Exception;

	/**
	 * 得到JSON格式的访问日志列表
	 * @param nowPage(当前页)
	 * @param pageSize(每页显示数量)
	 * @param userId(用户ID)
	 * @param informationId(信息ID)
	 * @param startTime(查询开始时间)
	 * @param endTime(查询结束时间)
	 * @return JSONArray
	 * @throws Exception
	 */
	public JSONArray getJSONViewLogList(int nowPage,int pageSize,int userId,int informationId,Timestamp startTime,Timestamp endTime) throws Exception;
}
