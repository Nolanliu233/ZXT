package cn.tongyuankeji.entity.information;

import java.sql.Timestamp;
import java.util.Date;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.common.parameters.EnumContentPageState;
import cn.tongyuankeji.entity.BaseEntity;

public class InformationSources extends BaseEntity implements java.io.Serializable{
	
	private Integer sysId;
	private String title;
	private String coverUrl;
	private String path;
	private Integer type;
	private String informationFromTitle;
	private String informationFromUrl;
	private Integer level;
	private Date releaseAt;
	private Date beginAt;
	private Date endAt;
	private Timestamp closedAt;
	private Integer closedBy;
	private String url;
	private String hashCode;
	private Float dynamicScore;
	private Float staticScore;
	private Byte state;
	private Integer browseCount;
	private Integer areaid;
	private Timestamp cawlerTime;
	private String detailSource;
	private Integer baseUrlId;
	private String remarks;

	// Constructors

	/** default constructor */
	public InformationSources() {
	}

	/** minimal constructor */
	public InformationSources(String title, String path, String informationFromTitle, String informationFromUrl,
			Integer level, String url, Byte state, Integer browseCount, Timestamp cawlerTime) {
		this.title = title;
		this.path = path;
		this.informationFromTitle = informationFromTitle;
		this.informationFromUrl = informationFromUrl;
		this.level = level;
		this.url = url;
		this.state = state;
		this.browseCount = browseCount;
		this.cawlerTime = cawlerTime;
	}

	/** full constructor */
	public InformationSources(String title, String coverUrl, String path, Integer type, 
			String informationFromTitle, String informationFromUrl, Integer level, Date beginAt, Date endAt,
			Timestamp closedAt, Integer closedBy, String url, String hashCode, Float dynamicScore, Float staticScore,
			Byte state, Integer browseCount, Integer areaid, Timestamp cawlerTime, String detailSource,
			Integer baseUrlId) {
		this.title = title;
		this.coverUrl = coverUrl;
		this.path = path;
		this.type = type;
		this.informationFromTitle = informationFromTitle;
		this.informationFromUrl = informationFromUrl;
		this.level = level;
		this.beginAt = beginAt;
		this.endAt = endAt;
		this.closedAt = closedAt;
		this.closedBy = closedBy;
		this.url = url;
		this.hashCode = hashCode;
		this.dynamicScore = dynamicScore;
		this.staticScore = staticScore;
		this.state = state;
		this.browseCount = browseCount;
		this.areaid = areaid;
		this.cawlerTime = cawlerTime;
		this.detailSource = detailSource;
		this.baseUrlId = baseUrlId;
	}

	@EntityDesc(always = true)
	public Integer getSysId() {
		return this.sysId;
	}

	public void setSysId(Integer sysId) {
		this.sysId = sysId;
	}

	@EntityDesc(always = true)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@EntityDesc(always = true)
	public String getCoverUrl() {
		return this.coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	@EntityDesc(always = true)
	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@EntityDesc(always = true)
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@EntityDesc(always = true)
	public String getInformationFromTitle() {
		return this.informationFromTitle;
	}

	public void setInformationFromTitle(String informationFromTitle) {
		this.informationFromTitle = informationFromTitle;
	}

	@EntityDesc(always = true)
	public String getInformationFromUrl() {
		return this.informationFromUrl;
	}

	public void setInformationFromUrl(String informationFromUrl) {
		this.informationFromUrl = informationFromUrl;
	}

	@EntityDesc(always = true)
	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@EntityDesc(always = true)
	public Date getReleaseAt() {
		return this.releaseAt;
	}

	public void setReleaseAt(Date releaseAt) {
		this.releaseAt = releaseAt;
	}
	
	@EntityDesc(always = true)
	public Date getBeginAt() {
		return this.beginAt;
	}

	public void setBeginAt(Date beginAt) {
		this.beginAt = beginAt;
	}

	@EntityDesc(always = true)
	public Date getEndAt() {
		return this.endAt;
	}

	public void setEndAt(Date endAt) {
		this.endAt = endAt;
	}

	@EntityDesc(always = true)
	public Timestamp getClosedAt() {
		return this.closedAt;
	}

	public void setClosedAt(Timestamp closedAt) {
		this.closedAt = closedAt;
	}

	@EntityDesc(always = true)
	public Integer getClosedBy() {
		return this.closedBy;
	}

	public void setClosedBy(Integer closedBy) {
		this.closedBy = closedBy;
	}

	@EntityDesc(always = true)
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@EntityDesc(always = true)
	public String getHashCode() {
		return this.hashCode;
	}

	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}

	@EntityDesc(always = true)
	public Float getDynamicScore() {
		return this.dynamicScore;
	}

	public void setDynamicScore(Float dynamicScore) {
		this.dynamicScore = dynamicScore;
	}

	@EntityDesc(always = true)
	public Float getStaticScore() {
		return this.staticScore;
	}

	public void setStaticScore(Float staticScore) {
		this.staticScore = staticScore;
	}

	@EntityDesc(always = true,makeString=true,enumType=EnumContentPageState.class)
	public Byte getState() {
		return this.state;
	}

	public void setState(Byte state) {
		this.state = state;
	}

	@EntityDesc(always = true)
	public Integer getBrowseCount() {
		return this.browseCount;
	}

	public void setBrowseCount(Integer browseCount) {
		this.browseCount = browseCount;
	}

	@EntityDesc(always = true)
	public Integer getAreaid() {
		return this.areaid;
	}

	public void setAreaid(Integer areaid) {
		this.areaid = areaid;
	}

	@EntityDesc(always = true)
	public Timestamp getCawlerTime() {
		return this.cawlerTime;
	}

	public void setCawlerTime(Timestamp cawlerTime) {
		this.cawlerTime = cawlerTime;
	}

	@EntityDesc(always = true)
	public String getDetailSource() {
		return this.detailSource;
	}

	public void setDetailSource(String detailSource) {
		this.detailSource = detailSource;
	}

	@EntityDesc(always = true)
	public Integer getBaseUrlId() {
		return this.baseUrlId;
	}

	public void setBaseUrlId(Integer baseUrlId) {
		this.baseUrlId = baseUrlId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
