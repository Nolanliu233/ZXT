package cn.tongyuankeji.common.parameters;

/**
 * 本工程支持的所有用户上传文件类型
 * 
 * @author Jean 2015-05-02 创建
 */

public enum EnumUploadType
{
	pid((byte) 1), // 用户证照图片
	thumb((byte) 2), // 用户头像图片
	pdfile((byte) 3); // 用户资源
	
	
	private byte value;
	
	private EnumUploadType(byte value)
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
	
	public static EnumUploadType valueOf(byte byteValue)
	{
		for (int i = 0; i < EnumUploadType.values().length; i++)
		{
			if (byteValue == EnumUploadType.values()[i].byteValue())
				return EnumUploadType.values()[i];
		}
		
		return EnumUploadType.thumb;
	}
}
