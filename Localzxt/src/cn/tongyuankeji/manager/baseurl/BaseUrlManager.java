package cn.tongyuankeji.manager.baseurl;

import java.util.List;

import cn.tongyuankeji.entity.baseurl.BaseUrl;
import net.sf.json.JSONObject;

public interface BaseUrlManager {
	
	public abstract JSONObject selectAllBaseUrl(Integer pageStart, Integer pageSize,String siteName,String siteUrl) throws Exception;
	
	public abstract int getCount(String siteName,String siteUrl) throws Exception;
	
	public abstract void addBaseUrlData(BaseUrl baseUrl) throws Exception;
	
	public abstract BaseUrl selectBaseUrl(int UrlID) throws Exception;
	
	public abstract void deleteBaseUrl(BaseUrl baseUrl) throws Exception;
	
	public abstract void updateBaseUrl(BaseUrl baseUrl) throws Exception;
	
	public JSONObject getSelectBaseUrl() throws Exception;

}
