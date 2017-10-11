package cn.tongyuankeji.dao.news;

import java.sql.Timestamp;
import java.util.List;

import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.entity.news.News;

public interface NewsDao {

	/**
	 * 新增或修改新闻信息
	 * @param news
	 */
	public void saveOrUpdateNews(News news) throws Exception;
	
	/**
	 * 根据ID得到新闻纪录
	 * @param newsId
	 * @param stateMin 状态
	 * @param islock 是否加锁
	 * @return News
	 */
	public News getNewsById(int newsId,EnumGenericState stateMin,Boolean islock) throws Exception;
	
	/**
	 * 分页查询新闻列表
	 * @param nowPage
	 * @param pageSize
	 * @param title(标题)
	 * @param state 状态
	 * @param type(类别)
	 * @param orderBy(排序号)
	 * @param orderByGe(排序号是否用大于等于)
	 * @param startTime(查询开始时间)
	 * @param endTime(查询结束时间)
	 * @return List<News>
	 */
	public List<News> getNewsList(Integer nowPage,Integer pageSize,
			String title,Byte state, Byte type,Byte orderBy,
			Boolean orderByGe,
			Timestamp startTime,Timestamp endTime) throws Exception;
	
	/**
	 * 得到新闻总记录数
	 * @param title
	 * @param state
	 * @param type
	 * @param orderBy
	 * @param orderByGe(排序号是否用大于等于)
	 * @param stateGe(状态是否用大于等于)
	 * @param startTime(查询开始时间)
	 * @param endTime(查询结束时间)
	 * @return count
	 * @throws Exception
	 */
	public int getNewsCount(String title,Byte state, Byte type,
			Byte orderBy,Boolean orderByGe,
			Timestamp startTime,Timestamp endTime) throws Exception;
	
}
