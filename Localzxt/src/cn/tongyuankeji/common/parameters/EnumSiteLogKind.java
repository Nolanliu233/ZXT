package cn.tongyuankeji.common.parameters;

public enum EnumSiteLogKind {
	login((byte) 0), // 登录
	searchInfor((byte) 1), // 查询信息
	addCompanyUser((byte) 2); // 管理员查询用户

	private byte value;

	private EnumSiteLogKind(byte value)
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

	public static EnumSiteLogKind valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumSiteLogKind.values().length; i++)
		{
			if (byteValue == EnumSiteLogKind.values()[i].byteValue())
				return EnumSiteLogKind.values()[i];
		}

		return EnumSiteLogKind.login;
	}
}
