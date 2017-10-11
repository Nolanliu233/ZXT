package cn.tongyuankeji.common.util;

public interface EnumConverter
{
	public abstract Object toEnum(Class enumType, String val) throws Exception;

	public abstract Object[] toArray(Class ttype, String[] values) throws Exception;
	
	public abstract String toText(Class enumType, String val) throws Exception;
}
