package cn.tongyuankeji.dao.dict;

import java.util.List;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import cn.tongyuankeji.common.parameters.EnumLogDomainTable;
import cn.tongyuankeji.common.parameters.EnumLogDomainType;
import cn.tongyuankeji.entity.dict.AppSetting;
import org.springframework.stereotype.Repository;

import cn.tongyuankeji.common.parameters.EnumAppSettingScope;
import cn.tongyuankeji.dao.BaseDAOImpl;
import cn.tongyuankeji.entity.Trace;

/**
 * AppSetting DAO
 * 
 * @author 代平 2017-05-15 创建
 */
@Repository
public class AppSettingDAOImpl extends BaseDAOImpl<AppSetting, String> implements AppSettingDAO
{
	// property constants
	public static final String SETTING_VALUE = "settingValue";
	public static final String SCOPE = "scope";
	public static final String TITLE = "title";
	public static final String DISPLAY_ORDER = "displayOrder";

	public AppSettingDAOImpl()
	{
		super(AppSetting.class, "settingKey");
	}

	/**
	 * 返回scope中的配置项，按displayOrder排序
	 */
	@Override
	public List<AppSetting> getByScope(EnumAppSettingScope scope, boolean lock) throws Exception
	{
		return super.getEntityList(new Criterion[] { Restrictions.ge("scope", new Byte((byte) scope.ordinal())) }, new Order[] { Order.asc("displayOrder") }, lock);
	}

	@Override
	public void update(AppSetting setting) throws Exception
	{
		getSession().update(setting);
	}

	/**
	 * 保存配置项后，添加日志
	 */
	@Override
	public void addUpdateTrace(Trace trace, String settingDetail)
	{
		trace.setDomain(EnumLogDomainType.app_setting.byteValue(), EnumLogDomainTable.app_setting.byteValue(), null, "修改系统配置");
		super.addTraceImpl(trace, settingDetail);
	}

	@Override
	public AppSetting getByKey(String key) throws Exception{
		return super.getFirst(new Criterion[]{Restrictions.eq("settingKey", key)},null,false);
	}
}
