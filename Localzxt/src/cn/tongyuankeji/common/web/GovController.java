package cn.tongyuankeji.common.web;

import net.sf.json.JSONObject;

import cn.tongyuankeji.common.cache.McPerson;

public interface GovController
{	
	/**
	 * 根据当前登录用户返回他可见的机构列表
	 * 
	 * @param loginPerson
	 * @param defaultPark
	 *            是否需返回默认园区
	 * @param govId
	 *            目标私企园区id（不能传入默认园区）。只在默认园区用户时使用。有值时，会排除其他私企园区。不使用此条件时，传入null
	 */
	public abstract JSONObject getVisibleGov(McPerson loginPerson, boolean defaultPark, Integer govId) throws Exception;
		
}
