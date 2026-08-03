package cn.caldm.www.common.utils;

import cn.caldm.www.auth.utils.SecurityContextHolder;

/**
 * 安全服务工具类
 * 用于获取当前登录用户的上下文信息，防止前端伪造 creator
 */
public class SecurityUtils {
    /**
     * 获取当前登录用户的ID
     * 临时模拟返回管理员ID，后续可对接 Spring Security: SecurityContextHolder.getContext().getAuthentication()
     *
     * @return 用户ID
     */
    public static Long getLoginUserId() {
        return SecurityContextHolder.getUserId();
    }

    /**
     * 获取当前登录用户的用户名
     *
     * @return 用户名
     */
    public static String getLoginUsername() {
        return SecurityContextHolder.getUsername();
    }
}
