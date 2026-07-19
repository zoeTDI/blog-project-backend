package cn.caldm.www.common.utils;

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
        // TODO 实际接入 Spring Security 后，从 Authentication 中获取
        return 1L;
    }

    /**
     * 获取当前登录用户的用户名
     *
     * @return 用户名
     */
    public static String getLoginUsername() {
        // TODO 临时模拟当前登录人，用于写入数据表的 creator 字段
        return "admin";
    }
}
