package cn.tongyuankeji.common.cache;

import java.sql.Timestamp;

import cn.tongyuankeji.common.util.DateUtils;
import cn.tongyuankeji.common.web.RunSettingImpl;
import cn.tongyuankeji.entity.Person;

/**
 * 放入缓存中的登录人，可能是用户，或用户
 * 
 * @author 代平
 */
public class McPerson implements Person, java.io.Serializable
{
	/**
	 * 保留这个version number，在其他工程中，从XMemcached中获取的实例需要用这个Version Number进行反序列化
	 */
	private static final long serialVersionUID = 7981560250804078637L;
	
	private Integer operatorId;
	private Byte operatorKind;
	
	private Integer roleId; // may be null
	private String username;
	
	private Integer sysId;
	private String loginName; // 网页上的显示名字：userName, 姓+手机号后4位
	private String keyName; // 识别名（水印）：如 姓+手机号
	private String thumbFile;
	private Integer ownerGovId;
	private Integer[] dataTypes;
	private Byte state;
	
	private Timestamp lastPingAt; // 最近一次刷新时间
	
	
	public McPerson(Person concrete)
	{
		this.operatorId = concrete.getOperatorId();
		this.operatorKind = concrete.getOperatorKind();

		this.ownerGovId = concrete.getOwnerGovId();
		this.roleId = concrete.getRoleId();
		this.username = concrete.getUserName();
		
		this.sysId = concrete.getSysId();
		this.loginName = concrete.getLoginName();
		this.keyName = concrete.getKeyName();
		this.thumbFile = concrete.getThumbFile();
		this.state = concrete.getState();
		this.dataTypes = concrete.getDataTypes();
		
		this.refreshPing();
	}
	
	@Override
	public Integer getOperatorId()
	{
		return operatorId;
	}
	
	@Override
	public Byte getOperatorKind()
	{
		return this.operatorKind;
	}
	
	@Override
	public Integer getRoleId()
	{
		return roleId;
	}
	
	@Override
	public String getUserName()
	{
		return username;
	}
	
	@Override
	public Integer getSysId()
	{
		return sysId;
	}
	
	@Override
	public String getLoginName()
	{
		return loginName;
	}
	
	@Override
	public String getKeyName()
	{
		return keyName;
	}
	
	@Override
	public String getThumbFile()
	{
		return thumbFile;
	}
	
	public Integer[] getDataTypes() {
		return dataTypes;
	}

	@Override
	public Byte getState()
	{
		return state;
	}
	
	public Integer getOwnerGovId()
	{
		return ownerGovId;
	}
	
	public Timestamp getLastPingAt()
	{
		return lastPingAt;
	}
	
	/*---------------------------------- Helper ----------------------------------*/
	/**
	 * 客户端最近一次操作时，刷新ping时刻
	 */
	public boolean refreshPing()
	{
		// 不要频繁往缓存放，当与上次ping间隔超过 “超时” 一半的，才放一次
		if (DateUtils.minutesDiff(this.getLastPingAt(), DateUtils.now()) > RunSettingImpl.getSESSION_EXPIRE_MINUTE() / 2)
		{
			this.lastPingAt = DateUtils.now();
			return true;
		}
		
		return false;
	}
	
	/**
	 * 修改用户/后台用户信息后，同步缓存
	 * 
	 * @param concrete
	 *            被保存的siteuser或student实例
	 */
	public void update(Person concrete)
	{
		this.username = concrete.getUserName();
		this.loginName = concrete.getLoginName();
		this.thumbFile = concrete.getThumbFile();
		this.state = concrete.getState();
		this.keyName = concrete.getKeyName();
		this.dataTypes = concrete.getDataTypes();
	}
	

}
