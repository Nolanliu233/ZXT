package cn.tongyuankeji.common.parameters;

/**
 * AppSetting.Scope
 * 
 * @author 代平 2017-05-02 创建
 */

public enum EnumAppSettingScope
{
	hidden,			// 所有，只在初始化系统时使用
	read_only,		// 用户只读，只在后台的系统配置显示时使用
	configurable	// 用户可编辑，在后台的系统配置编辑 保存时使用
}

