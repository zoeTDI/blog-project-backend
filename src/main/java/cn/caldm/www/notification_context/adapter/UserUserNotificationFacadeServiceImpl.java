package cn.caldm.www.notification_context.adapter;

import cn.caldm.www.notification_context.infrastructure.email.EmailSender;
import cn.caldm.www.notification_context.infrastructure.email.EmailTemplateBuilder;
import cn.caldm.www.user_context.application.service.UserNotificationFacadeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 *
 *
 * @author caldm
 */
@Service
public class UserUserNotificationFacadeServiceImpl implements UserNotificationFacadeService {
    @Resource
    private EmailSender emailSender;
    @Resource
    private EmailTemplateBuilder templateBuilder;
    @Override
    public void sendPasswordResetCode(String toEmail, String code) {
        String html = templateBuilder.buildHtml(
                "Caldm Blog",
                "安全中心 - 密码重置",
                "您好！",
                "您正在申请重置 <strong style=\"color: #4f46e5;\">Caldm Blog</strong> 账户密码，请使用下方的验证码完成操作：",
                code,
                "验证码有效期为 <strong style=\"color: #ef4444;\">5 分钟</strong>。为了保障您的账户安全，请勿将验证码泄露给他人。<br>如非本人操作，请忽略此邮件。"
        );
        emailSender.send(toEmail, "【Caldm Blog】密码重置验证码", html);
    }
}
