package cn.tongyuankeji.manager.news;

import java.sql.Timestamp;
import java.util.List;

import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.common.parameters.EnumNewsOrderBy;
import cn.tongyuankeji.common.parameters.EnumNewsType;
import cn.tongyuankeji.common.util.DateUtils;
import cn.tongyuankeji.common.util.FileUtils;
import cn.tongyuankeji.common.web.RunSettingImpl;
import cn.tongyuankeji.dao.news.NewsDao;
import cn.tongyuankeji.entity.BaseEntity;
import cn.tongyuankeji.entity.news.News;
import cn.tongyuankeji.entity.news.NewsType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Transactional(rollbackFor = Exception.class)
public class NewsServiceImpl implements NewsService{

	@Autowired
	private NewsDao newsDao;
	
	@Override
	public void saveNews(String title,String content,Byte type,Byte orderBy) throws Exception {
		try {
			News news = new News();
			news.setContentSummary("");
			news.setOrderBy(orderBy);
			news.setTitle(title);
			news.setType(type);
			//设置发布时间
			news.setDocCreateTime(DateUtils.now());
			//设置新闻状态
			news.setState(EnumGenericState.active.byteObject());
			//设置浏览次数
			news.setBrowerCount(0);
			news.setContentUrl(title+".html");
			//设置排序号(默认为2/不在前台显示，如果前台选择显示，设置为1，如需要长期置顶，设置为0)
			newsDao.saveOrUpdateNews(news);
			FileUtils.write(
					ConstantBase.getUPLOAD_PATH() + RunSettingImpl.getUPLOAD_NEWS_FILE()+news.getTitle()+".html", 
					content);
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}

	@Override
	public void updateNews(Integer newsId,String title,String content,Byte type,Byte orderBy) throws Exception {
		News news = getNewsById(newsId,EnumGenericState.active,true,false);
		//删除以前的文件
		boolean delFileSuccess = FileUtils.deleteFile(ConstantBase.getUPLOAD_PATH() + RunSettingImpl.getUPLOAD_NEWS_FILE()+news.getContentUrl());
		System.out.println(delFileSuccess);
		news.setContentSummary(null);
		news.setOrderBy(orderBy);
		news.setTitle(title);
		news.setType(type);
		news.setContentSummary("");
		news.setContentUrl(news.getTitle()+".html");
		//添加修改时间
		news.setDocAlterTime(DateUtils.now());
		newsDao.saveOrUpdateNews(news);
		//TODO 如果标题名称更改，文件名也应该更改
		//保存正文html
		FileUtils.write(
				ConstantBase.getUPLOAD_PATH() + RunSettingImpl.getUPLOAD_NEWS_FILE()+news.getContentUrl(), 
				content);
	}

	@Override
	public News getNewsById(int newsId,EnumGenericState state,Boolean isLock,Boolean isFront) throws Exception {
		
		News news = newsDao.getNewsById(newsId,state,isLock);
		//改变浏览次数
		if(isFront)
			news.setBrowerCount(news.getBrowerCount()+1);
		return news;
	}
	

	@Override
	public JSONObject getJSONNewsById(int newsId,EnumGenericState state,Boolean isLock,Boolean isFront) throws Exception {
		News news = getNewsById(newsId,state,isLock,isFront);
		JSONObject jsonObject = news.toJson(true, true);
		//拼接正文url
		jsonObject.put("contentUrl", 
				"../"+
				RunSettingImpl.getUPLOAD_NEWS_FILE()+
				jsonObject.get("contentUrl"));
		return jsonObject;
	}

	@Override
	public List<News> getNewsList(Integer nowPage, Integer pageSize, String title,Byte state, Byte type, Byte orderBy,Boolean orderByGe,Timestamp startTime,Timestamp endTime) throws Exception {
		return newsDao.getNewsList(nowPage*pageSize, pageSize, title, state, type, orderBy,orderByGe,startTime,endTime);
	}

	@Override
	public JSONArray getJSONNewsList(Integer nowPage, Integer pageSize, String title, Byte state, Byte type, Byte orderBy,Boolean orderByGe,Timestamp startTime,Timestamp endTime) throws Exception {
		JSONArray jsonArray = new JSONArray();
		List<News> list = getNewsList(nowPage, pageSize, title, state, type, orderBy,orderByGe,startTime,endTime);
		jsonArray = BaseEntity.list2JsonArray(list, true, true);
		return jsonArray;
	}

	
	@Override
	public int getNewsCount(String title, Byte state, Byte type, Byte orderBy,Boolean orderByGe,Timestamp startTime,Timestamp endTime) throws Exception {
		return newsDao.getNewsCount(title, state, type, orderBy,orderByGe,startTime,endTime);
	}

	@Override
	public boolean delNews(int newsId) throws Exception {
		News news = getNewsById(newsId,EnumGenericState.active,true,false);
		news.setState(EnumGenericState.deleted.byteObject());
		news.setDocAlterTime(DateUtils.now());
		newsDao.saveOrUpdateNews(news);
		return true;
	}
}
