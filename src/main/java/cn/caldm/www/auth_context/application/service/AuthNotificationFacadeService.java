package cn.caldm.www.auth_context.application.service;

/**
 * 认证上下文所需的外部通知防腐接口
 *
 * @author caldm
 */
public interface AuthNotificationFacadeService {
    /**
     * 发送登录验证码邮件
     */
    void sendLoginCode(String toEmail, String code);
}
