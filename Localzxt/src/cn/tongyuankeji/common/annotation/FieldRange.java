package cn.tongyuankeji.common.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AppSetting中配置项 默认值、取值范围性描述
 * 
 * @author 代平 2017-05-02 创建
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldRange
{
	/**
	 * 最小值 下边界。不使用此值时，设为""
	 */
	String min() default "";

	/**
	 * 最小值 上边界。不使用此值时，设为""
	 */
	String max() default "";

	String defaultAs() default "";
}