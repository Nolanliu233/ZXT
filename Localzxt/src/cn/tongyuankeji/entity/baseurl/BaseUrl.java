package cn.tongyuankeji.entity.baseurl;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.common.parameters.EnumBaseUrlState;
import cn.tongyuankeji.entity.BaseEntity;

public class BaseUrl extends BaseEntity implements java.io.Serializable{

	private Integer UrlID;
	private String Url;
	private String UrlName;
	private Integer UrlType;
	private Integer UrlLevel;
	private Integer UrlArea;
	private Integer CreateRole;
	private String CrawlerState;
	private String StatusDescription;
	private String dataTypeName;
	private String dataLevelName;
	private String dataAreaName;
	

	/** default constructor */
	public BaseUrl() {
	}
	
	/** minimal constructor */
	public BaseUrl(Integer urlID, String url, String urlName, Integer urlType, Integer urlLevel, Integer urlArea,
			Integer createRole) {
		this.UrlID = urlID;
		this.Url = url;
		this.UrlName = urlName;
		this.UrlType = urlType;
		this.UrlLevel = urlLevel;
		this.UrlArea = urlArea;
		this.CreateRole = createRole;
	}



	/** full constructor */
	public BaseUrl(Integer urlID, String url, String urlName, Integer urlType, Integer urlLevel, Integer urlArea,
			Integer createRole, String crawlerState, String statusDescription) {
		this.UrlID = urlID;
		this.Url = url;
		this.UrlName = urlName;
		this.UrlType = urlType;
		this.UrlLevel = urlLevel;
		this.UrlArea = urlArea;
		this.CreateRole = createRole;
		this.CrawlerState = crawlerState;
		this.StatusDescription = statusDescription;
	}
	
	@EntityDesc(always = true)
	public Integer getUrlID() {
		return this.UrlID;
	}

	public void setUrlID(Integer urlID) {
		this.UrlID = urlID;
	}
	
	@EntityDesc(always = true)
	public String getUrl() {
		return this.Url;
	}

	public void setUrl(String url) {
		this.Url = url;
	}

	@EntityDesc(always = true)
	public String getUrlName() {
		return this.UrlName;
	}

	public void setUrlName(String urlName) {
		this.UrlName = urlName;
	}

	@EntityDesc(always = true)
	public Integer getUrlType() {
		return this.UrlType;
	}

	public void setUrlType(Integer urlType) {
		this.UrlType = urlType;
	}

	@EntityDesc(always = true)
	public Integer getUrlLevel() {
		return this.UrlLevel;
	}

	public void setUrlLevel(Integer urlLevel) {
		this.UrlLevel = urlLevel;
	}

	@EntityDesc(always = true)
	public Integer getUrlArea() {
		return this.UrlArea;
	}

	public void setUrlArea(Integer urlArea) {
		this.UrlArea = urlArea;
	}

	@EntityDesc(always = true,enumType = EnumBaseUrlState.class,makeString = true)
	public Integer getCreateRole() {
		return this.CreateRole;
	}

	public void setCreateRole(Integer createRole) {
		this.CreateRole = createRole;
	}

	@EntityDesc(always = true)
	public String getCrawlerState() {
		return this.CrawlerState;
	}

	public void setCrawlerState(String crawlerState) {
		this.CrawlerState = crawlerState;
	}

	@EntityDesc(always = true)
	public String getStatusDescription() {
		return this.StatusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.StatusDescription = statusDescription;
	}

	@EntityDesc()
	public String getDataTypeName() {
		return dataTypeName;
	}

	@EntityDesc()
	public String getDataLevelName() {
		return dataLevelName;
	}

	@EntityDesc()
	public String getDataAreaName() {
		return dataAreaName;
	}

}
