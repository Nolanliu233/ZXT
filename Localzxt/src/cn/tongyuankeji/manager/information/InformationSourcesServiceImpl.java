package cn.tongyuankeji.manager.information;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.common.parameters.EnumAttachCreateType;
import cn.tongyuankeji.common.parameters.EnumContentPageState;
import cn.tongyuankeji.common.parameters.EnumSolarSortField;
import cn.tongyuankeji.common.util.FileUtils;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.common.web.RunSettingImpl;
import cn.tongyuankeji.common.web.SegmentUtls;
import cn.tongyuankeji.dao.ReadonlyTable;
import cn.tongyuankeji.dao.basicData.DataAreaDao;
import cn.tongyuankeji.dao.basicData.DataLevelDao;
import cn.tongyuankeji.dao.information.InformationAttachDao;
import cn.tongyuankeji.dao.information.InformationSourcesDao;
import cn.tongyuankeji.entity.BaseEntity;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.basicData.DataArea;
import cn.tongyuankeji.entity.basicData.DataLevel;
import cn.tongyuankeji.entity.basicData.DataType;
import cn.tongyuankeji.entity.information.InformationAttach;
import cn.tongyuankeji.entity.information.InformationSources;
import cn.tongyuankeji.manager.basicData.DataAreaService;
import cn.tongyuankeji.manager.basicData.DataLevelService;
import cn.tongyuankeji.manager.basicData.DataTypeService;
import cn.tongyuankeji.manager.user.UserManagerImpl;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

@Service
@Transactional(rollbackFor = Exception.class)
public class InformationSourcesServiceImpl implements InformationSourcesService{

	@Autowired
	private InformationSourcesDao infoSourcesDao;

	@Autowired
	private DataAreaDao areaDao;

	@Autowired
	private DataLevelDao levelDao;
	
	@Autowired
	private DataAreaService areaService;
	@Autowired
	private DataLevelService levelService;
	@Autowired
	private DataTypeService typeService;
	
	@Autowired
	private InformationAttachDao infoAttachDao;
	
	static final Logger log = LogManager.getLogger(UserManagerImpl.class.getName());
	@Override
	public void saveInformationSources(InformationSources infoSources) throws Exception {
		infoSourcesDao.saveOrUpdateInfoSource(infoSources);
	}

	@Override
	public InformationSources getInfoSourcesById(int infoSourcesId, EnumContentPageState stateMin, Boolean lock) throws Exception {
		InformationSources info = infoSourcesDao.getInfoSourceById(infoSourcesId, stateMin, lock);
		return info;
	}

	@Override
	public JSONObject getJSONInfoSourcesById(int infoSourcesId, Boolean isReview) throws Exception {
		
		InformationSources info = null;
		if(!isReview){
			info = getInfoSourcesById(infoSourcesId, EnumContentPageState.active, false);
			if(info == null)
				throw new RuntimeException("未找到该信息！");
			info.setBrowseCount(info.getBrowseCount()+1);
			infoSourcesDao.saveOrUpdateInfoSource(info);
		} else {
			info = getInfoSourcesById(infoSourcesId, EnumContentPageState.closed, false);
			if(info == null)
				throw new RuntimeException("未找到该信息！");
		}
		
		//将地区、等级、类型转换成中文
		int areaId = info.getAreaid();
		Integer typeId = info.getType();
		Integer levelId = info.getLevel();
		DataArea dataArea = areaService.getDataAreaById(areaId);
		DataLevel dataLevel = levelService.getDataLevelById(levelId);
		JSONObject jsonObject = info.toJson(true, false);
		if(typeId != null){
			DataType dataType = typeService.getDataTypeById(typeId);
			jsonObject.put("typeName",dataType.getTypeName());
		}
		jsonObject.put("areaName",dataArea.getAreaName());
		jsonObject.put("levelName",dataLevel.getLevelName());

		jsonObject.put("title",jsonObject.get("title"));
		jsonObject.put("informationFromTitle",jsonObject.get("informationFromTitle"));
		jsonObject.put("browseCount", jsonObject.get("browseCount"));
		jsonObject.put("endAt", JSONNull.getInstance().equals(jsonObject.get("endAt"))?"永久有效":jsonObject.get("endAt"));
		String fileContent = null;
		try {
			fileContent = FileUtils.readFile(ConstantBase.getUPLOAD_PATH() + RunSettingImpl.getUPLOAD_CONTENT_SOURCE_PATH() + jsonObject.getString("path"));
		} catch (Exception e) {
			throw new RuntimeException("读取正文文件失败："+e.getLocalizedMessage());
		}
		jsonObject.put("content",fileContent);
		List<InformationAttach> attachList = infoAttachDao.getAttachmentsByInfoId(infoSourcesId);
		if(attachList != null && attachList.size() > 0)
			jsonObject.put("attach",BaseEntity.list2JsonArray(attachList, false, false));
		return jsonObject;
	}

	/**
	 * 后台管理获取信息符合条件的记录（调用存储过程）
	 * @param fuzzy 模糊查询关键字
	 * @param typeIds 消息口数组
	 * @param createdAtBegin 创建记录开始时间
	 * @param createdAtEnd 常见记录结束时间
	 * @param deadLineAtBegin 申报截止如期开始日期
	 * @param deadLineAtEnd 申报截止如期结束日期
	 * @param pageStart 
	 * @param pageSize
	 * */
	@Override
	public JSONObject searchInfoList(String fuzzy, int[] typeIds, Date createdAtBegin, Date createdAtEnd,
			Date deadLineAtBegin, Date deadLineAtEnd, EnumSolarSortField sortFeild, Boolean isDesc, Integer pageStart, Integer pageSize) throws Exception {
		JSONObject result = new JSONObject();
		JSONArray InfoList = new JSONArray();
		JSONObject oentry = null;
		Integer count = null;
		pageSize = pageSize == null ? RunSettingImpl.getPAGINATION_COUNT() : pageSize;
		pageStart = (pageStart == null || pageStart <= 0) ? 0 : pageStart;
		//拼接消息口为String
		StringBuffer typeIdsString = new StringBuffer();
		if(typeIds!=null)
		for(int i=0;i<typeIds.length;i++){
			typeIdsString.append(","+typeIds[i]);
		}
		if(typeIdsString.length()>0)
		typeIdsString = typeIdsString.deleteCharAt(0);
		//如果没有输入模糊查询条件则查询数据库
		if(fuzzy == null || fuzzy.trim().equals("")){
			//如果不传状态默认查询正常状态数据
			List<ReadonlyTable> lstdt = infoSourcesDao.call("searchInformationList", 
					pageStart * pageSize, pageSize,
					fuzzy, EnumContentPageState.active.byteValue(), 
					typeIds==null?null:typeIdsString.toString(), 
					createdAtBegin, createdAtEnd, deadLineAtBegin, deadLineAtEnd);

			count = lstdt.get(0).get(0, "Count");
			result.put("count", count);
			if (count > 0)
			{
				ReadonlyTable dt = lstdt.get(1);
				for (int i = 0; i < dt.getRowCount(); i++)
				{
					oentry = new JSONObject();
					oentry.put("sysId", dt.get(i, "sysId"));
					oentry.put("title", dt.get(i, "title"));
					oentry.put("coverUrl", dt.get(i, "coverUrl"));
					oentry.put("fromWebTitle", dt.get(i, "fromWebTitle"));
					oentry.put("cawlerTime", dt.get(i, "cawlerTime"));
					oentry.put("typeName", dt.get(i, "typeName"));
					oentry.put("path", RunSettingImpl.getCONTENT_SOURCE_PATH() + dt.get(i, "path"));
					oentry.put("levelName", dt.get(i, "levelName"));
					oentry.put("areaName", dt.get(i, "areaName"));
					oentry.put("beginAt", dt.get(i, "beginAt"));
					oentry.put("endAt", dt.get(i, "endAt"));
					oentry.put("detailSource", dt.get(i, "detailSource"));
					InfoList.add(oentry);
				}
			}
			result.put("infoList", InfoList);
		} else {//有输入模糊查询时查询分词服务
			//TODO 代平 在结果中搜索暂时没做
			result =  SegmentUtls.search(fuzzy, null, createdAtBegin, 
					createdAtEnd, deadLineAtBegin, deadLineAtEnd, typeIds==null?null:typeIdsString.toString(), pageStart, pageSize, 
					sortFeild, isDesc);
		}
		count = result.getInt("count");
		result.put("pageSize", pageSize);
		result.put("pageCount", (count/pageSize) + (count%pageSize > 0 ? 1 : 0) );
		
		return result;
	}
	/**-------------------------------后台管理方法------------------------------------------------------**/

	@Override
	public JSONObject searchInfoDict() throws Exception {
		JSONObject result = new JSONObject();
		result.put("status", EnumContentPageState.displayArray);
		result.put("area", areaDao.getDataAreaList(null));
		result.put("level", levelDao.getDataLevelList(null));
		return result;
	}
	
	/**
	 * 后台管理获取信息符合条件的记录条数
	 * @param fuzzy 模糊查询关键字
	 * @param areaId 区域ID
	 * @param levelId 等级ID
	 * @param state 信息状态
	 * @param pageStart 
	 * @param pageSize
	 * */
	@Override
	public JSONObject backGetList(String fuzzy, Integer areaId, Integer levelId, EnumContentPageState state,
			Integer pageStart, Integer pageSize) throws Exception {
		JSONObject result = new JSONObject();
		JSONArray InfoList = new JSONArray();
		JSONObject oentry = null; 
		pageSize = pageSize == null ? RunSettingImpl.getPAGINATION_COUNT() : pageSize;
		//如果不传状态默认查询正常状态数据
		state = (state == null ? EnumContentPageState.active : state);
		List<ReadonlyTable> lstdt = infoSourcesDao.call("searchInformationListBack", 
				pageStart == null ? 0 : pageStart*pageSize, pageSize,
				fuzzy, areaId, levelId, state.byteValue());
		
		int count = lstdt.get(0).get(0, "Count");
		result.put("pageSize", pageSize);
		result.put("count", count);
		result.put("pageCount", (count/pageSize) + (count%pageSize > 0 ? 1 : 0) );
		if (count > 0)
		{
			ReadonlyTable dt = lstdt.get(1);
			for (int i = 0; i < dt.getRowCount(); i++)
			{
				oentry = new JSONObject();
				oentry.put("sysId", dt.get(i, "sysId"));
				oentry.put("title", dt.get(i, "title"));
				oentry.put("state", dt.get(i, "state"));
				oentry.put("stateZh", EnumContentPageState.titleOf(dt.get(i, "state")));
				oentry.put("path", RunSettingImpl.getCONTENT_SOURCE_PATH() + dt.get(i, "path"));
				oentry.put("fromWebName", dt.get(i, "fromWebName"));
				oentry.put("fromWebURL", dt.get(i, "fromWebURL"));
				oentry.put("endAt", dt.get(i, "endAt"));
				oentry.put("areaName", dt.get(i, "areaName"));
				oentry.put("typeName", dt.get(i, "typeName"));
				InfoList.add(oentry);
			}
		}
		result.put("infoList", InfoList);
		
		return result;
	}
	@Override
	public JSONArray getHotInfos(int nowPage, int pageSize, Byte state, String orderBy) throws Exception{
		List<InformationSources> list = infoSourcesDao.getInfoSourceList(nowPage, pageSize, null, state, null, null, orderBy);
		JSONArray jsonArray = BaseEntity.list2JsonArray(list, true, false);
		return jsonArray;
	}
	
	/**
	 * 后台管理获取信息详细数据
	 * */
	@Override
	public JSONObject backGetInfoDetail(Integer infoId) throws Exception {
		// TODO Auto-generated method stub
		JSONObject result = infoSourcesDao.getInfoZhById(infoId);
		if(result == null)
			throw new RuntimeException("未找到该信息");
		String fileContent = null;
		try {
			fileContent = FileUtils.readFile(ConstantBase.getUPLOAD_PATH() + RunSettingImpl.getUPLOAD_CONTENT_SOURCE_PATH() + result.getString("path"));
		} catch (Exception e) {
			throw new RuntimeException("读取正文文件失败："+e.getLocalizedMessage());
		}
		//将状态转换为中文显示
		result.put("statusZh", EnumContentPageState.titleOf(((Integer)result.getInt("status")).byteValue()));
		result.put("content", fileContent);
		
		List<InformationAttach> attachList = infoAttachDao.getAttachmentsByInfoId(infoId);
		
		result.put("attach", attachList);
		return result;
	}
	
	/**
	 * 根据ID查询附件详情
	 * **/
	@Override
	public JSONArray getInfoAttachByInfoId(Integer infoId) throws Exception {
		List<InformationAttach> attachList = infoAttachDao.getAttachmentsByInfoId(infoId);
		
		return attachList == null ? null : BaseEntity.list2JsonArray(attachList, true, true);
	}
	

	/**
	 * 后台管理山粗附件
	 * */
	@Override
	public void deleteAttach(Integer attachId) throws Exception {
		InformationAttach attach = infoAttachDao.getById(attachId, null, true);
		if(attach == null)
			throw new RuntimeException("未找到该附件，或该附件已被删除！");
		infoAttachDao.deleteInfoAttach(attach);
	}

	/**
	 * 后台管修改附件（只限于增加连接方式，上传用uploadHandler）
	 * */
	@Override
	public void editAttach(Integer attachId, String displayName, String attachUrl) throws Exception {
		InformationAttach attach = infoAttachDao.getById(attachId, null, true);
		if(attach == null)
			throw new RuntimeException("未找到该附件，或该附件已被删除！");
		attach.setDisplayName(displayName);
		attach.setAttachUrl(attachUrl);
		infoAttachDao.saveOrUpdate(attach);
	}
	
	/**
	 * 后台管理新增附件（只限于增加连接方式，上传用uploadHandler）
	 * */
	@Override
	public void addAttach(Person person, Integer infoId, String displayName, String attachUrl) throws Exception {
		InformationAttach attach = new InformationAttach();
		attach.setDisplayName(displayName);
		attach.setAttachUrl(attachUrl);
		attach.setCreatedAt(infoAttachDao.now());
		attach.setCreatedBy(person.getSysId());
		attach.setCreatedType(EnumAttachCreateType.link.byteValue());
		attach.setInfoId(infoId);
		infoAttachDao.saveOrUpdate(attach);
	}
	/**
	 * 修改保存信息数据
	 * */
	@Override
	public Byte editInformation(Integer infoId, String title, Date releaseAt, Date beginAt, Date endAt,
			String detailSource, String content) throws Exception {
		InformationSources info = infoSourcesDao.getInfoSourceById(infoId, EnumContentPageState.deleted, false);
		
		if(info == null)
			throw new RuntimeException("未找到相关数据，或该数据已被删除！");
		
		if(beginAt != null && endAt != null && beginAt.getTime() > endAt.getTime()){
			throw new RuntimeException("申报开始日期必须再截止日期之前，请检查！");
		}
		info.setTitle(title);
		info.setReleaseAt(releaseAt);
		info.setBeginAt(beginAt);
		info.setEndAt(endAt);
		info.setDetailSource(detailSource);
		//保存修改数据
		infoSourcesDao.saveOrUpdateInfoSource(info);
		
		//更新html文件内容
		FileUtils.write(
				ConstantBase.getUPLOAD_PATH() + RunSettingImpl.getUPLOAD_CONTENT_SOURCE_PATH() + info.getPath(), 
				content);
		//更新txt文件内容
		FileUtils.write(
				ConstantBase.getUPLOAD_PATH() + RunSettingImpl.getUPLOAD_CONTENT_SOURCE_PATH() + info.getPath().replace("html", "txt"), 
				Utils.replaceBlank(Jsoup.parse(content).text()));
		return info.getState();
	}
	/**
	 * 更改信息状态
	 * **/
	@Override
	public void changeInfoStatus(int infoId, EnumContentPageState toStatus, String remarks) throws Exception {
		InformationSources info = 
				infoSourcesDao.getInfoSourceById(infoId, EnumContentPageState.deleted, false);
		if(info == null)
			throw new RuntimeException("未找到相关数据，或该数据已被删除！");
		
		Byte infoStatus = info.getState();
		//当选择审核不通过时判断该状态是否允许
		if(toStatus == EnumContentPageState.audit_back && 
				(infoStatus == EnumContentPageState.active.byteValue()
				|| infoStatus == EnumContentPageState.closed.byteValue()
				|| infoStatus == EnumContentPageState.audit_back.byteValue())){
			throw new RuntimeException("“"+EnumContentPageState.titleOf(infoStatus)+"”状态的信息不允许次操作");
		}

		info.setState(toStatus.byteValue());
		info.setRemarks(remarks);
		infoSourcesDao.saveOrUpdateInfoSource(info);
		//如果是发布信息则添加分词索引，否则则删除分词索引
		if(toStatus == EnumContentPageState.active){
			SegmentUtls.segmentById(infoId);
		} else if(info.getState() == EnumContentPageState.active.byteValue()){
			SegmentUtls.deleteSegmentById(infoId);
		}
	}
}
