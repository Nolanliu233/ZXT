package cn.tongyuankeji.manager.dict;

import net.sf.json.JSONArray;

import cn.tongyuankeji.entity.Trace;
import cn.tongyuankeji.entity.dict.AppSetting;

public interface AppSettingManager
{

	public abstract JSONArray getByScope() throws Exception;

	public abstract void saveAll(String settings, Trace trace) throws Exception;

	public abstract void initRunSetting();
	
	/**
	 * 根据key值得到记录
	 * @param key
	 * @return AppSetting
	 */
	public AppSetting getByKey(String key) throws Exception;
}
