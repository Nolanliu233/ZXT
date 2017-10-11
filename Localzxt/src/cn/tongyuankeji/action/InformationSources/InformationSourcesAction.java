package cn.tongyuankeji.action.InformationSources;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.tongyuankeji.common.annotation.ACLMapping;
import cn.tongyuankeji.common.annotation.ActionArg;
import cn.tongyuankeji.common.annotation.ActionArgs;
import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.common.parameters.EnumContentPageState;
import cn.tongyuankeji.common.parameters.EnumSolarSortField;
import cn.tongyuankeji.common.util.DateUtils;
import cn.tongyuankeji.common.util.EnumConverterImpl;
import cn.tongyuankeji.common.util.ResponseUtils;
import cn.tongyuankeji.common.web.RunSettingImpl;
import cn.tongyuankeji.common.web.SchemaBase;
import cn.tongyuankeji.common.web.SegmentUtls;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.manager.basicData.DataTypeService;
import cn.tongyuankeji.manager.information.InformationSourcesService;

@Controller
public class InformationSourcesAction {

	@Autowired
	private InformationSourcesService infoService;
	
	@RequestMapping("/informationList.htm")
	@ActionArgs({
		@ActionArg(name = "nowPage" ,header = "当前页",target_type=Integer.class, required = false),
		@ActionArg(name = "pageSize", header="每页显示数量",target_type=Integer.class, required = false),
		@ActionArg(name = "title", header="新闻标题",required=false),
		@ActionArg(name = "typeIds[]", header="消息来源",target_type=Integer[].class, required = false),
		@ActionArg(name = "createdAtBegin", header="发布日期开始",required=false, target_type = Date.class),
		@ActionArg(name = "createdAtEnd", header="发布日期结束",required=false, target_type = Date.class),
		@ActionArg(name = "deadLineAtBegin", header="截止日期开始",required=false, target_type = Date.class),
		@ActionArg(name = "deadLineAtEnd", header="截止日期结束",required=false, target_type = Date.class),
		@ActionArg(name = "sortFeild", header="排序方式",required=false, target_type = EnumSolarSortField.class),
		@ActionArg(name = "isDesc", header="排序方式",required=false, target_type = Boolean.class),
		@ActionArg(name = "undead", header="查看未截止项目",required = false, target_type = Boolean.class)
	})
	public void getInfoList(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			map = SchemaBase.parseArg(request, this, EnumConverterImpl.getInstance());
			Date deadLineAtBegin = null;
			if((Boolean)map.get("undead")!=null&&(Boolean)map.get("undead")){
				deadLineAtBegin = DateUtils.timestamp2Date(DateUtils.now());
			}else{
				deadLineAtBegin = (Date)map.get("deadLineAtBegin");
			}
			Integer[] typeInteger = ((Integer[])map.get("typeIds[]"));
			int typeIds[] = null;
			if(typeInteger == null){
				Person p = (Person) request.getAttribute(Person.LOGIN_PERSON);
				typeInteger = p.getDataTypes();
			}
			if(typeInteger != null)
				typeIds = Arrays.stream(typeInteger).mapToInt(Integer::valueOf).toArray();
			ResponseUtils.renderJson(response, "成功", 
					infoService.searchInfoList((String)map.get("title"), 
					typeIds, 
					(Date)map.get("createdAtBegin"), 
					(Date)map.get("createdAtEnd"), 
					deadLineAtBegin,
					(Date)map.get("deadLineAtEnd"), 
					(EnumSolarSortField)map.get("sortFeild"), 
					(Boolean)map.get("isDesc"), 
					(Integer)map.get("nowPage"), 
					(Integer)map.get("pageSize")));
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
		//return "informationSources_list";
	}
	
	/**
	 * 得到最近七天的数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/informationIndex.htm")
	@ActionArgs({
		@ActionArg(name = "nowPage" ,header = "当前页",target_type=Integer.class, required = false),
		@ActionArg(name = "pageSize", header="每页显示数量",target_type=Integer.class, required = false)
	})
	public void getIndexInfoList(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			map = SchemaBase.parseArg(request, this, EnumConverterImpl.getInstance());
			ResponseUtils.renderJson(response, "成功", 
					infoService.searchInfoList(null, null, DateUtils.addDays(DateUtils.now(), -7), null, null, null, null, null, (Integer)map.get("nowPage"), (Integer)map.get("pageSize")));
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
	}
	
	/**
	 * 得到热门资讯
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/getHotInformations.htm")
	@ActionArgs({
		@ActionArg(name = "nowPage" ,header = "当前页",target_type=Integer.class, required = false),
		@ActionArg(name = "pageSize", header="每页显示数量",target_type=Integer.class, required = false)
	})
	public void getHotInfos(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			map = SchemaBase.parseArg(request, this, EnumConverterImpl.getInstance());
			int nowPage = (Integer)map.get("nowPage");
			if(nowPage<0){
				nowPage = 0;
			}
			int pageSize = (Integer)map.get("pageSize");
			if(pageSize<=0){
				pageSize = RunSettingImpl.getPAGINATION_COUNT();
			}
			ResponseUtils.renderJson(response, "成功", 
					infoService.getHotInfos(nowPage, pageSize, EnumContentPageState.active.byteObject(), "browseCount"));
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
	}
	
	/**
	 * 查询信息
	 * @return
	 */
	@RequestMapping("/informationDetail.htm")
	@ActionArgs({
		@ActionArg(name="sysId",header="查询ID",target_type=Integer.class)
	})
	public void getInfoByTitle(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			//TODO 添加访问日志和站点日志
			map = SchemaBase.parseArg(request, this, EnumConverterImpl.getInstance());
			ResponseUtils.renderJson(response, "成功", infoService.getJSONInfoSourcesById((Integer)map.get("sysId"), false));
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
	}
	
	/**
	 * 获取信息附件列表
	 * @return
	 */
	@RequestMapping("/getInfoAttachList.ofc")
	@ACLMapping(subsystem = "backoffice", module = "informationManager", page = "information", opr = "view")
	@ActionArgs({
		@ActionArg(name="infoId",header="信息ID",target_type=Integer.class)
	})
	public void getInfoAttachList(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			//TODO 添加访问日志和站点日志
			map = SchemaBase.validate(request, this, EnumConverterImpl.getInstance());
			ResponseUtils.renderJson(response, "成功", 
					infoService.getInfoAttachByInfoId((Integer)map.get("infoId")));
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
	}
	
	/**
	 * 修改信息附件
	 * @return
	 */
	@RequestMapping("/editInfoAttach.ofc")
	@ACLMapping(subsystem = "backoffice", module = "informationManager", page = "information", opr = "edit")
	@ActionArgs({
		@ActionArg(name="attachId",header="附件ID",target_type=Integer.class),
		@ActionArg(name="displayName",header="附件名称",target_type=String.class),
		@ActionArg(name="attachUrl",header="附件下载连接",target_type=String.class)
	})
	public void editInfoAttach(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			//TODO 添加访问日志和站点日志
			map = SchemaBase.validate(request, this, EnumConverterImpl.getInstance());
			infoService.editAttach(
					(Integer)map.get("attachId"),
					(String)map.get("displayName"),
					(String)map.get("attachUrl"));
			ResponseUtils.renderSuccessJson(response, "成功");
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
	}
	
	/**
	 * 新增信息附件
	 * @return
	 */
	@RequestMapping("/addInfoAttach.ofc")
	@ACLMapping(subsystem = "backoffice", module = "informationManager", page = "information", opr = "edit")
	@ActionArgs({
		@ActionArg(name="infoId",header="信息ID",target_type=Integer.class),
		@ActionArg(name="displayName",header="附件名称",target_type=String.class),
		@ActionArg(name="attachUrl",header="附件下载连接",target_type=String.class)
	})
	public void addInfoAttach(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			//TODO 添加访问日志和站点日志
			map = SchemaBase.validate(request, this, EnumConverterImpl.getInstance());
			infoService.addAttach(
					(Person) request.getAttribute(Person.LOGIN_PERSON),
					(Integer)map.get("infoId"),
					(String)map.get("displayName"),
					(String)map.get("attachUrl"));
			ResponseUtils.renderSuccessJson(response, "成功");
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
	}
	
	/**
	 * 修改信息附件
	 * @return
	 */
	@RequestMapping("/deleteInfoAttach.ofc")
	@ACLMapping(subsystem = "backoffice", module = "informationManager", page = "information", opr = "edit")
	@ActionArgs({
		@ActionArg(name="attachId",header="附件ID",target_type=Integer.class)
	})
	public void deleteInfoAttach(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			//TODO 添加访问日志和站点日志
			map = SchemaBase.validate(request, this, EnumConverterImpl.getInstance());
			infoService.deleteAttach(
					(Integer)map.get("attachId"));
			ResponseUtils.renderSuccessJson(response, "成功");
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
	}
	/**
	 * 查询信息(后台预览)
	 * @return
	 */
	@RequestMapping("/informationDetail.ofc")
	@ActionArgs({
		@ActionArg(name="sysId",header="查询ID",target_type=Integer.class)
	})
	public String informationDetail(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			//TODO 添加访问日志和站点日志
			map = SchemaBase.parseArg(request, this, EnumConverterImpl.getInstance());
			ResponseUtils.renderJson(response, ConstantBase.CONTENT_TYPE, 
					infoService.getJSONInfoSourcesById((Integer)map.get("sysId"), true));
		} catch (Exception e) {
			ResponseUtils.renderJson(response, e);
		}
		return null;
	}
	/**----------------------------------------后台请求-------------------------------------------**/
	/**
	 * 查询信息基础数据
	 * @return
	 */
	@RequestMapping("/searchInfoDict.ofc")
	public void searchInfoDict(HttpServletRequest request,HttpServletResponse response){
		try {
			ResponseUtils.renderJson(response, "成功", infoService.searchInfoDict());
		} catch (Exception e) {
			ResponseUtils.renderErrorJson(response, e.getMessage());
		}
	}
	/**
	 * 查询信息
	 * @return
	 */
	@RequestMapping(value="/searchInformationList.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "informationManager", page = "information", opr = "view")
	@ActionArgs({
		@ActionArg(name="fuzzy",header="标题关键字",target_type=String.class, required = false),
		@ActionArg(name="areaId",header="信息所属区域",target_type=Integer.class, required = false),
		@ActionArg(name="levelId",header="信息等级",target_type=Integer.class, required = false),
		@ActionArg(name="status",header="状态",target_type=EnumContentPageState.class, required = false),
		@ActionArg(name="pageStart",header="第几页",target_type=Integer.class, required = false),
		@ActionArg(name="pageSize",header="每页显示条数",target_type=Integer.class, required = false)
	})
	public void searchInformationList(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			map = SchemaBase.validate(request, this, EnumConverterImpl.getInstance());
			
			ResponseUtils.renderJson(response, "成功", 
					infoService.backGetList((String)map.get("fuzzy"), 
					(Integer)map.get("areaId"), 
					(Integer)map.get("levelId"), 
					(EnumContentPageState)map.get("status"), 
					map.get("pageStart") == null ? 0 : (Integer)map.get("pageStart"), 
					map.get("pageSize") == null ? RunSettingImpl.getPAGINATION_COUNT() : (Integer)map.get("pageSize")));
		} catch (Exception e) {
			ResponseUtils.renderErrorJson(response, e.getMessage());
		}
	}
	
	/**
	 * 根据ID获取信息详细数据
	 * @return
	 */
	@RequestMapping("/getInfoById.ofc")
	@ACLMapping(subsystem = "backoffice", module = "informationManager", page = "information", opr = "view")
	@ActionArgs({
		@ActionArg(name="infoId",header="信息ID",target_type=Integer.class)
	})
	public void getInfoById(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			map = SchemaBase.validate(request, this, EnumConverterImpl.getInstance());
			ResponseUtils.renderJson(response, "成功", infoService.backGetInfoDetail((Integer)map.get("infoId")));
		} catch (Exception e) {
			ResponseUtils.renderErrorJson(response, e.getMessage());
		}
	}
	
	/**
	 * 保存信息数据
	 * @return
	 */
	@RequestMapping(value="/editInformation.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "informationManager", page = "information", opr = "edit")
	@ActionArgs({
		@ActionArg(name="infoId",header="信息ID",target_type=Integer.class),
		@ActionArg(name="title",header="标题",target_type=String.class),
		@ActionArg(name="releaseAt",header="发布日期",target_type=String.class, required = true),
		@ActionArg(name="beginAt",header="申报开始日期",target_type=Date.class, required = false),
		@ActionArg(name="endAt",header="申报截止日期",target_type=Date.class, required = false),
		@ActionArg(name="detailSource",header="描述",target_type=String.class, required = false),
		@ActionArg(name="content",header="页面正文")
	})
	public void editInformation(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			map = SchemaBase.validate(request, this, EnumConverterImpl.getInstance());
			int infoId = (Integer)map.get("infoId");
			Byte infoStatus = infoService.editInformation(
					infoId,
					(String)map.get("title"),
					map.get("releaseAt") == null ? 
							null : DateUtils.string2Date((String)map.get("releaseAt")),
					(Date)map.get("beginAt"),
					(Date)map.get("endAt"),
					(String)map.get("detailSource"),
					(String)map.get("content"));

			//如果该数据为发布数据则更新分词索引
			if(infoStatus == EnumContentPageState.active.byteValue())
				SegmentUtls.segmentById(infoId);
			ResponseUtils.renderSuccessJson(response, "成功");
		} catch (Exception e) {
			ResponseUtils.renderErrorJson(response, e.getMessage());
		}
	}
	
	/**
	 * 查询信息基础数据
	 * @return
	 */
	@RequestMapping(value = "/auditInfo.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "informationManager", page = "information", opr = "audit")
	@ActionArgs({
		@ActionArg(name="infoId",header="信息ID",target_type=Integer.class),
		@ActionArg(name="toStatus",header="发布或驳回",target_type=EnumContentPageState.class),
		@ActionArg(name="remarks",header="备注",target_type=String.class, required = false)
	})
	public void auditInfo(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			map = SchemaBase.validate(request, this, EnumConverterImpl.getInstance());
			EnumContentPageState toStatus = null;
			if(map.get("toStatus") == null){
				toStatus = EnumContentPageState.active;
			} else {
				toStatus = (EnumContentPageState)map.get("toStatus");
			}
			if(toStatus != EnumContentPageState.audit_back && toStatus != EnumContentPageState.active){
				throw new RuntimeException("该操作只能审核发布或审核不通过信息！");
			}
			infoService.changeInfoStatus((Integer)map.get("infoId"), 
					toStatus, 
					(String)map.get("remarks"));
			ResponseUtils.renderSuccessJson(response, "成功");
		} catch (Exception e) {
			ResponseUtils.renderErrorJson(response, e.getMessage());
		}
	}
	
	/**
	 * 标记该页面不是正文页
	 * @return
	 */
	@RequestMapping(value = "/errInformation.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "informationManager", page = "information", opr = "audit")
	@ActionArgs({
		@ActionArg(name="infoId",header="信息ID",target_type=Integer.class),
		@ActionArg(name="remarks",header="备注",target_type=String.class, required = false)
	})
	public void errInformation(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			map = SchemaBase.validate(request, this, EnumConverterImpl.getInstance());
			infoService.changeInfoStatus((Integer)map.get("infoId"), 
					EnumContentPageState.closed, 
					(String)map.get("remarks"));
			ResponseUtils.renderSuccessJson(response, "成功");
		} catch (Exception e) {
			ResponseUtils.renderErrorJson(response, e.getMessage());
		}
	}
	
	/**
	 * 删除信息（逻辑删除）
	 * @return
	 */
	@RequestMapping("/deleteInfo.ofc")
	@ACLMapping(subsystem = "backoffice", module = "informationManager", page = "information", opr = "edit")
	@ActionArgs({
		@ActionArg(name="infoId",header="信息ID",target_type=Integer.class)
	})
	public void deleteInfo(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = null;
		try {
			map = SchemaBase.validate(request, this, EnumConverterImpl.getInstance());
			infoService.changeInfoStatus((Integer)map.get("infoId"), 
					EnumContentPageState.deleted, 
					null);
			ResponseUtils.renderSuccessJson(response, "成功");
		} catch (Exception e) {
			ResponseUtils.renderErrorJson(response, e.getMessage());
		}
	}
}
