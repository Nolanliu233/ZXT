package cn.tongyuankeji.dao.baseurl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import cn.tongyuankeji.common.parameters.EnumBaseUrlState;
import cn.tongyuankeji.dao.BaseDAOImpl;
import cn.tongyuankeji.entity.baseurl.BaseUrl;
import cn.tongyuankeji.entity.user.User;

@Repository
public class BaseUrlDaoImpl extends BaseDAOImpl<BaseUrl,Integer> implements BaseUrlDao {

	public BaseUrlDaoImpl() {
		super(BaseUrl.class,"UrlID");
	}

	@Override
	public List<BaseUrl> selectAllBaseUrl(Integer pageStart, Integer pageSize,String siteName,String siteUrl,EnumBaseUrlState MinStatus) throws Exception {
		List<Criterion> criterions = new ArrayList<Criterion>();
		
		int type = MinStatus.byteValue();
		criterions.add(Restrictions.ge("CreateRole", type));
		
		if(siteName!=null){
			criterions.add(Restrictions.like("UrlName", "%"+siteName+"%"));
		}
		
		if(siteUrl!=null){
			criterions.add(Restrictions.like("Url", "%"+siteUrl+"%"));
		}
		
		return super.getEntityPage(criterions, null, pageStart, pageSize);
	}

	@Override
	public int getCount(String siteName,String siteUrl ,EnumBaseUrlState MinStatus) throws Exception {
		List<Criterion> criterions = new ArrayList<Criterion>();
		int type = MinStatus.byteValue();
		criterions.add(Restrictions.ge("CreateRole", type));
		
		if(siteName!=null){
			criterions.add(Restrictions.like("UrlName", "%"+siteName+"%"));
		}
		
		if(siteUrl!=null){
			criterions.add(Restrictions.like("Url", "%"+siteUrl+"%"));
		}
		return super.getCount(criterions);
	}
	
	@Override
	public void saveOrUpdate(BaseUrl baseUrl) throws Exception {
		 super.getSession().saveOrUpdate(baseUrl);
		}
	
	@Override
	public BaseUrl selectBaseUrl(int UrlID) throws Exception {
		return super.getById(UrlID, null, false);
	}
}
