package cn.tongyuankeji.entity.structure;

import java.sql.Timestamp;
import java.util.Date;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.common.parameters.EnumPersonState;
import cn.tongyuankeji.entity.BaseEntity;

/**
 * Company entity. @author MyEclipse Persistence Tools
 */

public class Company extends BaseEntity implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer sysId;
	private String title;
	private String companyCode;
	private Byte state;
	private Timestamp createAt;
	private Integer createdBy;
	private Timestamp modifiedAt;
	private Integer modifiedBy;
	private String modifiedByFullName;
	private Date expiryDate;
	private String industryCode;
	private String mainBusiness;
	private Integer scale;
	private Integer property;
	private Float turnover;
	private Integer userMax;
	private String regionCode;
	private String address;
	private String officPhone;
	private String email;
	private String homeUrl;
	private String remarks;

	// Constructors

	/** default constructor */
	public Company() {
	}

	/** minimal constructor */
	public Company(String title, String companyCode, Byte state, Timestamp createAt, Integer createdBy,
			Timestamp modifiedAt, Integer modifiedBy, String modifiedByFullName, String industryCode, Integer scale,
			Integer property, Integer userMax, String regionCode, String address) {
		this.title = title;
		this.companyCode = companyCode;
		this.state = state;
		this.createAt = createAt;
		this.createdBy = createdBy;
		this.modifiedAt = modifiedAt;
		this.modifiedBy = modifiedBy;
		this.modifiedByFullName = modifiedByFullName;
		this.industryCode = industryCode;
		this.scale = scale;
		this.property = property;
		this.userMax = userMax;
		this.regionCode = regionCode;
		this.address = address;
	}

	/** full constructor */
	public Company(String title, String companyCode, Byte state, Timestamp createAt, Integer createdBy,
			Timestamp modifiedAt, Integer modifiedBy, String modifiedByFullName, Date expiryDate, String industryCode,
			String mainBusiness, Integer scale, Integer property, Float turnover, Integer userMax, String regionCode,
			String address, String officPhone, String email, String homeUrl, String remarks) {
		this.title = title;
		this.companyCode = companyCode;
		this.state = state;
		this.createAt = createAt;
		this.createdBy = createdBy;
		this.modifiedAt = modifiedAt;
		this.modifiedBy = modifiedBy;
		this.modifiedByFullName = modifiedByFullName;
		this.expiryDate = expiryDate;
		this.industryCode = industryCode;
		this.mainBusiness = mainBusiness;
		this.scale = scale;
		this.property = property;
		this.turnover = turnover;
		this.userMax = userMax;
		this.regionCode = regionCode;
		this.address = address;
		this.officPhone = officPhone;
		this.email = email;
		this.homeUrl = homeUrl;
		this.remarks = remarks;
	}

	// Property accessors

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
	public String getCompanyCode() {
		return this.companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	@EntityDesc(always = true, makeString = true, enumType = EnumPersonState.class)
	public Byte getState() {
		return this.state;
	}

	public void setState(Byte state) {
		this.state = state;
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

	public String getModifiedByFullName() {
		return this.modifiedByFullName;
	}

	public void setModifiedByFullName(String modifiedByFullName) {
		this.modifiedByFullName = modifiedByFullName;
	}

	public Date getExpiryDate() {
		return this.expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getIndustryCode() {
		return this.industryCode;
	}

	public void setIndustryCode(String industryCode) {
		this.industryCode = industryCode;
	}

	public String getMainBusiness() {
		return this.mainBusiness;
	}

	public void setMainBusiness(String mainBusiness) {
		this.mainBusiness = mainBusiness;
	}

	public Integer getScale() {
		return this.scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public Integer getProperty() {
		return this.property;
	}

	public void setProperty(Integer property) {
		this.property = property;
	}

	public Float getTurnover() {
		return this.turnover;
	}

	public void setTurnover(Float turnover) {
		this.turnover = turnover;
	}

	public Integer getUserMax() {
		return this.userMax;
	}

	public void setUserMax(Integer userMax) {
		this.userMax = userMax;
	}

	public String getRegionCode() {
		return this.regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	@EntityDesc(always = true)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@EntityDesc(always = true)
	public String getOfficPhone() {
		return this.officPhone;
	}

	public void setOfficPhone(String officPhone) {
		this.officPhone = officPhone;
	}

	@EntityDesc(always = true)
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

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}