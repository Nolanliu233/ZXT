package cn.tongyuankeji.dao.dict;

import java.util.List;

import cn.tongyuankeji.entity.dict.AppSetting;

import cn.tongyuankeji.common.parameters.EnumAppSettingScope;
import cn.tongyuankeji.dao.BaseDAO;
import cn.tongyuankeji.entity.Trace;

public interface AppSettingDAO extends BaseDAO<AppSetting>
{
	/**
	 * 返回scope中的配置项，按displayOrder排序
	 */
	public abstract List<AppSetting> getByScope(EnumAppSettingScope scope, boolean lock) throws Exception;
	
	public abstract void update(AppSetting setting) throws Exception;
	
	/**
	 * 保存配置项后，添加日志
	 */
	public abstract void addUpdateTrace(Trace trace, String settingDetail);
	
	/**
	 * 根据key值得到记录
	 * @param key
	 * @return AppSetting
	 */
	public AppSetting getByKey(String key) throws Exception;
}
