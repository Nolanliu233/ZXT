package cn.tongyuankeji.common.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.tongyuankeji.common.parameters.EnumSolarSortField;
import cn.tongyuankeji.common.util.DateUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SegmentUtls {

	//根据ID更新数据分词索引
	public static JSONObject segmentById(int infoId){
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("infoId", infoId);
		parms.put("isReSegment", 1);
		String result = HttpClient.sendPost(RunSettingImpl.getSEGMENT_URL() + "/segmentById.htm", parms);
		JSONObject solrReuslt = JSONObject.fromObject(result);
		if(solrReuslt.get("err") != null){
			throw new RuntimeException("重新分词失败("+solrReuslt.getString("err")+")，请重试！");
		}
		return solrReuslt;
	}
	
	//根据ID删除分词索引
	public static void deleteSegmentById(int infoId){
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("infoId", infoId);
		String result = HttpClient.sendPost(RunSettingImpl.getSEGMENT_URL() + "/deleteById.htm", parms);
		JSONObject solrReuslt = JSONObject.fromObject(result);
		if(solrReuslt.get("err") != null){
			throw new RuntimeException("重新分词失败("+solrReuslt.getString("err")+")，请重试！");
		}
	}
	
	//根据ID更新数据分词索引
	public static JSONObject search(String fuzzy, String historyKeyword, Date createdAtBegin, 
			Date createdAtEnd, Date deadLineAtBegin, Date deadLineAtEnd, String typeIds,
			Integer pageStart, Integer pageSize, EnumSolarSortField sortFeild, Boolean isDesc){
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("keyword", fuzzy);
		//TODO 代平 在结果中查询还没有做
		parms.put("historyKeyword", null);
		parms.put("datelineBegin", DateUtils.date2String(createdAtBegin));
		parms.put("datelineEnd", DateUtils.date2String(createdAtEnd));
		parms.put("deadlineBegin", DateUtils.date2String(deadLineAtBegin));
		parms.put("datelineEnd", DateUtils.date2String(deadLineAtEnd));
		parms.put("typeIds", typeIds);
		parms.put("pageStart", pageStart);
		parms.put("pageSize", pageSize);
		parms.put("orderField", sortFeild);
		parms.put("isDesc", isDesc == null ? true : isDesc);
		String resultStr = HttpClient.sendPost(RunSettingImpl.getSEGMENT_URL() + "/search.htm", parms);
		
		JSONObject solrReuslt = JSONObject.fromObject(resultStr), result = new JSONObject(), oentry = null;
		JSONObject response = solrReuslt.getJSONObject("data").getJSONObject("response");
		JSONArray InfoList = new JSONArray();
		Integer count = response.getInt("count");
		if (count != null && count > 0)
		{
			JSONArray docs = response.getJSONArray("docs");
			JSONObject oneData = null;
			for (int i = 0; i < docs.size(); i++)
			{
				oneData = docs.getJSONObject(i);
				oentry = new JSONObject();
				oentry.put("sysId", oneData.get("sysId"));
				oentry.put("title", oneData.get("title"));
				oentry.put("coverUrl", oneData.get("coverUrl"));
				oentry.put("fromWebTitle", oneData.get("fromWebTitle"));
				oentry.put("releaseAt", oneData.get("releaseAt"));
				oentry.put("typeName", oneData.get("typeName"));
				oentry.put("path", RunSettingImpl.getCONTENT_SOURCE_PATH() + oneData.get("path"));
				oentry.put("levelName", oneData.get("levelName"));
				oentry.put("areaName", oneData.get("areaName"));
				oentry.put("beginAt", oneData.get("beginAt"));
				oentry.put("endAt", oneData.get("endAt"));
				oentry.put("detailSource", oneData.get("detailSource"));
				InfoList.add(oentry);
			}
		}
		result.put("count", count);
		result.put("infoList", InfoList);
		result.put("hightlighting", solrReuslt.getJSONObject("data").get("hightlighting"));
		return result;
	}
}
