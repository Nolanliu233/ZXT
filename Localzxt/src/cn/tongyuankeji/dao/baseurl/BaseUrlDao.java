package cn.tongyuankeji.dao.baseurl;

import java.util.List;

import cn.tongyuankeji.common.parameters.EnumBaseUrlState;
import cn.tongyuankeji.dao.BaseDAO;
import cn.tongyuankeji.entity.baseurl.BaseUrl;

public interface BaseUrlDao extends BaseDAO<BaseUrl>{
	
	public abstract List<BaseUrl> selectAllBaseUrl(Integer pageStart, Integer pageSize,String siteName,String siteUrl,EnumBaseUrlState MinStatus) throws Exception;
	
	public abstract int getCount(String siteName,String siteUrl,EnumBaseUrlState MinStatus) throws Exception;
	
	public abstract void saveOrUpdate(BaseUrl baseUrl) throws Exception;
	
	public abstract BaseUrl selectBaseUrl(int UrlID) throws Exception;
	

}
