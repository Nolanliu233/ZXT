package cn.tongyuankeji.entity.structure;

import java.sql.Timestamp;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.entity.BaseEntity;

/**
 * Role entity.
 * 
 * @author 代平  017-05-15 创建
 */
public class Role extends BaseEntity implements java.io.Serializable
{
	// Fields
	
	private Integer sysId;
	private Byte state = EnumGenericState.active.byteValue();
	private String code;
	private Timestamp modifiedAt;
	private Integer modifiedBy;
	private String modifiedByFullname;
	private String title;
	private Boolean isSystemManager;
	private String ACL;
	private String remarks;
	
	// Constructors
	
	/** 系统ctor */
	public Role()
	{}
	
	/** 新建ctor */
	public Role(boolean isSystemManager)
	{
		this.isSystemManager = isSystemManager;
	}
	
	public Role(Byte state, String code, Timestamp modifiedAt, Integer modifiedBy, String modifiedByFullname,
			String title, Boolean isSystemManager, String ACL,String remarks)
	{
		this.state = state;
		this.code = code;
		this.modifiedAt = modifiedAt;
		this.modifiedBy = modifiedBy;
		this.modifiedByFullname = modifiedByFullname;
		this.title = title;
		this.isSystemManager = isSystemManager;
		this.ACL = ACL;
		this.remarks = remarks;
	}
	// Property accessors
	@EntityDesc(always = true)
	public Integer getSysId()
	{
		return this.sysId;
	}
	
	public void setSysId(Integer sysId)
	{
		this.sysId = sysId;
	}
	
	@EntityDesc(always = true, makeString = true, enumType = EnumGenericState.class)
	public Byte getState()
	{
		return this.state;
	}
	
	public void setState(Byte state)
	{
		this.state = state;
	}
	
	@EntityDesc(always = true)
	public String getCode()
	{
		return this.code;
	}
	
	public void setCode(String code)
	{
		this.code = code;
	}
	
	@EntityDesc()
	public Timestamp getModifiedAt()
	{
		return this.modifiedAt;
	}
	
	public void setModifiedAt(Timestamp modifiedAt)
	{
		this.modifiedAt = modifiedAt;
	}
	
	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	@EntityDesc()
	public String getModifiedByFullname()
	{
		return this.modifiedByFullname;
	}
	
	public void setModifiedByFullname(String modifiedByFullname)
	{
		this.modifiedByFullname = modifiedByFullname;
	}
	
	@EntityDesc()
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	@EntityDesc(always = true)
	public Boolean getIsSystemManager()
	{
		return isSystemManager;
	}
	
	public void setIsSystemManager(Boolean isSystemManager)
	{
		this.isSystemManager = isSystemManager;
	}
	
	// 不参与BaseEntity.toJson()，不标注@EntityDesc
	public String getACL()
	{
		return ACL;
	}
	
	public void setACL(String aCL)
	{
		ACL = aCL;
	}
	
	@EntityDesc(traceOn = false)
	public String getRemarks()
	{
		return this.remarks;
	}
	
	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}
}
