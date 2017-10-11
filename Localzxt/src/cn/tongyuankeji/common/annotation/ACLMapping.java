package cn.tongyuankeji.common.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Action方法对应权限描述
 * 
 * @author 代平 2017-05-02 创建
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ACLMapping
{
	/**
	 * 子系统名。如，backoffice
	 */
	String subsystem();

	/**
	 * 模块名。如，dictionary 
	 */
	String module();

	/**
	 * 页面名。如，role
	 */
	String page();

	/**
	 * 操作组名。如：edit 
	 */
	String opr();

	public final static String NODE_SUBSYSTEM = "subsystem";
	public final static String NODE_MODULE = "module";
	public final static String NODE_PAGE = "page";
	public final static String NODE_OPR = "opr";

	public final static String ATTR_NAME = "name";
	public final static String ATTR_HEADER = "header";	
	public final static String ATTR_PLATFORM = "platform";	// 平台管理员专用
	public final static String ATTR_IS_CHECKED = "ischecked"; // （管理界面）是否勾
	public final static String ATTR_LINK = "link"; // 是eRequestMapping中的哪个 页面跳转对应值
	public final static String ATTR_ICON = "icon"; // 菜单项前面显示的icon
}