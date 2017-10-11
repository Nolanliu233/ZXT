package cn.tongyuankeji.entity.news;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.entity.BaseEntity;

public class NewsType extends BaseEntity implements java.io.Serializable{

	private Integer typeId;
	private String typeName;
	@EntityDesc(always = true)
	public Integer getTypeId() {
		return typeId;
	}
	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}
	@EntityDesc(always = true)
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
