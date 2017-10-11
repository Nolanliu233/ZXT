package cn.tongyuankeji.common.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rubyeye.xmemcached.MemcachedClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cn.tongyuankeji.entity.user.User;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;

import cn.tongyuankeji.common.exception.LoginExpireException;
import cn.tongyuankeji.common.exception.LoginFailException;
import cn.tongyuankeji.common.util.CookieUtils;
import cn.tongyuankeji.common.util.DateUtils;
import cn.tongyuankeji.common.util.RandomUtils;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.common.web.RunSettingImpl;
import cn.tongyuankeji.entity.Person;

/**
 * 缓存主入口
 * 
 * @author 代平 2017-06-01 创建
 */
@Service
@Component
@Transactional(rollbackFor = Exception.class)
public class XmcManagerImpl implements XmcManager
{
	static final Logger log = LogManager.getLogger(XmcManagerImpl.class.getName());
	
	private static MemcachedClient MC = null;
	
	/**
	 * 所有放入到缓存中的元素的cacheKey（format:Entity_SysId)<br />
	 * 因为不使用memcached的过期时间，当需要过期一个元素时，直接从CACHE_KEYS中去掉。下次的getXXX方法如果在CACHE_KEYS中找不到，总是会从数据库中取<br />
	 * 至少有：McSpecialtyList.CACHE_KEY, <br />
	 * LOGIN_BACKUSER_CACHE_KEY, LOGIN_USER_CACHE_KEY<br />
	 */
	private static Set<String> CACHE_KEYS = new HashSet<String>(6);
	
	/**
	 * 所有登录人缓存在一个HashMap<UserId, authKey>链中，每个节点是的key是person.getSysId()，value是及时生成的authKey<br />
	 * McPerson实例，用这个authKey再放入缓存中
	 */
	private final static String LOGIN_BACKUSER_CACHE_KEY = "LoginBackUsers";
	private final static String LOGIN_USER_CACHE_KEY = "LoginUsers";
	
	@Override
	public void initCache()
	{
		ApplicationContext appctx = ContextLoader.getCurrentWebApplicationContext();
		XmcManagerImpl.MC = (MemcachedClient) appctx.getBean("memcachedClient");
		
		this.initLoginList();
	}
	
	/*--------------------------- 登录、注销 ----------------------------------*/
	private void initLoginList()
	{
		try
		{
			// 多个web server时，不要冲掉前面web server放入的登录链
			
			if (XmcManagerImpl.MC.get(XmcManagerImpl.LOGIN_BACKUSER_CACHE_KEY) == null)
			{
				XmcManagerImpl.MC.set(XmcManagerImpl.LOGIN_BACKUSER_CACHE_KEY, 0, new HashMap<Integer, String>());
				XmcManagerImpl.CACHE_KEYS.add(XmcManagerImpl.LOGIN_BACKUSER_CACHE_KEY);
			}
		}
		catch (Exception ex)
		{
			log.warn(String.format("set cache failed: %s", XmcManagerImpl.LOGIN_BACKUSER_CACHE_KEY), ex);
		}
		
		try
		{
			if (XmcManagerImpl.MC.get(XmcManagerImpl.LOGIN_USER_CACHE_KEY) == null)
			{
				XmcManagerImpl.MC.set(XmcManagerImpl.LOGIN_USER_CACHE_KEY, 0, new HashMap<Integer, String>());
				XmcManagerImpl.CACHE_KEYS.add(XmcManagerImpl.LOGIN_USER_CACHE_KEY);
			}
		}
		catch (Exception ex)
		{
			log.warn(String.format("set cache failed: %s", XmcManagerImpl.LOGIN_USER_CACHE_KEY), ex);
		}
	}
	
	/**
	 * SiteUserManager或UserManager验证登录信息后，此方法将登录人缓存到xmc，并将uuid送到客户端
	 * 
	 * @param concrete
	 *            SiteUser或User实例
	 * @return 客户端唯一标识
	 */
	public static String addAuthentication(HttpServletResponse resp, Person concrete) throws LoginFailException
	{
		// 登录时读取数据库后创建登录人简略信息实例，保存在memcached（key = uuid, value = Person实例），并设置response.setAttribute("_auth_key", uuid)
		// Interceptor接收到请求时，用request.getAttribute("_auth_key")取得uuid，到memcached中取Person实例，转存HttpServletRequest.setAttribute("_login_person")
		// 之后Controller都使用HttpServletRequest.getAttribute("_login_person")
		
		HashMap<Integer, String> lstLogin = null;
		java.util.Map.Entry mapEntry = null;
		String authKey = null;
		McPerson mcp = null;
		
		try
		{
			lstLogin = XmcManagerImpl.getLoginList(concrete instanceof User);
			
			// 添加登录人到缓存前，检查缓存中是否存在相同“人”实例，如果有，应该先去掉，用新的authKey重新放
			mapEntry = XmcManagerImpl.searchLoginList(lstLogin, concrete.getSysId());
			if (mapEntry != null)
			{
				XmcManagerImpl.MC.delete((String) mapEntry.getValue());
				
				lstLogin.remove(concrete.getSysId());
			}
			
			// 添加到缓存
			authKey = RandomUtils.get32UUID();
			mcp = new McPerson(concrete);
			
			// 不使用xmc的过期时间，McPerson内部记录这个时刻
			XmcManagerImpl.MC.set(authKey, 0, mcp);
			
			lstLogin.put(mcp.getSysId(), authKey);
			XmcManagerImpl.MC.replaceWithNoReply((concrete instanceof User) ? XmcManagerImpl.LOGIN_USER_CACHE_KEY : XmcManagerImpl.LOGIN_BACKUSER_CACHE_KEY, 0, lstLogin);
		}
		catch (Exception ex)
		{
			throw new LoginFailException(String.format("登录失败：%s", ex.getMessage()));
		}
		
		if (resp != null) // 远程流媒体管理工具 登录时，没有response
			CookieUtils.addCookie(resp, Person.AUTH_KEY, authKey, 60 * 60 * 24 * 365);
		
		return null;
	}
	
	/**
	 * 修改后台用户、用户资料后，更新缓存中对象的属性：fullName, displayName, thumbFilename
	 * 
	 * @param concrete
	 *            SiteUser或User实例
	 */
	public static void updateAuthentication(Person concrete)
	{
		HashMap<Integer, String> lstLogin = null;
		java.util.Map.Entry mapEntry = null;
		McPerson mcp = null;
		
		try
		{
			lstLogin = XmcManagerImpl.getLoginList(concrete instanceof User);
			
			mapEntry = XmcManagerImpl.searchLoginList(lstLogin, concrete.getSysId());
			if (mapEntry == null)
				return;
			
			mcp = XmcManagerImpl.MC.get((String) mapEntry.getValue());
			if (mcp != null)
			{
				mcp.update(concrete);
				XmcManagerImpl.MC.replaceWithNoReply((String) mapEntry.getValue(), 0, mcp);
			}
			else
			{
				lstLogin.remove(mapEntry.getKey());
				XmcManagerImpl.MC.replaceWithNoReply(concrete instanceof User ? XmcManagerImpl.LOGIN_USER_CACHE_KEY : XmcManagerImpl.LOGIN_BACKUSER_CACHE_KEY, 0, lstLogin);
			}
		}
		catch (Exception ex)
		{
			log.warn(String.format("获取 登录人 缓存失败: %d", concrete.getSysId()), ex);
		}
	}
	
	/**
	 * 注销时，清空登录人
	 */
	public static void removeAuthentication(HttpServletRequest req, HttpServletResponse resp, boolean isUser)
	{
		String authKey = XmcManagerImpl.getAuthKey(req);
		HashMap<Integer, String> lstLogin = null;
		java.util.Map.Entry mapEntry = null;
		
		if (Utils.isBlank(authKey))
			return;
		
		CookieUtils.deleteCookie(req, resp, Person.AUTH_KEY);
		
		try
		{
			XmcManagerImpl.MC.delete(authKey);
			
			lstLogin = XmcManagerImpl.getLoginList(isUser);
			
			mapEntry = XmcManagerImpl.searchLoginList(lstLogin, authKey);
			if (mapEntry == null)
				return;
			
			lstLogin.remove(mapEntry.getKey());
			XmcManagerImpl.MC.replaceWithNoReply(isUser ? XmcManagerImpl.LOGIN_USER_CACHE_KEY : XmcManagerImpl.LOGIN_BACKUSER_CACHE_KEY, 0, lstLogin);
		}
		catch (Exception ex)
		{
			log.warn(String.format("清除 登录人 缓存失败: %s", authKey), ex);
		}
	}
	
	/**
	 * 某些操作使已登录的concrete失效，从LOGIN_BACKUSER_CACHE_KEY或LOGIN_USER_CACHE_KEY中去掉，强制那个McPerson重新登录（注意，不是把当前登录人踢出）
	 */
	public static boolean tryRemoveAuthentication(Person concrete)
	{
		return XmcManagerImpl.tryRemoveAuthentication(concrete.getSysId(), concrete instanceof User);
	}
	
	public static boolean tryRemoveAuthentication(int personId, boolean isUser)
	{
		HashMap<Integer, String> lstLogin = null;
		java.util.Map.Entry mapEntry = null;
		
		try
		{
			lstLogin = XmcManagerImpl.getLoginList(isUser);
			
			mapEntry = XmcManagerImpl.searchLoginList(lstLogin, personId);
			if (mapEntry == null)
				return true;
			
			XmcManagerImpl.MC.delete((String) mapEntry.getValue());
			
			lstLogin.remove(mapEntry.getKey());
			XmcManagerImpl.MC.replaceWithNoReply(isUser ? XmcManagerImpl.LOGIN_USER_CACHE_KEY : XmcManagerImpl.LOGIN_BACKUSER_CACHE_KEY, 0, lstLogin);
			
			return true;
		}
		catch (Exception ex)
		{
			log.warn(String.format("强退 登录人 缓存失败：%d", personId), ex);
			return false;
		}
	}
	
	private static HashMap<Integer, String> getLoginList(boolean isUser) throws Exception
	{
		return XmcManagerImpl.MC.get(isUser ? XmcManagerImpl.LOGIN_USER_CACHE_KEY : XmcManagerImpl.LOGIN_BACKUSER_CACHE_KEY);
	}
	
	private static java.util.Map.Entry searchLoginList(HashMap<Integer, String> lstLogin, int personId)
	{
		Iterator itr = null;
		java.util.Map.Entry mapEntry = null;
		
		if (lstLogin == null)
			return null;
		
		try
		{
			itr = lstLogin.entrySet().iterator();
			while (itr.hasNext())
			{
				mapEntry = (java.util.Map.Entry) itr.next();
				if (mapEntry.getKey().equals(personId))
					return mapEntry;
			}
		}
		catch (Exception ex)
		{
		}
		
		return null;
	}
	
	private static java.util.Map.Entry searchLoginList(HashMap<Integer, String> lstLogin, String authKey)
	{
		Iterator itr = null;
		java.util.Map.Entry mapEntry = null;
		
		if (lstLogin == null || Utils.isBlank(authKey))
			return null;
		
		try
		{
			itr = lstLogin.entrySet().iterator();
			while (itr.hasNext())
			{
				mapEntry = (java.util.Map.Entry) itr.next();
				if (authKey.equals((String) mapEntry.getValue()))
					return mapEntry;
			}
		}
		catch (Exception ex)
		{
		}
		
		return null;
	}
	
	/**
	 * 用Request中的Person.AUTH_KEY在xmc中找登录人。<br />
	 * 未超时的，刷新ping时间后，放回xmc，并返回登录人给调用方法<br />
	 * 已超时的，从xmc删除登录人，返回null<br />
	 * 一般方法不直接调用，只被interceptor调用
	 * 
	 * @return McPerson
	 * @exception LoginExpireException
	 *                长时间无操作时
	 */
	public static McPerson refreshByAuthKey(HttpServletRequest req, HttpServletResponse resp, boolean isUser)
	{
		String authKey = XmcManagerImpl.getAuthKey(req);
		HashMap<Integer, String> lstLogin = null;
		java.util.Map.Entry mapEntry = null;
		McPerson mcp = null;
		
		try
		{
			lstLogin = XmcManagerImpl.getLoginList(isUser);
			
			mapEntry = XmcManagerImpl.searchLoginList(lstLogin, authKey);
			if (mapEntry == null)
			{
				CookieUtils.deleteCookie(req, resp, Person.AUTH_KEY);
				return null;
			}
			
			mcp = XmcManagerImpl.MC.get((String) mapEntry.getValue());
			if (mcp == null)
			{
				CookieUtils.deleteCookie(req, resp, Person.AUTH_KEY);
				
				lstLogin.remove(mapEntry.getKey());
				XmcManagerImpl.MC.replaceWithNoReply(isUser ? XmcManagerImpl.LOGIN_USER_CACHE_KEY : XmcManagerImpl.LOGIN_BACKUSER_CACHE_KEY, 0, lstLogin);
				return null;
			}
			
			if (DateUtils.minutesDiff(mcp.getLastPingAt(), DateUtils.now()) > RunSettingImpl.getSESSION_EXPIRE_MINUTE())
			{
				CookieUtils.deleteCookie(req, resp, Person.AUTH_KEY);
				
				lstLogin.remove(mapEntry.getKey());
				XmcManagerImpl.MC.replaceWithNoReply(isUser ? XmcManagerImpl.LOGIN_USER_CACHE_KEY : XmcManagerImpl.LOGIN_BACKUSER_CACHE_KEY, 0, lstLogin);
				
				XmcManagerImpl.MC.delete((String) mapEntry.getValue());
				return null;
			}
			
			if (mcp.refreshPing())
			{
				XmcManagerImpl.MC.replaceWithNoReply((String) mapEntry.getValue(), 0, mcp);
			}
			
			return mcp;
		}
		catch (Exception ex)
		{
			log.warn(String.format("替换 登录人 缓存失败: %s", authKey), ex);
			return null;
		}
	}
	
	/**
	 * 用Request中的Person.AUTH_KEY在xmc中找登录人。
	 * 
	 * @return McPerson
	 */
	public static McPerson getByAuthKey(HttpServletRequest req)
	{
		return XmcManagerImpl.getByAuthKey(XmcManagerImpl.getAuthKey(req));
	}
	
	/**
	 * 类似上一个方法，专门为flash player和手机端直接用@OEActionArg传参数的清空
	 */
	public static McPerson getByAuthKey(String authKey) // 这个方法允许从action调用
	{
		if (!Utils.isBlank(authKey))
		{
			try
			{
				return XmcManagerImpl.MC.get(authKey);
			}
			catch (Exception ex)
			{
				log.warn(String.format("获取 登录人 缓存失败: %s", authKey), ex);
			}
		}
		
		return null;
	}
	
	/**
	 * 有些HTM页面，无法传authKey进来，传的是UserId，尝试在登录链中找对应的用户实例
	 */
	public static McPerson getByUserId(int studentId)
	{
		HashMap<Integer, String> lstLogin = null;
		java.util.Map.Entry mapEntry = null;
		
		try
		{
			lstLogin = XmcManagerImpl.getLoginList(true);
			
			mapEntry = XmcManagerImpl.searchLoginList(lstLogin, studentId);
			if (mapEntry != null)
				return XmcManagerImpl.getByAuthKey((String) mapEntry.getValue());
		}
		catch (Exception ex)
		{
		}
		
		return null;
	}
	
	/**
	 * 尝试获得req中的登录标识
	 */
	public static String getAuthKey(HttpServletRequest req)
	{
		return CookieUtils.getCookieValue(req, Person.AUTH_KEY);
	}
	
	/*--------------------------- private Helper ----------------------------------*/
	private static Object getCache(String key)
	{
		try
		{
			if (XmcManagerImpl.CACHE_KEYS.contains(key))
				return XmcManagerImpl.MC.get(key);
		}
		catch (Exception ex)
		{
			log.warn(String.format("获取 %s 缓存数据失败：%s", key, ex));
		}
		
		return null;
	}
	
	/**
	 * 添加/替换xmc中缓存实例，并更新 CACHE_KEYS中 对应 带SysId的key。key 如：Course_32<br />
	 * 在启动装载缓存时，或获取单条数据时，被调用。无论此方法是否成功将实例保存到xmc，获取单条数据的还是可以将McBase实例返回给service manager使用
	 * 
	 * @param mcentry
	 *            缓存的对象
	 */
	synchronized static boolean setCache(McBase mcentry)
	{
		assert mcentry != null : "setCache(McBase)参数mcentry不能为空！";
		assert !Utils.isBlank(mcentry.getCacheKey()) : "setCache(McBase)的mcentry.getCacheKey()返回空！";
		
		try
		{
			XmcManagerImpl.MC.set(mcentry.getCacheKey(), 0, mcentry);
			
			// 要先保证保存到xmc成功，才能放入set
			XmcManagerImpl.CACHE_KEYS.add(mcentry.getCacheKey());
			
			log.debug("set cache: {}", mcentry.getCacheKey());
		}
		catch (Exception ex)
		{
			log.warn(String.format("set cache failed: %s", mcentry.getCacheKey()), ex);
			return false;
		}
		
		return true;
	}
	
	/**
	 * 添加/替换xmc中缓存实例，并更新CACHE_KEYS 对应 不带SysId的key。key 如：SpecialtyList
	 * 
	 * @param versionKey
	 *            取版本号的key。如：SpecialtyListVersion
	 * @param cacheKeyPrefix
	 *            取对象的key前缀。方法内部会追加版本号。如：SpecialtyList:verX
	 * @param caching
	 *            缓存的对象
	 */
	synchronized static boolean setCache(String cacheKey, Object mcentry)
	{
		assert !Utils.isBlank(cacheKey) : "setCache(String, Object)参数cacheKey不能为空！";
		assert mcentry != null : "setCache(String, Object)参数mcentry不能为空！";
		
		try
		{
			XmcManagerImpl.MC.set(cacheKey, 0, mcentry);
			
			// 要先保证保存到xmc成功，才能放入set
			XmcManagerImpl.CACHE_KEYS.add(cacheKey);
			
			log.debug("set cache: {}", cacheKey);
		}
		catch (Exception ex)
		{
			log.warn(String.format("set cache failed: %s", cacheKey), ex);
			return false;
		}
		
		return true;
	}
	
	private static void replaceCache(String key, Object value) throws Exception
	{
		assert XmcManagerImpl.CACHE_KEYS.contains(key);
		
		XmcManagerImpl.MC.replaceWithNoReply(key, 0, value);
	}
}
