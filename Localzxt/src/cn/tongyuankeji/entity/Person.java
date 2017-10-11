package cn.tongyuankeji.entity;


/**
 * 可进行登录的人员 接口
 * 
 * @author 代平 2017-05-02 创建
 */
public interface Person
{
	/*
	 * 登录时读取数据库后创建登录人简略信息实例，保存在memcached（key = uuid, value = Person实例），并设置response.setAttribute("_auth_key", uuid)<br />
	 * Interceptor接收到请求时，用request.getAttribute("_auth_key")取得uuid，到memcached中取Person实例，转存HttpServletRequest.setAttribute("_login_person")<br />
	 * 之后Controller都使用HttpServletRequest.getAttribute("_login_person")
	 */
	
	/**
	 * memcached key
	 */
	public static final String AUTH_KEY = "_auth_key";

	/**
	 * request key
	 */
	public static final String LOGIN_PERSON = "_login_person";

	/**
	 * 被Trace使用
	 */
	public abstract Integer getOperatorId();

	public abstract Byte getOperatorKind();

	/**
	 * 被SchemaBase使用
	 */
	public abstract Integer getRoleId();

	public abstract String getUserName();	// 真实姓名
	
	public abstract String getThumbFile();	// 头像
	
	public abstract Integer getOwnerGovId();	// 头像

	/**
	 * 被相应的Manager和HttpSession使用
	 */
	public abstract Integer getSysId();

	public abstract String getLoginName(); // 网页上的显示名字：userName, 姓+手机号后4位
	
	public abstract String getKeyName();	// 识别名（水印）：如 姓+手机号

	/**EnumUserState*/
	public abstract Byte getState();	

	public abstract Integer[] getDataTypes();	
}
