package cn.caldm.www.notification_context.adapter;

import cn.caldm.www.auth_context.application.service.AuthNotificationFacadeService;
import cn.caldm.www.notification_context.infrastructure.email.EmailSender;
import cn.caldm.www.notification_context.infrastructure.email.EmailTemplateBuilder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 *
 *
 * @author caldm
 */
@Service
public class AuthNotificationFacadeServiceImpl implements AuthNotificationFacadeService {
    @Resource
    private EmailSender emailSender;

    @Resource
    private EmailTemplateBuilder templateBuilder;

    @Override
    public void sendLoginCode(String toEmail, String code) {
        String html = templateBuilder.buildHtml(
                "Caldm Blog",
                "安全中心 - 身份验证",
                "您好！",
                "您正在尝试登录 <strong style=\"color: #4f46e5;\">Caldm Blog</strong>，请使用下方的验证码完成登录验证：",
                code,
                "验证码有效期为 <strong style=\"color: #ef4444;\">5 分钟</strong>。为了保障您的账户安全，请勿将验证码泄露给他人。"
        );
        emailSender.send(toEmail, "【Caldm Blog】登录验证码", html);
        // emailSender.sendCustomHtmlEmail(
        //         toEmail,
        //         "【Caldm Blog】登录验证码",
        //         "Caldm Blog",
        //         "安全中心 - 身份验证",
        //         "您好！",
        //         "您正在尝试登录 <strong style=\"color: #4f46e5;\">Caldm Blog</strong>，请使用下方的验证码完成登录验证：",
        //         code,
        //         "验证码有效期为 <strong style=\"color: #ef4444;\">5 分钟</strong>。为了保障您的账户安全，请勿将验证码泄露给他人。<br>如非本人操作，请忽略此邮件。"
        // );
    }
}
