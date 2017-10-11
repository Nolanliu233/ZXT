package cn.tongyuankeji.entity;


import javax.servlet.http.HttpServletRequest;

import cn.tongyuankeji.common.exception.LoginExpireException;
import cn.tongyuankeji.common.util.RequestUtils;

/**
 * 系统日志对象。一般在进行数据库写操作（BaseDAO.addTrace()）时才需要
 * 
 * @author 代平 2017-05-02 创建 代平 2017-06-18 改
 * 
 */
public final class Trace
{
	// action入口方法，调用OESchema后，args中OeLog键名称
	public final static String LOG_ARG = "trace";
	
	// Fields
	private byte operatorKind; // EnumLogOperatorKind
	private int operatorId; // 操作人id
	private int ownerGovId;
	
	private byte domainType; // EnumLogDomainType
	private Byte domainTable; // EnumLogDomainTable
	private String domainKey;
	
	private String actionMethod;
	private String actionIP;
	private String actionURI;
	private String body;
	private String friendlyBody;
	
	final static int LOG_FRIENDLY_BODY_LENGTH = 200;
	
	public final static int LOG_BODY_LENGTH = 500;
	
	/**
	 * 被manager或DAO调用，设置操作资料相关性。
	 * 
	 * @param domainType
	 *            EnumLogDomainType
	 * @param domainTable
	 *            EnumLogDomainTable
	 * @param domainKey
	 *            主键，允许null
	 */
	public void setDomain(byte domainType, Byte domainTable, String domainKey, String friendlyBodyFormat, Object... args)
	{
		this.domainType = domainType;
		this.domainTable = domainTable;
		this.domainKey = domainKey;
		this.friendlyBody = args == null || args.length == 0 ? friendlyBodyFormat : String.format(friendlyBodyFormat, args);
		if (this.friendlyBody != null && this.friendlyBody.length() > Trace.LOG_FRIENDLY_BODY_LENGTH)
			this.friendlyBody = this.friendlyBody.substring(0, Trace.LOG_FRIENDLY_BODY_LENGTH - 1);
	}
	
	/**
	 * 登录方法中，Action中取到的operatorId和operatorKind在登录成功后，被真实的登录人信息替换<br />
	 * 只能在DAO.updateLogin()方法中调用
	 * 
	 * @param loginPerson
	 *            登录人
	 */
	public void relaceOperatorWhenLogin(Person loginPerson)
	{
		this.operatorKind = loginPerson.getOperatorKind().byteValue();
		this.operatorId = loginPerson.getOperatorId().intValue();
	}
	
	// properties
	
	public Byte getOperatorKind()
	{
		return operatorKind;
	}
	
	public Integer getOperatorId()
	{
		return operatorId;
	}
	
	public Integer getOwnerGovId()
	{
		return ownerGovId;
	}
	
	public Byte getDomainType()
	{
		return this.domainType;
	}
	
	public Byte getDomainTable()
	{
		return domainTable;
	}
	
	public String getDomainKey()
	{
		return domainKey;
	}
	
	public String getActionMethod()
	{
		return actionMethod;
	}
	
	public String getActionIP()
	{
		return actionIP;
	}
	
	public String getActionURI()
	{
		return actionURI;
	}
	
	public String getBody()
	{
		return this.body;
	}
	
	public String getFriendlyBody()
	{
		return friendlyBody;
	}
	
	/**
	 * 从Action进入时，由SchemaBase创建。使用此实例的DAO类应调用Trace.setDomain()
	 * 
	 * @param req
	 *            HttpServletRequest
	 * @param rootMethodName
	 *            actionMethodName
	 */
	public Trace(HttpServletRequest req, String rootMethodName)
	{
		// 有登录人时
		Person loginPerson = (Person) req.getAttribute(Person.LOGIN_PERSON); // 在EducationContextInterceptor.preHandle()中设置
		if (loginPerson != null)
		{
			this.operatorKind = loginPerson.getOperatorKind().byteValue();
			this.operatorId = loginPerson.getOperatorId().intValue();
		}
		else
		{
			this.operatorKind = 0; /* EnumLogOperatorKind.system */
			this.operatorId = 0;
			this.ownerGovId = 0;
		}
		
		this.actionMethod = rootMethodName;
		this.actionIP = RequestUtils.getIpAddr(req);
		this.actionURI = RequestUtils.getUri(req);
		
		// ParYear、ActionAt在BaseDAO.addTraceImpl()时设置
		
		// domainType, domainTable, domainKey 由setDomain()设置
		
		// Body在BaseDAOImpl.addTrace()中组合，并设置
		
		// FriendlyBody由manager设置
	}
	
	/**
	 * 系统自动调用时使用
	 * 
	 * @param rootMethodName
	 *            actionMethodName
	 */
	public Trace(Person loginPerson, String rootMethodName)
	{
		if (loginPerson != null)
		{
			this.operatorKind = loginPerson.getOperatorKind().byteValue();
			this.operatorId = loginPerson.getOperatorId().intValue();
		}
		else
		{
			this.operatorKind = 0; /* EnumLogOperatorKind.system */
			this.operatorId = 0;
			this.ownerGovId = 0;
		}
		
		this.actionMethod = rootMethodName;
		this.actionIP = "127.0.0.1";
		this.actionURI = "systemAutoCreate";
	}
	
	/**
	 * 上传专用
	 * 
	 * @param loginPerson
	 *            从缓存中取得的McPerson
	 */
	public Trace(HttpServletRequest req, Person loginPerson) throws LoginExpireException
	{
		if (loginPerson == null)
			throw new LoginExpireException("请登录");
		
		this.operatorKind = loginPerson.getOperatorKind().byteValue();
		this.operatorId = loginPerson.getOperatorId().intValue();
		
		this.actionMethod = "postUpload";
		this.actionIP = RequestUtils.getIpAddr(req);
		this.actionURI = RequestUtils.getUri(req);
	}
	
	/**
	 * EnumLogOperatorKind.system专用，如，自动报表
	 * 
	 * @param domainType
	 * @param domainTable
	 * @param domainKey
	 * @param actionMethod
	 * @param actionIP
	 * @param actionURI
	 * @param body
	 * @param friendlyBody
	 */
	public static Trace getSystemTrace(byte domainType, byte domainTable, String domainKey,
			String actionMethod, String actionIP, String actionURI, String body, String friendlyBody)
	{
		return new Trace(domainType, domainTable, domainKey, actionMethod, actionIP, actionURI, body, friendlyBody);
	}
	
	/**
	 * EnumLogOperatorKind.system专用
	 * 
	 * @param domainType
	 * @param domainTable
	 * @param domainKey
	 * @param actionMethod
	 * @param actionIP
	 * @param actionURI
	 * @param body
	 * @param friendlyBody
	 */
	private Trace(byte domainType, byte domainTable, String domainKey, String actionMethod, String actionIP, String actionURI, String body, String friendlyBody)
	{
		this.operatorKind = 0;// EnumLogOperatorKind.system;
		this.operatorId = 0;
		this.ownerGovId = 0;
		
		this.domainType = domainType;
		this.domainTable = domainTable;
		this.domainKey = domainKey;
		this.actionMethod = actionMethod;
		this.actionIP = actionIP;
		this.actionURI = actionURI;
		this.body = body;
		this.friendlyBody = friendlyBody;
	}
}