package cn.tongyuankeji.entity.dict;

import cn.tongyuankeji.common.web.RunSetting;
import cn.tongyuankeji.entity.BaseEntity;

/**
 * AppSetting entity.
 * 
 * @author 代平 2017-05-15 创建
 */

public class AppSetting extends BaseEntity implements java.io.Serializable, RunSetting
{
	private String settingKey;
	private String settingValue = "";
	private Byte scope = 1	/*EnumAppSettingScope.read_only*/;
	private String title = "";
	private Short displayOrder;

	// Constructors
	private AppSetting()
	{
	}

	// Property accessors

	public String getSettingKey()
	{
		return this.settingKey;
	}

	public void setSettingKey(String settingKey)
	{
		this.settingKey = settingKey;
	}

	public String getSettingValue()
	{
		return this.settingValue;
	}

	public void setSettingValue(String settingValue)
	{
		this.settingValue = settingValue;
	}

	public Byte getScope()
	{
		return this.scope;
	}

	public void setScope(Byte scope)
	{
		this.scope = scope;
	}

	public String getTitle()
	{
		return this.title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public Short getDisplayOrder()
	{
		return displayOrder;
	}

	public void setDisplayOrder(Short displayOrder)
	{
		this.displayOrder = displayOrder;
	}
}