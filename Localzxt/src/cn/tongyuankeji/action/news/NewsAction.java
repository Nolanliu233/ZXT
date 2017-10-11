package cn.tongyuankeji.action.news;

import java.sql.Timestamp;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.tongyuankeji.common.annotation.ACLMapping;
import cn.tongyuankeji.common.annotation.ActionArg;
import cn.tongyuankeji.common.annotation.ActionArgs;
import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.common.parameters.EnumNewsOrderBy;
import cn.tongyuankeji.common.parameters.EnumNewsType;
import cn.tongyuankeji.common.util.EnumConverterImpl;
import cn.tongyuankeji.common.util.ResponseUtils;
import cn.tongyuankeji.common.web.SchemaBase;
import cn.tongyuankeji.manager.news.NewsService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author 刘磊
 * @date 2017年8月22日
 * @Description
 */
@Controller
public class NewsAction {

	@Autowired
	private NewsService newsService;
	
	@RequestMapping("/saveNews.ofc")
	@ACLMapping(subsystem = "backoffice", module = "informationManager", page = "newsManager", opr = "save")
	@ActionArgs({
		@ActionArg(name="title",header="新闻标题"),
		@ActionArg(name="type",header="新闻类别",target_type=Byte.class),
		@ActionArg(name="orderBy",header="显示方式",target_type=Byte.class),
		@ActionArg(name="content",header="新闻内容")
	})
	public String saveNews(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			map = SchemaBase.validate(request, this, EnumConverterImpl.getInstance());
			newsService.saveNews((String)map.get("title"),
					(String)map.get("content"),(Byte)map.get("type"),
					(Byte)map.get("orderBy"));
			ResponseUtils.renderSuccessJson(response, "添加成功", null);
		} catch (Exception e) {
			ResponseUtils.renderErrorJson(response, "添加失败，请重试", null);
		}
		return null;
	}
	
	@RequestMapping("/newsList.ofc")
	@ACLMapping(subsystem = "backoffice", module = "informationManager", page = "newsManager", opr = "view")
	@ActionArgs({
		@ActionArg(name="nowPage",header="当前页",target_type=Integer.class),
		@ActionArg(name="pageSize",header="每页显示条数",target_type=Integer.class),
		@ActionArg(name="title",header="标题",required=false),
		@ActionArg(name="orderBy",header="排序方式" ,required=false,target_type=Byte.class),
		@ActionArg(name="type",header="类别" ,required=false,target_type=Byte.class),
		@ActionArg(name="startTime",header="开始时间" ,required=false,target_type=Timestamp.class),
		@ActionArg(name="endTime",header="结束时间" ,required=false,target_type=Timestamp.class)
	})
	public String getNewsList(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		JSONObject jsonObject = new JSONObject();
		try {
			map = SchemaBase.validate(request, this, EnumConverterImpl.getInstance());
			int nowPage = (Integer)map.get("nowPage");
			if(nowPage<0){
				nowPage = 0 ;
			}
			int pageSize = (Integer)map.get("pageSize");
			//orderBy==null表示查询所有新闻
			JSONArray jsonArray = newsService.getJSONNewsList(
					nowPage, (Integer)map.get("pageSize"),
					(String)map.get("title"),
					EnumGenericState.hidden.byteObject(),
					(Byte)map.get("type"),
					(Byte)map.get("orderBy"),
					(Byte)map.get("orderBy")==null?false:true,
					(Timestamp)map.get("startTime"),
					(Timestamp)map.get("endTime"));
			int count = newsService.getNewsCount(
					(String)map.get("title"),
					EnumGenericState.hidden.byteObject(),
					(Byte)map.get("type"),
					(Byte)map.get("orderBy"),
					(Byte)map.get("orderBy")==null?false:true,
					(Timestamp)map.get("startTime"),
					(Timestamp)map.get("endTime"));
			jsonObject.put("totalCount", count);
			if(pageSize>0){
				count = count%pageSize==0?count/pageSize:count/pageSize+1;
			}
			jsonObject.put("newsList",jsonArray);
			jsonObject.put("pageCount",count);
			
			ResponseUtils.renderJson(response, ConstantBase.CONTENT_TYPE, jsonObject);
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
		return null;
	}
	
	@RequestMapping("/newsForm.ofc")
	public String newsForm(HttpServletRequest request,HttpServletResponse response){
		try {
			JSONArray newsTypes = EnumNewsType.dataArray;
			JSONArray newsOrderBys = EnumNewsOrderBy.dataArray;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("newsTypes", newsTypes);
			jsonObject.put("newsOrderBys", newsOrderBys);
			ResponseUtils.renderJson(response, ConstantBase.CONTENT_TYPE, jsonObject);
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
		return null;
	}
	
	@RequestMapping("/editNews.ofc")
	@ActionArgs({
		@ActionArg(name="newsId",header="ID",target_type = Integer.class)
	})
	public String editNews(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		JSONObject jsonObject = new JSONObject();
		try {
			map = SchemaBase.parseArg(request, this, EnumConverterImpl.getInstance());
			//得到新闻类别枚举JSON
			JSONArray newsTypes = EnumNewsType.dataArray;
			JSONArray newsOrderBys = EnumNewsOrderBy.dataArray;
			JSONObject newsInfo = newsService.getJSONNewsById((Integer)map.get("newsId"), EnumGenericState.active, true,false);
			jsonObject.put("newTypes", newsTypes);
			jsonObject.put("newsOrderBys", newsOrderBys);
			jsonObject.put("newsInfo", newsInfo);
			ResponseUtils.renderJson(response, "成功",jsonObject);
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
		return null;
	}
	
	@RequestMapping("/updateNews.ofc")
	@ACLMapping(subsystem = "backoffice", module = "informationManager", page = "newsManager", opr = "edit")
	@ActionArgs({
		@ActionArg(name="newsId",header="ID",target_type = Integer.class),
		@ActionArg(name="title",header="标题"),
		@ActionArg(name="content",header="正文"),
		@ActionArg(name="type",header="新闻类别",target_type = Byte.class),
		@ActionArg(name="orderBy",header="排序号",target_type=Byte.class)
	})
	public String updateNews(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			map = SchemaBase.validate(request, this, EnumConverterImpl.getInstance());
			newsService.updateNews((Integer)map.get("newsId"),(String)map.get("title"),(String)map.get("content"),(Byte)map.get("type"),(Byte)map.get("orderBy"));
			ResponseUtils.renderSuccessJson(response, "修改成功");
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
		return null;
	}
	@RequestMapping("/deleteNews.ofc")
	@ACLMapping(subsystem = "backoffice", module = "informationManager", page = "newsManager", opr = "delete")
	@ActionArgs({
		@ActionArg(name="newsId",header="ID",target_type = Integer.class),
	})
	public String deleteNews(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			map = SchemaBase.validate(request, this, EnumConverterImpl.getInstance());
			newsService.delNews((Integer)map.get("newsId"));
			ResponseUtils.renderSuccessJson(response, "删除成功");
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
		return null;
	}
	
	@RequestMapping("/newsDetail.htm")
	@ActionArgs({
		@ActionArg(name="newsId",header="ID",target_type=Integer.class)
	})
	public String getNewsDetail(HttpServletRequest request,HttpServletResponse response){
		Map<String, Object> args = null;
		try
		{
			// 获取参数
			args = SchemaBase.parseArg(request, this,
					EnumConverterImpl.getInstance());
			JSONObject jsonObject = newsService.getJSONNewsById((Integer)args.get("newsId"),EnumGenericState.active,false,true);
			ResponseUtils.renderJson(response, ConstantBase.CONTENT_TYPE, jsonObject);
		}
		catch (Exception e)
		{
			ResponseUtils.renderJson(response, e);
		}
		return null;
	}
	
	@RequestMapping("/newsList.htm")
	@ActionArgs({
		@ActionArg(name="nowPage",header="当前页",target_type=Integer.class),
		@ActionArg(name="pageSize",header="每页显示条数",target_type=Integer.class),
		@ActionArg(name="title",header="标题",required=false),
		@ActionArg(name="type",header="类别" ,required=false,target_type=Byte.class),
		@ActionArg(name="startTime",header="开始时间" ,required=false,target_type=Timestamp.class),
		@ActionArg(name="endTime",header="结束时间" ,required=false,target_type=Timestamp.class)
	})
	//网站公告列表
	public String getNews(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		JSONObject jsonObject = new JSONObject();
		try {
			map = SchemaBase.parseArg(request, this, EnumConverterImpl.getInstance());
			int nowPage = (Integer)map.get("nowPage");
			int pageSize = (Integer)map.get("pageSize");
			if(nowPage<0){
				nowPage = 0;
			}
			JSONArray jsonArray = newsService.getJSONNewsList(
					nowPage, (Integer)map.get("pageSize"), 
					(String)map.get("title"),
					EnumGenericState.active.byteObject(),
					(Byte)map.get("type"),
					EnumNewsOrderBy.hidden.byteObject(),
					true,
					(Timestamp)map.get("startTime"), 
					(Timestamp)map.get("endTime"));
			int count = newsService.getNewsCount(
					(String)map.get("title"),
					EnumGenericState.active.byteObject(),
					(Byte)map.get("type"),
					EnumNewsOrderBy.hidden.byteObject(),
					true,
					(Timestamp)map.get("startTime"),
					(Timestamp)map.get("endTime"));
			jsonObject.put("count",count);
			if(pageSize>0){
				count = count%pageSize==0?count/pageSize:count/pageSize+1;
			}
			jsonObject.put("newsList",jsonArray);
			jsonObject.put("pageCount",count);
			ResponseUtils.renderJson(response, ConstantBase.CONTENT_TYPE, jsonObject);
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
		return null;
	}
	
	@RequestMapping("/indexNewsList.htm")
	@ActionArgs({
		@ActionArg(name="nowPage",header="当前页",required=false,target_type=Integer.class),
		@ActionArg(name="pageSize",header="每页显示条数",required=false,target_type=Integer.class),
		@ActionArg(name="title",header="标题",required=false),
		@ActionArg(name="type",header="类别",required=false,target_type=Integer.class),
		@ActionArg(name="startTime",header="开始时间",required=false, target_type=Timestamp.class),
		@ActionArg(name="endTime",header="结束时间",required=false, target_type=Timestamp.class)
	})
	//首页网站公告
	public String getIndexNewsList(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		JSONObject jsonObject = new JSONObject();
		try {
			map = SchemaBase.parseArg(request, this, EnumConverterImpl.getInstance());
			int nowPage = (Integer)map.get("nowPage");
			if(nowPage<0){
				nowPage = 0;
			}
			int pageSize = (Integer)map.get("pageSize");
			JSONArray jsonArray = newsService.getJSONNewsList(
					nowPage,
					(Integer)map.get("pageSize"), 
					(String)map.get("title"),
					EnumGenericState.active.byteObject(),
					(Byte)map.get("type"),
					EnumNewsOrderBy.show.byteObject(),
					true,
					(Timestamp)map.get("startTime"),
					(Timestamp)map.get("endTime"));
			/*int count = newsService.getNewsCount(map.get("title").toString(), (Integer)map.get("type"),
					0, (Timestamp)map.get("startTime"), (Timestamp)map.get("endTime"));
			if(pageSize>0){
				count = count%pageSize==0?count/pageSize:count/pageSize+1;
			}*/
			jsonObject.put("newsList",jsonArray);
			//jsonObject.put("pageCount",count);
			ResponseUtils.renderJson(response, ConstantBase.CONTENT_TYPE, jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.renderJson(response, e);
		}
		return null;
	}
}
