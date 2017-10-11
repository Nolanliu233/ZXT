package cn.tongyuankeji.entity.log;

import java.sql.Timestamp;

import cn.tongyuankeji.entity.BaseEntity;

/**
 * ViewLog entity. @author MyEclipse Persistence Tools
 */

public class ViewLog extends BaseEntity implements java.io.Serializable {

	// Fields

	private Integer sysId;
	private Integer userId;
	private Integer informationId;
	private Timestamp viewAt;
	private String remark;

	// Constructors

	/** default constructor */
	public ViewLog() {
	}

	/** minimal constructor */
	public ViewLog(Integer userId, Integer informationId, Timestamp viewAt) {
		this.userId = userId;
		this.informationId = informationId;
		this.viewAt = viewAt;
	}

	/** full constructor */
	public ViewLog(Integer userId, Integer informationId, Timestamp viewAt, String remark) {
		this.userId = userId;
		this.informationId = informationId;
		this.viewAt = viewAt;
		this.remark = remark;
	}

	// Property accessors

	public Integer getSysId() {
		return this.sysId;
	}

	public void setSysId(Integer sysId) {
		this.sysId = sysId;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getInformationId() {
		return this.informationId;
	}

	public void setInformationId(Integer informationId) {
		this.informationId = informationId;
	}

	public Timestamp getViewAt() {
		return this.viewAt;
	}

	public void setViewAt(Timestamp viewAt) {
		this.viewAt = viewAt;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}