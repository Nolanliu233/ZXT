package cn.tongyuankeji.common.parameters;

/**
 * SiteLog.OperatorKind
 * 
 * @author 代平 2017-05-17 创建
 */
public enum EnumLogOperatorKind
{
	system((byte) 0), // 系统
	backuser((byte) 1), // 后台用户
	user((byte) 2), // 前台用户
	gov((byte)3);	// 园区用户

	private byte value;

	private EnumLogOperatorKind(byte value)
	{
		this.value = value;
	}

	public byte byteValue()
	{
		return value;
	}

	public Byte byteObject()
	{
		return new Byte(value);
	}

	public static EnumLogOperatorKind valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumLogOperatorKind.values().length; i++)
		{
			if (byteValue == EnumLogOperatorKind.values()[i].byteValue())
				return EnumLogOperatorKind.values()[i];
		}

		return EnumLogOperatorKind.system;
	}
}
