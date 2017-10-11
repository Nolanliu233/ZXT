package cn.tongyuankeji.common.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Entity的属性描述
 * 
 * @author 代平 2017-05-02 创建
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityDesc
{
	/************************** 转JSON时使用的 **************************/
	/**
	 * 调用BaseEntity.toJson()时，keepAll与always的匹配：keepAll = false，只保留always = true的getXXX属性 ；keepAll = true, 所有标识了OEEntityDesc的getXXX属性
	 */
	boolean always() default false;

	/**
	 * 调用BaseEntity.toJson()时，showZh控制是否要将值转化为String。showZh = true，遇到makeString的，转出文字（json有双引号）
	 */
	boolean makeString() default false;

	/**
	 * 配合makeString使用。是enum时，指明对应的enum类。<br />
	 * 在转换时, makeString()=true的，调用enum上的entityDescGet(Integer)取得显示中文
	 */
	Class enumType() default Object.class;

	/**
	 * 当类型是Timestamp时，是否转成日期（丢掉后面的时间部分）
	 */
	boolean shortDate() default false;
	
	boolean emptyStringUseNull() default true;

	/************************** 记trace时使用的 **************************/
	/**
	 * getSession.save()、update()中，是否记录这个属性的值到log_detail中
	 */
	boolean traceOn() default true;
}