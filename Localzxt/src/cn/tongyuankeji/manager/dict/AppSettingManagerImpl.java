package cn.tongyuankeji.manager.dict;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cn.tongyuankeji.common.web.RunSettingExImpl;
import cn.tongyuankeji.dao.dict.AppSettingDAO;
import cn.tongyuankeji.entity.dict.AppSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tongyuankeji.common.annotation.FieldRange;
import cn.tongyuankeji.common.parameters.EnumAppSettingScope;
import cn.tongyuankeji.common.util.JsonUtils;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.entity.Trace;

/**
 * 系统初始化，或后台管理 系统配置
 * 
 * @author 代平 2017-05-09 创建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppSettingManagerImpl implements AppSettingManager
{
	static final Logger log = LogManager.getLogger(AppSettingManagerImpl.class.getName());
	
	@Autowired
	private AppSettingDAO dao;
	
	/*---------------------------------- 后台管理 ----------------------------------*/
	/**
	 * 后台管理装载AppSetting<br />
	 * 返回的JSON包中，readOnly=1 的，应显示为“只读”；readOnly=0 的，用户可编辑<br />
	 * 返回的JSON包中，isChecked=1 的，用户已勾选；isChecked=0 的，用户未勾选
	 */
	public JSONArray getByScope() throws Exception
	{
		List<AppSetting> lst = dao.getByScope(EnumAppSettingScope.read_only, false);
		
		JSONArray asetting = new JSONArray();
		JSONObject osetting = null;
		Field[] fs = AppSetting.class.getDeclaredFields();
		
		for (AppSetting entry : lst)
		{
			osetting = new JSONObject();
			osetting.put("settingKey", entry.getSettingKey());
			JsonUtils.put(osetting, "settingValue", entry.getSettingValue());
			osetting.put("header", entry.getTitle());
			
			osetting.put("readOnly", entry.getScope() == 1 ? 1 : 0); // EnumAppSettingScope.read_only = 1, configurable
																		// = 2
			for (int j = 0; j < fs.length; j++)
			{
				if (fs[j].getName().equals(entry.getSettingKey()))
				{
					osetting.put("isChecked", (fs[j].getType() == Boolean.class) ? 1 : 0);
					break; // break j
				}
			}
			
			asetting.add(osetting);
		}
		
		return asetting;
	}
	
	/**
	 * 后台的系统配置编辑 保存，只针对AppSetting.scope = 2才进行保存。 此方法要刷新静态AppSetting，所以使用synchronized做共享资源保护。
	 */
	public synchronized void saveAll(String settings, Trace trace) throws Exception
	{
		JSONObject osetting = null;
		String tmp = null;
		String header = null;
		Field[] fs = RunSettingExImpl.class.getDeclaredFields();
		Field[] fssuper = RunSettingExImpl.class.getSuperclass().getDeclaredFields();
		Field fsetting = null;
		FieldRange ann = null;
		JSONArray asetting = JSONArray.fromObject(settings);
		StringBuffer sb = new StringBuffer();
		AppSetting loop = null;
		
		List<AppSetting> lst = dao.getByScope(EnumAppSettingScope.configurable, true), lstChanged = new ArrayList<AppSetting>();
		
		for (int i = 0, len = lst.size(); i < len; i++) // 用数据库查出来的scope=2的链做循环
		{
			loop = lst.get(i);
			
			// 1. find value from user input
			osetting = findSetting(asetting, loop.getSettingKey());
			
			// 2. find corresponding AppSetting field
			fsetting = findSetting(fssuper, loop.getSettingKey());
			if (fsetting == null)
				fsetting = findSetting(fs, loop.getSettingKey());
			
			if (osetting == null || fsetting == null)
				continue;
			
			// 3. 对每个（允许修改的scope=2）输入的值
			header = osetting.getString("header");
			tmp = osetting.getString("settingValue");
			
			ann = fs.getClass().getAnnotation(FieldRange.class);
			
			// 3.1 empty? try default value on annotation
			if (Utils.isBlank(tmp))
			{
				if (ann == null)
					continue;
				else
					tmp = ann.defaultAs();
			}
			
			// 与数据库相同的值，跳过
			if (tmp.equals(loop.getSettingValue()))
				continue;
			
			if (fsetting.getType() == Integer.class)
			{
				// 3.2 数值型的，值范围？
				Integer val;
				try
				{
					val = Integer.valueOf(tmp);
				}
				catch (Exception ex)
				{
					throw new RuntimeException(String.format("%s：请输入整数！", header));
				}
				
				if (ann != null && !Utils.isBlank(ann.min()) && val < Double.valueOf(ann.min()))
					throw new RuntimeException(String.format("%s值超下边界！", header));
				
				if (ann != null && !Utils.isBlank(ann.max()) && val > Double.valueOf(ann.max()))
					throw new RuntimeException(String.format("%s值超上边界！", header));
				
				// 将tmp赋值到DAO对象
				loop.setSettingValue(val.toString());
				dao.update(loop);
				
				if (sb.length() > 0)
					sb.append(",");
				sb.append(fsetting.getName() + "=" + loop.getSettingValue());
				
				lstChanged.add(loop);
			}
			
			else if (fsetting.getType() == Double.class)
			{
				// 3.2 数值型的，值范围？
				Double val;
				
				try
				{
					val = Double.valueOf(tmp);
				}
				catch (Exception ex)
				{
					throw new RuntimeException(String.format("%s：请输入数值！", header));
				}
				
				if (ann != null && !Utils.isBlank(ann.min()) && val < Double.valueOf(ann.min()))
					throw new RuntimeException(String.format("%s值超下边界！", header));
				
				if (ann != null && !Utils.isBlank(ann.max()) && val > Double.valueOf(ann.max()))
					throw new RuntimeException(String.format("%s值超上边界！", header));
				
				// 3.3 数值型的，精度？
				if (fsetting.getType() == Double.class && !Utils.isValidScale(val.toString(), (byte) 2))
					throw new RuntimeException(String.format("%s值精度不正确！", header));
				
				// 将tmp赋值到DAO对象
				loop.setSettingValue(val.toString());
				dao.update(loop);
				
				if (sb.length() > 0)
					sb.append(",");
				sb.append(fsetting.getName() + "=" + loop.getSettingValue());
				
				lstChanged.add(loop);
			}
			else
			{
				loop.setSettingValue(tmp);
				dao.update(loop);
				
				if (sb.length() > 0)
					sb.append(",");
				sb.append(fsetting.getName() + "=" + loop.getSettingValue());
				
				lstChanged.add(loop);
			}
		}
		
		if (!lstChanged.isEmpty())
		{
			dao.addUpdateTrace(trace, sb.toString());
			
			// 5. apply lock on cached AppSetting
			RunSettingExImpl.applySetting(lstChanged);
		}
	}
	
	private JSONObject findSetting(JSONArray asetting, String key)
	{
		for (int i = 0, len = asetting.size(); i < len; i++)
		{
			if (asetting.getJSONObject(i).getString("settingKey").equals(key))
				return asetting.getJSONObject(i);
		}
		
		return null;
	}
	
	private Field findSetting(Field[] fs, String name)
	{
		for (int i = 0; i < fs.length; i++)
		{
			if (fs[i].getName().equals(name))
				return fs[i];
		}
		
		return null;
	}
	
	/*---------------------------------- 系统初始化 ----------------------------------*/
	/**
	 * 网站启动时 系统配置初始化
	 */
	@Override
	public void initRunSetting()
	{
		log.info("Load AppSetting...");
		
		List<AppSetting> lst = null;
		
		try
		{
			lst = dao.getByScope(EnumAppSettingScope.hidden, false);
		}
		catch (Exception ex)
		{
			log.fatal("system initialization failed, unabled to load setting from database", ex);
			return;
		}
		
		RunSettingExImpl.applySetting(lst);
	}

	@Override
	public AppSetting getByKey(String key) throws Exception {
		return dao.getByKey(key);
	}
}
