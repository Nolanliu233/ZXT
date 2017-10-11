package cn.tongyuankeji.common.cache;

public interface McBase extends java.io.Serializable
{
	/**
	 * xmc中的键，保存在XmcManagerImpl.CACHE_KEYS中
	 */
	public abstract String getCacheKey();	
}
