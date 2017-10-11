package cn.tongyuankeji.entity.log;

import java.sql.Timestamp;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.common.parameters.EnumClientType;
import cn.tongyuankeji.common.parameters.EnumSiteLogKind;
import cn.tongyuankeji.entity.BaseEntity;

/**
 * SiteLog entity. @author MyEclipse Persistence Tools
 */

public class SiteLog extends BaseEntity implements java.io.Serializable {

	// Fields

	private Integer sysId;
	private Byte kind;
	private Integer ownerGovId;
	private Integer companyId;
	private Integer userId;
	private Byte useClientType;
	private String useDeviceType;
	private String deviceModel;
	private Timestamp createdAt;
	private String createdIp;
	private String body;

	// Constructors

	/** default constructor */
	public SiteLog() {
	}

	/** minimal constructor */
	public SiteLog(Byte kind, Byte useClientType, String useDeviceType, Timestamp createdAt, String createdIp,
			String body) {
		this.kind = kind;
		this.useClientType = useClientType;
		this.useDeviceType = useDeviceType;
		this.createdAt = createdAt;
		this.createdIp = createdIp;
		this.body = body;
	}

	/** full constructor */
	public SiteLog(Byte kind, Integer ownerGovId, Integer companyId, Integer userId, Byte useClientType,
			String useDeviceType, String deviceModel, Timestamp createdAt, String createdIp, String body) {
		this.kind = kind;
		this.ownerGovId = ownerGovId;
		this.companyId = companyId;
		this.userId = userId;
		this.useClientType = useClientType;
		this.useDeviceType = useDeviceType;
		this.deviceModel = deviceModel;
		this.createdAt = createdAt;
		this.createdIp = createdIp;
		this.body = body;
	}

	// Property accessors

	@EntityDesc(always=true)
	public Integer getSysId() {
		return this.sysId;
	}

	public void setSysId(Integer sysId) {
		this.sysId = sysId;
	}
	@EntityDesc(always=true,makeString=true,enumType=EnumSiteLogKind.class)
	public Byte getKind() {
		return this.kind;
	}

	public void setKind(Byte kind) {
		this.kind = kind;
	}

	@EntityDesc(always=true)
	public Integer getOwnerGovId() {
		return this.ownerGovId;
	}

	public void setOwnerGovId(Integer ownerGovId) {
		this.ownerGovId = ownerGovId;
	}
	
	@EntityDesc(always=true)
	public Integer getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	
	@EntityDesc(always=true)
	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	@EntityDesc(always=true,makeString=true,enumType=EnumClientType.class)
	public Byte getUseClientType() {
		return this.useClientType;
	}

	public void setUseClientType(Byte useClientType) {
		this.useClientType = useClientType;
	}

	@EntityDesc(always=true)
	public String getUseDeviceType() {
		return this.useDeviceType;
	}

	public void setUseDeviceType(String useDeviceType) {
		this.useDeviceType = useDeviceType;
	}

	@EntityDesc(always=true)
	public String getDeviceModel() {
		return this.deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	@EntityDesc(always=true)
	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@EntityDesc(always=true)
	public String getCreatedIp() {
		return this.createdIp;
	}

	public void setCreatedIp(String createdIp) {
		this.createdIp = createdIp;
	}

	@EntityDesc(always=true)
	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}