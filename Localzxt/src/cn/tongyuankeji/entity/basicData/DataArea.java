package cn.tongyuankeji.entity.basicData;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.entity.BaseEntity;

public class DataArea extends BaseEntity implements java.io.Serializable{
	
	// Fields
	private Integer areaId;
	private String areaName;

	// Constructors

	/** default constructor */
	public DataArea() {
	}

	/** full constructor */
	public DataArea(Integer areaId, String areaName) {
		this.areaId = areaId;
		this.areaName = areaName;
	}

	// Property accessors

	@EntityDesc(always = true)
	public Integer getAreaId() {
		return this.areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}
	
	@EntityDesc(always = true)
	public String getAreaName() {
		return this.areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

}
