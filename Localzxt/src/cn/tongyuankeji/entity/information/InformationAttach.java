package cn.tongyuankeji.entity.information;

import java.sql.Timestamp;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.common.parameters.EnumAttachCreateType;
import cn.tongyuankeji.entity.BaseEntity;

/**
 * InformationAttach entity. @author MyEclipse Persistence Tools
 */

public class InformationAttach extends BaseEntity implements java.io.Serializable{

	// Fields

	private Integer sysId;
	private Integer infoId;
	private String attachUrl;
	private String baseUrl;
	private String path;
	private String saveName;
	private String displayName;
	private Integer fileSize;
	private Timestamp createdAt;
	private Integer createdBy;
	private Byte createdType;

	// Constructors

	/** default constructor */
	public InformationAttach() {
	}

	/** minimal constructor */
	public InformationAttach(Integer infoId, Byte createdType) {
		this.infoId = infoId;
		this.createdType = createdType;
	}

	/** full constructor */
	public InformationAttach(Integer infoId, String attachUrl, String baseUrl, String path, String saveName,
			String displayName, Integer fileSize, Timestamp createdAt, Integer createdBy, Byte createdType) {
		this.infoId = infoId;
		this.attachUrl = attachUrl;
		this.baseUrl = baseUrl;
		this.path = path;
		this.saveName = saveName;
		this.displayName = displayName;
		this.fileSize = fileSize;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
		this.createdType = createdType;
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
	public Integer getInfoId() {
		return this.infoId;
	}

	public void setInfoId(Integer infoId) {
		this.infoId = infoId;
	}

	@EntityDesc(always = true)
	public String getAttachUrl() {
		return this.attachUrl;
	}

	public void setAttachUrl(String attachUrl) {
		this.attachUrl = attachUrl;
	}

	@EntityDesc(always = true)
	public String getBaseUrl() {
		return this.baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@EntityDesc(always = true)
	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@EntityDesc(always = true)
	public String getSaveName() {
		return this.saveName;
	}

	public void setSaveName(String saveName) {
		this.saveName = saveName;
	}

	@EntityDesc(always = true)
	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@EntityDesc(always = true)
	public Integer getFileSize() {
		return this.fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	@EntityDesc()
	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@EntityDesc()
	public Integer getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	@EntityDesc(always = true,makeString=true,enumType=EnumAttachCreateType.class)
	public Byte getCreatedType() {
		return this.createdType;
	}

	public void setCreatedType(Byte createdType) {
		this.createdType = createdType;
	}

}