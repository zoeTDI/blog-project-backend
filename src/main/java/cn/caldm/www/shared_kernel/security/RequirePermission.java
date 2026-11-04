package cn.caldm.www.shared_kernel.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    /** 需要的权限码；多码 OR 需求后续出现时再扩展 anyOf 属性（最小原则） */
    PermissionCodes value();
}
