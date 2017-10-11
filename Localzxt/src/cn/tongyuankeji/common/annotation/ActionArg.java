package cn.tongyuankeji.common.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Action传入参数描述
 * 
 * @author 代平 2017-05-02 创建
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionArg
{
	/**
	 * 参数名，如：titleZh
	 */
	String name();

	/**
	 * 中文显示名，默认“”。如：标题
	 */
	String header() default "";

	/**
	 * 是否是必填字段，默认 真
	 */
	boolean required() default true;

	/**
	 * 传入的饿参数类型，默认 String.class。如：String.class
	 */
	Class type() default java.lang.String.class;

	/**
	 * 转换成什么类型，默认 String.class。如：Integer.class
	 */
	Class target_type() default java.lang.String.class;

	/**
	 * 是否应被转换成某个Enum，默认 否。
	 */
	boolean isEnumArray() default false;

	/**
	 * target_type = string时，是字符串长度；target_type = 数值型时，是至范围；target_type = 数组时，是数值中元素的个数
	 */
	String[] range() default {};

	/**
	 * 不足有效长度，或超过有效长度时的 用户提示
	 */
	String rangeFailMsg() default "";

	/**
	 * 如果将被转换成小数，小数点后的位数
	 */
	String scale() default "";

	/**
	 * 小数点后精度不正确时的 用户提示
	 */
	String scaleFailMsg() default "";

	/**
	 * 需要用 正则表达式 检验传入参数的。如，IP地址
	 */
	String regex() default "";

	/**
	 * 正则表达式 检验失败时的 用户提示
	 */
	String regexFailMsg() default "";
}