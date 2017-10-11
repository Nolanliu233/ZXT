package cn.tongyuankeji.common.util;

import cn.tongyuankeji.common.parameters.EnumContentPageState;
import cn.tongyuankeji.common.parameters.EnumGenderKind;
import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.common.parameters.EnumSolarSortField;
import cn.tongyuankeji.common.parameters.EnumUserState;

/**
 * String到enum互转。<br />
 * 配合cn.tongyuankeji.common.parameters中的enum使用。添加新的enum定义时，注意要在下面3个方法中添加
 * 
 * @author 代平 2017-05-10 创建
 */

public class EnumConverterImpl implements EnumConverter
{
	private static EnumConverterImpl instance = null;
	
	public static synchronized EnumConverterImpl getInstance()
	{
		if (null == instance)
		{
			instance = new EnumConverterImpl();
		}
		return instance;
	}
	
	protected EnumConverterImpl()
	{}
	
	@Override
	public Object toEnum(Class enumType, String val)
	{
		assert enumType != null : "toEnum(Class, String)参数enumType不能为空！";
		
		if (enumType == EnumGenderKind.class)
			return Utils.isBlank(val) ? null : EnumGenderKind.valueOf(Byte.valueOf(val).byteValue());
		else if (enumType == EnumGenericState.class)
			return Utils.isBlank(val) ? null : EnumGenericState.valueOf(Byte.valueOf(val).byteValue());
		else if (enumType == EnumUserState.class)
			return Utils.isBlank(val) ? null : EnumUserState.valueOf(Byte.valueOf(val).byteValue());
		else if (enumType == EnumContentPageState.class)
			return Utils.isBlank(val) ? null : EnumContentPageState.valueOf(Byte.valueOf(val).byteValue());
		else if (enumType == EnumSolarSortField.class)
			return Utils.isBlank(val) ? null : EnumSolarSortField.valueOf(Byte.valueOf(val).byteValue());
		return null;
	}
	
	@Override
	public Object[] toArray(Class ttype, String[] values)
	{
		assert ttype != null : "toArray(Class, String[])参数ttype不能为空！";
		assert values != null : "toArray(Class, String[])参数values不能为空！";
		if (ttype == EnumGenderKind[].class)
		{
			EnumGenderKind[] tmparrVal = new EnumGenderKind[values.length];
			for (int i = 0; i < values.length; i++)
				tmparrVal[i] = EnumGenderKind.valueOf(Byte.valueOf(values[i]).byteValue());
			
			return tmparrVal;
		}
		else if (ttype == EnumGenericState[].class)
		{
			EnumGenericState[] tmparrVal = new EnumGenericState[values.length];
			for (int i = 0; i < values.length; i++)
				tmparrVal[i] = EnumGenericState.valueOf(Byte.valueOf(values[i]).byteValue());
			
			return tmparrVal;
		}
		
		else if (ttype == EnumUserState[].class)
		{
			EnumUserState[] tmparrVal = new EnumUserState[values.length];
			for (int i = 0; i < values.length; i++)
				tmparrVal[i] = EnumUserState.valueOf(Byte.valueOf(values[i]).byteValue());
			
			return tmparrVal;
		}
		
		return null;
	}
	
	@Override
	public String toText(Class enumType, String val) throws Exception
	{
		assert enumType != null : "toText(Class, String[])参数enumType不能为空！";
		
		if (enumType == EnumGenderKind.class)
			return Utils.isBlank(val) ? "" : EnumGenderKind.titleOf(Byte.valueOf(val));
		else if (enumType == EnumGenericState.class)
			return Utils.isBlank(val) ? "" : EnumGenericState.titleOf(Byte.valueOf(val));
		else if (enumType == EnumUserState.class)
			return Utils.isBlank(val) ? "" : EnumUserState.titleOf(Byte.valueOf(val));
		
		return "";
	}
}
