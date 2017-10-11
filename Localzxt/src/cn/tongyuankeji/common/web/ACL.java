package cn.tongyuankeji.common.web;


import java.io.File;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import cn.tongyuankeji.common.annotation.ACLMapping;
import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.common.util.XmlUtils;
import cn.tongyuankeji.entity.Person;

/**
 * Role 和 权限 的Map。网站启动时加载
 * 
 * @author 代平 2017-05-09 创建
 */
public final class ACL
{
	static final Logger log = LogManager.getLogger(ACL.class.getName());
	
	/**
	 * key - Role.sysId, value - permission xml doc
	 */
	protected static Map<Integer, Document> ACL_LIST = new HashMap<Integer, Document>();
	
	/**
	 * 将母版permission.xml裁剪为对应Role.ACL中，保留acl中哪些opr节点
	 * 
	 * @param aclMaskFilename
	 *            权限文件的母版文件名。如，permission.xml
	 * @param acl
	 *            将被应用的权限字符串，一般从数据库取得
	 */
	public static Document loadPermission(String aclMaskFilename, String acl, DocumentBuilder dBuilder, XPath xpath)
	{
		Document xdocMask = null, xdoc = null;
		
		assert !Utils.isBlank(aclMaskFilename) : "loadPermission(String, String, DocumentBuilder , XPath) 参数aclMaskFilename不能为空";
		assert !Utils.isBlank(acl) : "loadPermission(String, String, DocumentBuilder , XPath) 参数acl不能为空";
		assert dBuilder != null : "loadPermission(String, String, DocumentBuilder , XPath) 参数dBuilder不能为空";
		assert xpath != null : "loadPermission(String, String, DocumentBuilder , XPath) 参数xpath不能为空";
		
		try
		{
			xdocMask = dBuilder.parse(new File(aclMaskFilename));
		}
		catch (Exception ex)
		{
			log.error("Unable to read ACL file!", ex);
			return null;
		}
		
		try
		{
			xdoc = dBuilder.parse(new InputSource(new StringReader(acl)));
		}
		catch (Exception ex)
		{
			log.error("Unable to parse Role.ACL!", ex);
			return null;
		}
		
		NodeList lstuser = null, lstsys = null, lstmod = null, lstpage = null, lstopr = null;
		Element xsys = null, xmod = null, xpage = null, xopr = null;
		String ssys = null, smod = null, spage = null, sopr = null;
		
		// 子系统
		lstsys = xdocMask.getDocumentElement().getElementsByTagName(ACLMapping.NODE_SUBSYSTEM);
		
		// mask中是否有这个子系统？
		for (int i = lstsys.getLength() - 1; i >= 0; i--)
		{
			xsys = (Element) lstsys.item(i);
			ssys = xsys.getAttribute(ACLMapping.ATTR_NAME);
			
			try
			{
				lstuser = (NodeList) xpath.evaluate(
						String.format("%s[@%s='%s']",
								ACLMapping.NODE_SUBSYSTEM, ACLMapping.ATTR_NAME, ssys),
						xdoc.getDocumentElement(), XPathConstants.NODESET);
			}
			catch (Exception ex)
			{
				log.error("No such node in master ACL: {}", ssys);
				xdocMask.getDocumentElement().removeChild(xsys);
				continue;
			}
			if (lstuser.getLength() == 0)
			{
				xdocMask.getDocumentElement().removeChild(xsys);
				continue;
			}
			
			// mask中是否有这个模块？
			lstmod = xsys.getElementsByTagName(ACLMapping.NODE_MODULE);
			for (int j = lstmod.getLength() - 1; j >= 0; j--)
			{
				xmod = (Element) lstmod.item(j);
				smod = xmod.getAttribute(ACLMapping.ATTR_NAME);
				
				try
				{
					lstuser = (NodeList) xpath.evaluate(
							String.format("%s[@%s='%s']/%s[@%s='%s']",
									ACLMapping.NODE_SUBSYSTEM, ACLMapping.ATTR_NAME, ssys,
									ACLMapping.NODE_MODULE, ACLMapping.ATTR_NAME, smod),
							xdoc.getDocumentElement(), XPathConstants.NODESET);
				}
				catch (Exception ex)
				{
					log.error("No such node in master ACL: {}", smod);
					xsys.removeChild(xmod);
					continue;
				}
				if (lstuser.getLength() == 0)
				{
					xsys.removeChild(xmod);
					continue;
				}
				
				// mask中是否有这个page？
				lstpage = xmod.getElementsByTagName(ACLMapping.NODE_PAGE);
				for (int k = lstpage.getLength() - 1; k >= 0; k--)
				{
					xpage = (Element) lstpage.item(k);
					spage = xpage.getAttribute(ACLMapping.ATTR_NAME);
					
					try
					{
						lstuser = (NodeList) xpath.evaluate(
								String.format("%s[@%s='%s']/%s[@%s='%s']/%s[@%s='%s']",
										ACLMapping.NODE_SUBSYSTEM, ACLMapping.ATTR_NAME, ssys,
										ACLMapping.NODE_MODULE, ACLMapping.ATTR_NAME, smod,
										ACLMapping.NODE_PAGE, ACLMapping.ATTR_NAME, spage),
								xdoc.getDocumentElement(), XPathConstants.NODESET);
					}
					catch (Exception ex)
					{
						log.error("No such node in master ACL: {}", spage);
						xmod.removeChild(xpage);
						continue;
					}
					if (lstuser.getLength() == 0)
					{
						xmod.removeChild(xpage);
						continue;
					}
					
					// mask中是否有这种类型的操作？
					lstopr = xpage.getElementsByTagName(ACLMapping.NODE_OPR);
					for (int m = lstopr.getLength() - 1; m >= 0; m--)
					{
						xopr = (Element) lstopr.item(m);
						sopr = xopr.getAttribute(ACLMapping.ATTR_NAME);
						
						try
						{
							lstuser = (NodeList) xpath.evaluate(
									String.format("%s[@%s='%s']/%s[@%s='%s']/%s[@%s='%s']/%s[@%s='%s']",
											ACLMapping.NODE_SUBSYSTEM, ACLMapping.ATTR_NAME, ssys,
											ACLMapping.NODE_MODULE, ACLMapping.ATTR_NAME, smod,
											ACLMapping.NODE_PAGE, ACLMapping.ATTR_NAME, spage,
											ACLMapping.NODE_OPR, ACLMapping.ATTR_NAME, sopr),
									xdoc.getDocumentElement(), XPathConstants.NODESET);
						}
						catch (Exception ex)
						{
							log.error("No such node in master ACL: {}", sopr);
							xpage.removeChild(xopr);
						}
						if (lstuser.getLength() == 0)
							xpage.removeChild(xopr);
						
					} // foreach opr
					
				} // foreach page
				
			} // foreach module
			
		} // foreach subsystem
		
		return xdocMask;
	}
	
	/**
	 * Add into or replace map entry
	 * 
	 * @param key
	 *            Role.SysId
	 * @param docACL
	 *            ACL XML document
	 */
	public static void addToMap(int roleId, Document docACL)
	{
		assert roleId > 0 : "addToMap(int, byte, Document) 参数roleId应大于0！";
		assert docACL != null : "addToMap(int, byte, Document) 参数docACL不能为空";
		
		ACL.ACL_LIST.put(new Integer(roleId), docACL);
	}
	
	public static void removeFromMap(int roleId)
	{
		assert roleId > 0 : "removeFromMap(int) 参数roleId应大于0！";
		
		ACL.ACL_LIST.remove(new Integer(roleId));
	}
	
	public static void clearMap()
	{
		ACL.ACL_LIST.clear();
	}
	
	/**
	 * 权限Map中是否有此角色
	 * 
	 * @param roleId
	 *            角色Role.SysId
	 */
	public static boolean hasRole(int roleId)
	{
		return ACL.ACL_LIST.containsKey(new Integer(roleId));
	}
	
	/**
	 * 检查roleId对应的角色是否具有执行clsCallingObj.actionMethodName的权限。 --- 本class内部调用时，isMappingAct = false ---
	 * 某些manager中调用时，isMappingAct = false --- RequestMapping.java中调用，isMappingAct = true --- --- RequestMapping(value
	 * = "/getAllSpecialty.ofc", method = RequestMethod.GET) --- --- ACLMapping(subsystem = "backoffice", module =
	 * "dictionary", page = "specialty", opr = "edit")
	 * 
	 * @param roleId
	 *            被检查的角色Role.SysId。
	 * @param clsCallingObj
	 *            被检查的Action类或RequestMapping类。
	 * @param actionMethodName
	 *            被调用的方法
	 * @param isMappingAct
	 *            true - 从RequestMapping调用
	 * @return 没有权限时，返回错误提示。允许操作的，返回null
	 */
	public static String checkACL(Person loginPerson, Class<?> clsCallingObj, String actionMethodName, boolean isMappingAct)
	{
		Method m = null;
		
		assert clsCallingObj != null : "checkACL(int, Class<?>, String, boolean) 参数clsCallingObj不能为空";
		assert !Utils.isBlank(actionMethodName) : "checkACL(int, Class<?>, String, boolean) 参数actionMethodName不能为空";
		
		Document xdoc = ACL.ACL_LIST.get(loginPerson.getRoleId());
		if (xdoc == null)
			return "您没有登录此系统的权限！";
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		try
		{
			if (isMappingAct)
				m = clsCallingObj.getMethod(actionMethodName, HttpServletRequest.class, HttpServletResponse.class, ModelMap.class);
			else
				m = clsCallingObj.getMethod(actionMethodName, HttpServletRequest.class, HttpServletResponse.class);
		}
		catch (NoSuchMethodException ex)
		{
			return "权限不足(1)！";
		}
		
		if (!m.isAnnotationPresent(ACLMapping.class))
			return "权限不足(2)！";
		
		ACLMapping ann = m.getAnnotation(ACLMapping.class);
		NodeList lstth = null; // Opr下面具体的方法
		try
		{
			lstth = (NodeList) xpath.evaluate(
					String.format("%s[@%s='%s']/%s[@%s='%s']/%s[@%s='%s']/%s[@%s='%s']/%s",
							ACLMapping.NODE_SUBSYSTEM, ACLMapping.ATTR_NAME, ann.subsystem(),
							ACLMapping.NODE_MODULE, ACLMapping.ATTR_NAME, ann.module(),
							ACLMapping.NODE_PAGE, ACLMapping.ATTR_NAME, ann.page(),
							ACLMapping.NODE_OPR, ACLMapping.ATTR_NAME, ann.opr(),
							actionMethodName),
					xdoc.getDocumentElement(), XPathConstants.NODESET);
		}
		catch (XPathExpressionException ex)
		{
			return "权限不足(3)！";
		}
		
		// 4. 用户角色是否能执行这个action
		if (lstth.getLength() == 0)
			return "权限不足(4)！";
		
		String platform = ((Element) lstth.item(0).getParentNode()).getAttribute(ACLMapping.ATTR_PLATFORM);
		if (Utils.isBlank(platform))
			return null;
		
		if (!platform.equals("1"))
			return null;
		
		// 只有默认园区才能操作的
		if (!ConstantBase.DEFAULT_OWNER_GOV_ID.equals(loginPerson.getOwnerGovId()))
			return "权限不足(5)";
		
		// 有权限
		return null;
	}
	
	/**
	 * 根据登录人的角色权限，生成网页用菜单JSON
	 * 
	 * @param roleId
	 *            登录人roleId
	 * @param userGovId
	 *            登录用户的园区ID
	 * @param subsystem
	 *            登录的是哪个子系统
	 */
	public static JSONObject role2Menu(int roleId, int userGovId, String subsystem) throws Exception
	{
		JSONObject result = new JSONObject(), omod = null, opage = null;
		JSONArray asys = null, amod = null;
		NodeList lstmod = null, lstpage = null;
		Element xsys = null, xmod = null, xpage = null;
		String link = null, icon = null, platform = null;
		boolean hasPage = false; // 当前module下至少有一个page whose showMenu=true & link != empty
		
		assert !Utils.isBlank(subsystem) : "role2Menu(int, String) 参数subsystem不能为空";
		
		Document xdoc = ACL.ACL_LIST.get(roleId);
		if (xdoc == null)
			return result;
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList lstsys = (NodeList) xpath.evaluate(
				String.format("%s[@%s='%s']",
						ACLMapping.NODE_SUBSYSTEM, ACLMapping.ATTR_NAME, subsystem),
				xdoc.getDocumentElement(), XPathConstants.NODESET);
		
		assert lstsys.getLength() > 0 : "子系统 " + subsystem + " 不存在！";
		
		xsys = (Element) lstsys.item(0);
		lstmod = xsys.getElementsByTagName(ACLMapping.NODE_MODULE);
		asys = new JSONArray();
		
		for (int j = 0; j < lstmod.getLength(); j++)
		{
			xmod = (Element) lstmod.item(j);
			lstpage = xmod.getElementsByTagName(ACLMapping.NODE_PAGE);
			
			omod = new JSONObject();
			
			hasPage = false;
			
			// <module name="dictionary" --> "id":"dictionary","header":"数据字典"
			omod.put("id", xmod.getAttribute(ACLMapping.ATTR_NAME));
			omod.put("header", xmod.getAttribute(ACLMapping.ATTR_HEADER));
			
			amod = new JSONArray();
			for (int k = 0; k < lstpage.getLength(); k++)
			{
				xpage = (Element) lstpage.item(k);
				
				link = xpage.getAttribute(ACLMapping.ATTR_LINK);
				icon = xpage.getAttribute(ACLMapping.ATTR_ICON);
				platform = xpage.getAttribute(ACLMapping.ATTR_PLATFORM);
				
				opage = new JSONObject();
				
				// <page name="specialty" --> "id":"specialty","header":"专业","link":"/specialty.htm"
				opage.put("id", xpage.getAttribute(ACLMapping.ATTR_NAME));
				opage.put("header", xpage.getAttribute(ACLMapping.ATTR_HEADER));
				
				if (Utils.isBlank(icon))
					opage.put("icon", JSONNull.getInstance());
				else
					opage.put("icon", icon);
				
				if (Utils.isBlank(link))
					opage.put("link", JSONNull.getInstance());
				else
					opage.put("link", RunSettingImpl.getCODE_ROOT() + ConstantBase.FILE_SEP + link);
				
				// 只有当page下有opr节点时，才显示page菜单
				// 非默认园区用户，排除platform="1"的page
				if (XmlUtils.getElementChild(xpage).isEmpty())
					opage.put("showMenu", false);
				else if (!ConstantBase.DEFAULT_OWNER_GOV_ID.equals(userGovId) && platform.equals("1"))
					opage.put("showMenu", false);
				else
				{
					opage.put("showMenu", true);
					
					if (!hasPage && !Utils.isBlank(link))
						hasPage = true;
				}
				
				amod.add(opage);
				
			} // foreach module
			
			omod.put("menu", amod);
			omod.put("showMenu", hasPage); // 只有当module下有合法的page时，才显示module菜单
			asys.add(omod);
			
		} // foreach subsystem
		
		result.put("menu", asys);
		
		return result;
	}
	
	/**
	 * 用permission文件，将数据库取得的ACL字段转前台勾选权限的JSON。也可使用在新建角色，输出所有可勾选的权限
	 * 
	 * @param aclMaskFilename
	 *            权限母版XML doc
	 * @param xdocRole
	 *            被编辑的Role.ACL字段文字XML doc。新建时，传入null
	 * @param isBird
	 *            是否用在默认园区用户上的角色。false时，屏蔽platform=1的节点
	 */
	public static JSONArray ACL2Json(Document xdocMask, Document xdocRole, boolean isBird, boolean isSystemAutoCreate)
	{
		JSONObject osys = null, omod = null, opage = null, oopr = null;
		JSONArray aacl = new JSONArray(), asys = null, amod = null, apage = null;
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList lstsys = null, lstmod = null, lstpage = null, lstopr = null;
		Element xsys = null, xmod = null, xpage = null, xopr = null;
		boolean ischecked = false;
		String platform = null;
		
		assert xdocMask != null : "ACL2Json(Document, Document) 参数xdocMask不能为空";
		
		lstsys = xdocMask.getDocumentElement().getElementsByTagName(ACLMapping.NODE_SUBSYSTEM);
		
		for (int i = 0; i < lstsys.getLength(); i++)
		{
			xsys = (Element) lstsys.item(i);
			lstmod = xsys.getElementsByTagName(ACLMapping.NODE_MODULE);
			
			osys = new JSONObject();
			
			// <subsystem name="backoffice" --> "id":"backoffice","header":"后台管理"
			osys.put("id", xsys.getAttribute(ACLMapping.ATTR_NAME));
			osys.put("header", xsys.getAttribute(ACLMapping.ATTR_HEADER));
			
			// "nodes": [...]
			asys = new JSONArray();
			for (int j = 0; j < lstmod.getLength(); j++)
			{
				xmod = (Element) lstmod.item(j);
				lstpage = xmod.getElementsByTagName(ACLMapping.NODE_PAGE);
				
				omod = new JSONObject();
				
				// <module name="dictionary" --> "id":"dictionary","header":"数据字典"
				omod.put("id", xmod.getAttribute(ACLMapping.ATTR_NAME));
				omod.put("header", xmod.getAttribute(ACLMapping.ATTR_HEADER));
				
				// "nodes": [...]
				amod = new JSONArray();
				for (int k = 0; k < lstpage.getLength(); k++)
				{
					xpage = (Element) lstpage.item(k);
					platform = xpage.getAttribute(ACLMapping.ATTR_PLATFORM);
					
					// 非默认园区用户的角色，屏蔽platform=1的节点
					if (!isBird && platform.equals("1"))
						continue;
					
					lstopr = xpage.getElementsByTagName(ACLMapping.NODE_OPR);
					
					opage = new JSONObject();
					
					// <page name="specialty" --> "id":"specialty","header":"专业"
					opage.put("id", xpage.getAttribute(ACLMapping.ATTR_NAME));
					opage.put("header", xpage.getAttribute(ACLMapping.ATTR_HEADER));
					
					// "nodes": [...]
					apage = new JSONArray();
					for (int m = 0; m < lstopr.getLength(); m++)
					{
						xopr = (Element) lstopr.item(m);
						
						ischecked = false;
						oopr = new JSONObject();
						
						// <getAllSpecialty name="view" --> "id":"getAllSpecialty","header":"查看专业","ischecked":1
						oopr.put("id", xopr.getAttribute(ACLMapping.ATTR_NAME));
						oopr.put("header", xopr.getAttribute(ACLMapping.ATTR_HEADER));
						
						try
						{
							if(isSystemAutoCreate)
								ischecked = true;
							else 
								ischecked = (xdocRole != null
									&& ((NodeList) xpath.evaluate(
									String.format("%s[@%s='%s']/%s[@%s='%s']/%s[@%s='%s']/%s[@%s='%s']",
											ACLMapping.NODE_SUBSYSTEM, ACLMapping.ATTR_NAME, xsys.getAttribute(ACLMapping.ATTR_NAME),
											ACLMapping.NODE_MODULE, ACLMapping.ATTR_NAME, xmod.getAttribute(ACLMapping.ATTR_NAME),
											ACLMapping.NODE_PAGE, ACLMapping.ATTR_NAME, xpage.getAttribute(ACLMapping.ATTR_NAME),
											ACLMapping.NODE_OPR, ACLMapping.ATTR_NAME, xopr.getAttribute(ACLMapping.ATTR_NAME)),
									xdocRole.getDocumentElement(), XPathConstants.NODESET)).getLength() > 0);
						}
						catch (Exception ex)
						{
						}
						
						oopr.put(ACLMapping.ATTR_IS_CHECKED, ischecked);
						apage.add(oopr);
						
					} // foreach opr
					opage.put("nodes", apage);
					amod.add(opage);
					
				} // foreach page
				omod.put("nodes", amod);
				asys.add(omod);
			} // foreach module
			osys.put("nodes", asys);
			aacl.add(osys);
		} // foreach subsystem
		
		return aacl;
	}
	
	/**
	 * 界面编辑保存时，将JSON包转换成XML，用于保存到数据库ACL字段
	 * 
	 * @param xdocMask
	 *            权限母版XML document
	 * 
	 * @param jsonACL
	 *            被应用的客户端勾选权限json包
	 */
	public static String json2ACL(Document xdocMask, String jsonACL)
	{
		StringBuffer sb = new StringBuffer();
		JSONArray asys = JSONArray.fromObject(jsonACL);
		NodeList lstsys = null, lstmod = null, lstpage = null, lstopr = null;
		Element xsys = null, xmod = null, xpage = null, xopr = null;
		
		JSONArray amod = null, apage = null, aopr = null;
		JSONObject oth = null;
		
		assert xdocMask != null : "json2ACL(Document, String) 参数xdocMask不能为空";
		assert !Utils.isBlank(jsonACL) : "json2ACL(Document, String) 参数jsonACL不能为空";
		
		// apply json checked to xmldocument
		lstsys = xdocMask.getDocumentElement().getElementsByTagName(ACLMapping.NODE_SUBSYSTEM);
		for (int i = lstsys.getLength() - 1; i >= 0; i--)
		{
			xsys = (Element) lstsys.item(i);
			if ((amod = findSubsystemChildNodes(xsys, asys)) == null || amod.size() == 0)
			{
				xdocMask.getDocumentElement().removeChild(xsys);
				continue;
			}
			
			sb.append(xsys.toString());
			lstmod = xsys.getElementsByTagName(ACLMapping.NODE_MODULE);
			for (int j = lstmod.getLength() - 1; j >= 0; j--)
			{
				xmod = (Element) lstmod.item(j);
				
				if ((apage = findModuleChildNodes(xmod, amod)) == null || apage.size() == 0)
				{
					xsys.removeChild(xmod);
					continue;
				}
				
				sb.append(xmod.toString());
				lstpage = xmod.getElementsByTagName(ACLMapping.NODE_PAGE);
				for (int k = lstpage.getLength() - 1; k >= 0; k--)
				{
					xpage = (Element) lstpage.item(k);
					if ((aopr = findPageChildNodes(xpage, apage)) == null || aopr.size() == 0)
					{
						xmod.removeChild(xpage);
						continue;
					}
					
					sb.append(xpage.toString());
					lstopr = xpage.getElementsByTagName(ACLMapping.NODE_OPR);
					for (int q = lstopr.getLength() - 1; q >= 0; q--)
					{
						xopr = (Element) lstopr.item(q);
						if ((oth = findOpr(xopr, aopr)) == null || !oth.getBoolean(ACLMapping.ATTR_IS_CHECKED))
						{
							xpage.removeChild(xopr);
							continue;
						}
						
						// 这时，剩下的xopr是要保留的。但是保存时，我们不需要下面的method节点
						while (xopr.getChildNodes().getLength() > 0)
							xopr.removeChild(xopr.getLastChild());
					} // foreach opr
					
				} // foreach page
				
			} // foreach module
			
		} // foreach subsystem
		
		return XmlUtils.xdoc2String(xdocMask, new String[] { ACLMapping.ATTR_NAME }, true, null);
	}
	
	/**
	 * 界面编辑保存时，将JSON包转换成XML，用于保存到数据库ACL字段
	 * 
	 * @param xdocMask
	 *            权限母版XML document
	 * 
	 * @param jsonACL
	 *            被应用的客户端勾选权限json包
	 */
	public static String getMasterACL(Document xdocMask)
	{
		StringBuffer sb = new StringBuffer();
		NodeList lstsys = null, lstmod = null, lstpage = null, lstopr = null;
		Element xsys = null, xmod = null, xpage = null, xopr = null;
		
		JSONArray amod = null, apage = null, aopr = null;
		JSONObject oth = null;
		
		assert xdocMask != null : "getMasterACL(Document) 参数xdocMask不能为空";
		
		// apply json checked to xmldocument
		lstsys = xdocMask.getDocumentElement().getElementsByTagName(ACLMapping.NODE_SUBSYSTEM);
		for (int i = lstsys.getLength() - 1; i >= 0; i--)
		{
			xsys = (Element) lstsys.item(i);
			sb.append(xsys.toString());
			lstmod = xsys.getElementsByTagName(ACLMapping.NODE_MODULE);
			for (int j = lstmod.getLength() - 1; j >= 0; j--)
			{
				xmod = (Element) lstmod.item(j);
				
				if ((apage = findModuleChildNodes(xmod, amod)) == null || apage.size() == 0)
				{
					xsys.removeChild(xmod);
					continue;
				}
				
				sb.append(xmod.toString());
				lstpage = xmod.getElementsByTagName(ACLMapping.NODE_PAGE);
				for (int k = lstpage.getLength() - 1; k >= 0; k--)
				{
					xpage = (Element) lstpage.item(k);
					if ((aopr = findPageChildNodes(xpage, apage)) == null || aopr.size() == 0)
					{
						xmod.removeChild(xpage);
						continue;
					}
					
					sb.append(xpage.toString());
					lstopr = xpage.getElementsByTagName(ACLMapping.NODE_OPR);
					for (int q = lstopr.getLength() - 1; q >= 0; q--)
					{
						xopr = (Element) lstopr.item(q);
						if ((oth = findOpr(xopr, aopr)) == null || !oth.getBoolean(ACLMapping.ATTR_IS_CHECKED))
						{
							xpage.removeChild(xopr);
							continue;
						}
						
						// 这时，剩下的xopr是要保留的。但是保存时，我们不需要下面的method节点
						while (xopr.getChildNodes().getLength() > 0)
							xopr.removeChild(xopr.getLastChild());
					} // foreach opr
					
				} // foreach page
				
			} // foreach module
			
		} // foreach subsystem
		
		return XmlUtils.xdoc2String(xdocMask, new String[] { ACLMapping.ATTR_NAME }, true, null);
	}
	
	private static JSONArray findSubsystemChildNodes(Element xsys, JSONArray asys)
	{// {"id":"backoffice","header":"后台管理","nodes":[...]}
		for (int i = 0, len = asys.size(); i < len; i++)
		{
			if (asys.getJSONObject(i).getString("id").equals(xsys.getAttribute(ACLMapping.ATTR_NAME)))
				return asys.getJSONObject(i).getJSONArray("nodes");
		}
		
		return null;
	}
	
	private static JSONArray findModuleChildNodes(Element xmod, JSONArray amod)
	{// {"id":"dictionary","header":"数据字典","nodes": [...] }
		for (int j = 0, len = amod.size(); j < len; j++)
		{
			if (amod.getJSONObject(j).getString("id").equals(xmod.getAttribute(ACLMapping.ATTR_NAME)))
				return amod.getJSONObject(j).getJSONArray("nodes");
		}
		return null;
	}
	
	private static JSONArray findPageChildNodes(Element xpage, JSONArray apage)
	{// {"id":"specialty", "header":"专业", "nodes":[...]}
		for (int k = 0, len = apage.size(); k < len; k++)
		{
			if (apage.getJSONObject(k).getString("id").equals(xpage.getAttribute(ACLMapping.ATTR_NAME)))
				return apage.getJSONObject(k).getJSONArray("nodes");
		}
		return null;
	}
	
	private static JSONObject findOpr(Element xopr, JSONArray aopr)
	{// {"id":"view","header":"查看所有", "ischecked":true}
		for (int q = 0, len = aopr.size(); q < len; q++)
		{
			if (aopr.getJSONObject(q).getString("id").equals(xopr.getAttribute(ACLMapping.ATTR_NAME)))
				return aopr.getJSONObject(q);
		}
		return null;
	}
}