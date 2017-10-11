package cn.tongyuankeji.manager.news;

import java.sql.Timestamp;
import java.util.List;

import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.entity.news.News;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface NewsService {
	/**
	 * 新增新闻信息
	 * @param title 标题
	 * @param content 内容
	 * @param type 类别
	 * @param orderBy 排序号
	 * @throws Exception
	 */
	public void saveNews(String title,String content,Byte type,Byte orderBy) throws Exception;
	
	/**
	 * 修改新闻信息
	 * @param newsId 
	 * @param title 标题
	 * @param content 内容
	 * @param type 类别
	 * @param orderBy 排序号
	 * @throws Exception
	 */
	public void updateNews(Integer newsId,String title,String content,Byte type,Byte orderBy) throws Exception;
	
	/**
	 * 根据ID得到新闻纪录
	 * @param newsId
	 * @param state 状态
	 * @param isLock 是否加锁
	 * @param isFront 是否是前台查询
	 * @return News
	 */
	public News getNewsById(int newsId,EnumGenericState state,Boolean isLock,Boolean isFront) throws Exception;
	
	/**
	 * 得到JSON格式的新闻纪录
	 * @param newsId
	 * @param isFront 是否是前台查询
	 * @return JSONObject
	 * @throws Exception
	 */
	public JSONObject getJSONNewsById(int newsId,EnumGenericState state,Boolean isLock,Boolean isFront) throws Exception;
	
	/**
	 * 分页查询新闻列表
	 * @param nowPage
	 * @param pageSize
	 * @param title(标题)
	 * @param state 状态
	 * @param type(类别)
	 * @param orderBy(排序号)
	 * @param startTime(查询开始时间)
	 * @param endTime(查询结束时间)
	 * @return List<News>
	 */
	public List<News> getNewsList(Integer nowPage,Integer pageSize,String title,Byte state,Byte type,Byte orderBy,Boolean orderByGe,Timestamp startTime,Timestamp endTime) throws Exception;
	
	/**
	 * 得到JSON格式的新闻列表
	 * @param nowPage
	 * @param pageSize
	 * @param title
	 * @param state
	 * @param type
	 * @param orderBy
	 * @param startTime(查询开始时间)
	 * @param endTime(查询结束时间)
	 * @return JSONArray
	 * @throws Exception
	 */
	public JSONArray getJSONNewsList(Integer nowPage,Integer pageSize,String title,Byte state,Byte type,Byte orderBy,Boolean orderByGe,Timestamp startTime,Timestamp endTime) throws Exception;
	
	/**
	 * 得到新闻总记录数
	 * @param title
	 * @param type
	 * @param orderBy
	 * @param startTime(查询开始时间)
	 * @param endTime(查询结束时间)
	 * @return count
	 * @throws Exception
	 */
	public int getNewsCount(String title,Byte state,Byte type,Byte orderBy,Boolean orderByGe,Timestamp startTime,Timestamp endTime) throws Exception;
	
	/**
	 * 删除新闻
	 * @param newsId
	 * @return true/false
	 */
	public boolean delNews(int  newsId) throws Exception;
}
