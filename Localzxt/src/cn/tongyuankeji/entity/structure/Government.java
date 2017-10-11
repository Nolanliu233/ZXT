package cn.tongyuankeji.entity.structure;

import java.sql.Timestamp;

import cn.tongyuankeji.entity.BaseEntity;

/**
 * Government entity. @author MyEclipse Persistence Tools
 */
public class Government extends BaseEntity implements java.io.Serializable {

	// Constructors

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** default constructor */
	public Government() {
	}

	// Fields

	private Integer sysId;
	private String title;
	private Byte level;
	private String regionCode;
	private String address;
	private Byte state;
	private Byte kind;
	private String industryCode;
	private String officPhone;
	private String email;
	private String homeUrl;
	private String weichatOpenId;
	private String weixinSignature;
	private String weichatHeadImgUrl;
	private String weichatNickname;
	private Timestamp createAt;
	private Integer createdBy;
	private Timestamp modifiedAt;
	private Integer modifiedBy;


	/** minimal constructor */
	public Government(String title, Byte level, String regionCode, String address, Byte state, Byte kind,
			String industryCode, Timestamp createAt, Integer createdBy, Timestamp modifiedAt, Integer modifiedBy) {
		this.title = title;
		this.level = level;
		this.regionCode = regionCode;
		this.address = address;
		this.state = state;
		this.kind = kind;
		this.industryCode = industryCode;
		this.createAt = createAt;
		this.createdBy = createdBy;
		this.modifiedAt = modifiedAt;
		this.modifiedBy = modifiedBy;
	}

	/** full constructor */
	public Government(String title, Byte level, String regionCode, String address, Byte state, Byte kind,
			String industryCode, String officPhone, String email, String homeUrl, String weichatOpenId,
			String weixinSignature, String weichatHeadImgUrl, String weichatNickname, Timestamp createAt,
			Integer createdBy, Timestamp modifiedAt, Integer modifiedBy) {
		this.title = title;
		this.level = level;
		this.regionCode = regionCode;
		this.address = address;
		this.state = state;
		this.kind = kind;
		this.industryCode = industryCode;
		this.officPhone = officPhone;
		this.email = email;
		this.homeUrl = homeUrl;
		this.weichatOpenId = weichatOpenId;
		this.weixinSignature = weixinSignature;
		this.weichatHeadImgUrl = weichatHeadImgUrl;
		this.weichatNickname = weichatNickname;
		this.createAt = createAt;
		this.createdBy = createdBy;
		this.modifiedAt = modifiedAt;
		this.modifiedBy = modifiedBy;
	}

	// Property accessors

	public Integer getSysId() {
		return this.sysId;
	}

	public void setSysId(Integer sysId) {
		this.sysId = sysId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Byte getLevel() {
		return this.level;
	}

	public void setLevel(Byte level) {
		this.level = level;
	}

	public String getRegionCode() {
		return this.regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Byte getState() {
		return this.state;
	}

	public void setState(Byte state) {
		this.state = state;
	}

	public Byte getKind() {
		return this.kind;
	}

	public void setKind(Byte kind) {
		this.kind = kind;
	}

	public String getIndustryCode() {
		return this.industryCode;
	}

	public void setIndustryCode(String industryCode) {
		this.industryCode = industryCode;
	}

	public String getOfficPhone() {
		return this.officPhone;
	}

	public void setOfficPhone(String officPhone) {
		this.officPhone = officPhone;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHomeUrl() {
		return this.homeUrl;
	}

	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}

	public String getWeichatOpenId() {
		return this.weichatOpenId;
	}

	public void setWeichatOpenId(String weichatOpenId) {
		this.weichatOpenId = weichatOpenId;
	}

	public String getWeixinSignature() {
		return this.weixinSignature;
	}

	public void setWeixinSignature(String weixinSignature) {
		this.weixinSignature = weixinSignature;
	}

	public String getWeichatHeadImgUrl() {
		return this.weichatHeadImgUrl;
	}

	public void setWeichatHeadImgUrl(String weichatHeadImgUrl) {
		this.weichatHeadImgUrl = weichatHeadImgUrl;
	}

	public String getWeichatNickname() {
		return this.weichatNickname;
	}

	public void setWeichatNickname(String weichatNickname) {
		this.weichatNickname = weichatNickname;
	}

	public Timestamp getCreateAt() {
		return this.createAt;
	}

	public void setCreateAt(Timestamp createAt) {
		this.createAt = createAt;
	}

	public Integer getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getModifiedAt() {
		return this.modifiedAt;
	}

	public void setModifiedAt(Timestamp modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public Integer getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

}
