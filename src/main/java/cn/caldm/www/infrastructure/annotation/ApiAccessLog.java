package cn.caldm.www.infrastructure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API 访问日志自定义注解
 * 挂在 Controller 方法上，用于自动记录审计日志
 *
 * @author caldm
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiAccessLog {
    /**
     * 操作模块 (例如：文章管理、评论管理)
     */
    String operateModule() default "";
    /**
     * 操作名 (例如：发布文章、删除评论)
     */
    String operateName() default "";

    /**
     * 操作分类 (0=其他, 1=查询, 2=新增, 3=修改, 4=删除)
     */
    int operateType() default 0;
}
