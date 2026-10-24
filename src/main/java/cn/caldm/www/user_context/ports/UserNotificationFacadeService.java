package cn.caldm.www.user_context.ports;

public interface UserNotificationFacadeService {
    /**
     * 发送密码重置验证码
     */
    void sendPasswordResetCode(String toEmail, String code);
}
