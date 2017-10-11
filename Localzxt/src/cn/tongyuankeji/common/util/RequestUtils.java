package cn.tongyuankeji.common.util;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.util.UrlPathHelper;

import cn.tongyuankeji.common.parameters.ConstantBase;

/**
 * HttpServletRequest帮助类
 */
public class RequestUtils
{
	static final Logger log = LogManager.getLogger(RequestUtils.class.getName());

	/**
	 * 获取QueryString的参数值，并使用URLDecoder以UTF-8格式转码。如果请求是以post方法提交的，
	 * 那么将通过HttpServletRequest#getParameter获取。
	 * 
	 * @param name
	 *            参数名称
	 */
	public static String getQueryParam(HttpServletRequest req, String name)
	{
		assert req != null : "getQueryParam(HttpServletRequest, String)参数req不能为空！";
		assert !Utils.isBlank(name) : "getQueryParam(HttpServletRequest, String)参数name不能为空！";

		String val = null;
		String[] result = null;

		if (StringUtils.isBlank(name))
			return null;

		if (req.getMethod().equalsIgnoreCase(ConstantBase.POST))
			return req.getParameter(name);

		val = req.getQueryString();
		if (StringUtils.isBlank(val))
			return null;

		try
		{
			val = URLDecoder.decode(val, ConstantBase.UTF8);
		}
		catch (UnsupportedEncodingException ex)
		{
			log.error(String.format("encoding %s not support?", ConstantBase.UTF8), ex);
		}

		result = parseQueryString(val).get(name);
		if (result != null && result.length > 0)
			return result[result.length - 1];

		return null;
	}

	/**
	 * 将req中所有参数值，放入返回的键-值对
	 */
	public static Map<String, Object> getQueryParams(HttpServletRequest req)
	{
		assert req != null : "getQueryParam(HttpServletRequest)参数req不能为空！";

		Map<String, String[]> map;
		String val = null;
		Map<String, Object> result = null;
		int leng = 0;

		if (req.getMethod().equalsIgnoreCase(ConstantBase.POST))
			map = req.getParameterMap();

		else
		{
			val = req.getQueryString();
			if (StringUtils.isBlank(val))
				return new HashMap<String, Object>();

			try
			{
				val = URLDecoder.decode(val, ConstantBase.UTF8);
			}
			catch (UnsupportedEncodingException ex)
			{
				log.error(String.format("encoding %s not support? ", ConstantBase.UTF8), ex);
			}

			map = parseQueryString(val);
		}

		result = new HashMap<String, Object>(map.size());

		for (Map.Entry<String, String[]> entry : map.entrySet())
		{
			leng = entry.getValue().length;
			if (leng == 1)
				result.put(entry.getKey(), entry.getValue()[0]);

			else if (leng > 1)
				result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

	/**
	 * Parses a query string passed from the client to the server and builds a <code>HashTable</code> object with key-value pairs. The query string
	 * should be in the form of a string packaged by the GET or POST method,
	 * that is, it should have key-value pairs in the form <i>key=value</i>,
	 * with each pair separated from the next by a &amp; character.
	 * 
	 * <p>
	 * A key can appear more than once in the query string with different values. However, the key appears only once in the hashtable, with its value being an array of strings
	 * containing the multiple values sent by the query string.
	 * 
	 * <p>
	 * The keys and values in the hashtable are stored in their decoded form, so any + characters are converted to spaces, and characters sent in hexadecimal notation (like
	 * <i>%xx</i>) are converted to ASCII characters.
	 * 
	 * @param s
	 *            a string containing the query to be parsed
	 * 
	 * @return a <code>HashTable</code> object built from the parsed key-value
	 *         pairs
	 * 
	 * @exception IllegalArgumentException
	 *                if the query string is invalid
	 * 
	 */
	public static Map<String, String[]> parseQueryString(String s)
	{
		String valArray[] = null;
		if (s == null)
		{
			throw new IllegalArgumentException();
		}
		Map<String, String[]> ht = new HashMap<String, String[]>();
		StringTokenizer st = new StringTokenizer(s, "&");
		while (st.hasMoreTokens())
		{
			String pair = (String) st.nextToken();
			int pos = pair.indexOf('=');
			if (pos == -1)
			{
				continue;
			}
			String key = pair.substring(0, pos);
			String val = pair.substring(pos + 1, pair.length());
			if (ht.containsKey(key))
			{
				String oldVals[] = (String[]) ht.get(key);
				valArray = new String[oldVals.length + 1];
				for (int i = 0; i < oldVals.length; i++)
				{
					valArray[i] = oldVals[i];
				}
				valArray[oldVals.length] = val;
			}
			else
			{
				valArray = new String[1];
				valArray[0] = val;
			}
			ht.put(key, valArray);
		}
		return ht;
	}

	public static Map<String, String> getRequestMap(HttpServletRequest request,
			String prefix)
	{
		return getRequestMap(request, prefix, false);
	}

	public static Map<String, String> getRequestMapWithPrefix(
			HttpServletRequest request, String prefix)
	{
		return getRequestMap(request, prefix, true);
	}

	private static Map<String, String> getRequestMap(
			HttpServletRequest request, String prefix, boolean nameWithPrefix)
	{
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<String> names = request.getParameterNames();
		String name, key, value;
		while (names.hasMoreElements())
		{
			name = names.nextElement();
			if (name.startsWith(prefix))
			{
				key = nameWithPrefix ? name : name.substring(prefix.length());
				value = StringUtils.join(request.getParameterValues(name), ',');
				map.put(key, value);
			}
		}
		return map;
	}

	/**
	 * 获取访问者IP
	 * 
	 * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
	 * 
	 * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，
	 * 如果还不存在则调用Request .getRemoteAddr()。
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request)
	{
		String ip = request.getHeader("X-Real-IP");
		if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip))
		{
			return ip;
		}
		ip = request.getHeader("X-Forwarded-For");
		if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip))
		{
			// 多次反向代理后会有多个IP值，第一个为真实IP。
			int index = ip.indexOf(',');
			if (index != -1)
			{
				return ip.substring(0, index);
			}
			else
			{
				return ip;
			}
		}
		else
		{
			return request.getRemoteAddr();
		}
	}

	/**
	 * 获得当的访问全路径
	 * 
	 * HttpServletRequest.getRequestURL+"?"+HttpServletRequest.getQueryString
	 * 
	 * @param request
	 * @return
	 */
	public static String getLocation(HttpServletRequest request)
	{
		UrlPathHelper helper = new UrlPathHelper();
		StringBuffer buff = request.getRequestURL();
		String uri = request.getRequestURI();
		String origUri = helper.getOriginatingRequestUri(request);
		buff.replace(buff.length() - uri.length(), buff.length(), origUri);
		String queryString = helper.getOriginatingQueryString(request);
		if (queryString != null)
		{
			buff.append("?").append(queryString);
		}
		return buff.toString();
	}

	/**
	 * 获得当前访问的RequestMapping。如 .../OESYS/getCollection.jspx，返回/getCollection.jspx
	 * 
	 * @author 代平
	 */
	public static String getUri(HttpServletRequest request)
	{
		String uri = request.getRequestURI();
		int start = uri.lastIndexOf('/');
		if (start >= 0)
			return uri.substring(start);
		return uri;
	}

	public static String getUriPattern(HttpServletRequest request)
	{
		String uri = RequestUtils.getUri(request);
		int idxDot = uri.lastIndexOf('.');
		if (idxDot < 0)
			return null;

		return uri.substring(idxDot + 1);
	}
	
	public static String getUriName(HttpServletRequest request)
	{
		String uri = RequestUtils.getUri(request);

		int idxSprit = uri.lastIndexOf('/');
		if (idxSprit < 0)
			idxSprit = 0;
		
		int idxDot = uri.lastIndexOf('.');
		if (idxDot < 0)
			idxDot = uri.length();
		return uri.substring(idxSprit + 1, idxDot);
	}
}