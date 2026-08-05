package cn.caldm.www.user_context.application.service;

public interface UserNotificationFacadeService {
    /**
     * 发送密码重置验证码
     */
    void sendPasswordResetCode(String toEmail, String code);
}
