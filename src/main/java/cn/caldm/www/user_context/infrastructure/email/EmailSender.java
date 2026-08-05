package cn.caldm.www.user_context.infrastructure.email;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class EmailSender {
    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 发送精美的 HTML 验证码邮件
     *
     * @param toEmail 接收者邮箱
     * @param code    验证码
     */
    public void sendVerificationCode(String toEmail, String code) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("【Caldm Blog】密码重置验证码");

            String htmlContent = buildHtmlContent(code);

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("HTML 邮件发送失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建优雅的 HTML 邮件模板
     */
    private String buildHtmlContent(String code) {
        return "<div style=\"font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f4f6f8; margin: 0; padding: 40px 0;\">" +
                "    <div style=\"max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);\">" +
                "        <!-- 头部标题 -->" +
                "        <div style=\"background: linear-gradient(135deg, #4f46e5 0%, #3b82f6 100%); padding: 30px; text-align: center; color: #ffffff;\">" +
                "            <h2 style=\"margin: 0; font-size: 24px; font-weight: 600;\">Caldm Blog</h2>" +
                "            <p style=\"margin: 8px 0 0; font-size: 14px; opacity: 0.9;\">安全中心 - 密码重置</p>" +
                "        </div>" +
                "        <!-- 主体内容 -->" +
                "        <div style=\"padding: 40px 30px; color: #333333;\">" +
                "            <p style=\"font-size: 16px; line-height: 1.5; margin-top: 0;\">您好！</p>" +
                "            <p style=\"font-size: 15px; line-height: 1.5;\">您正在申请重置 <strong style=\"color: #4f46e5;\">Caldm Blog</strong> 账户密码，请使用下方的验证码完成操作：</p>" +
                "            <!-- 验证码卡片块 -->" +
                "            <div style=\"background-color: #f8fafc; border: 1px dashed #cbd5e1; border-radius: 6px; padding: 20px; text-align: center; margin: 30px 0;\">" +
                "                <span style=\"font-size: 32px; font-weight: bold; letter-spacing: 6px; color: #4f46e5;\">" + code + "</span>" +
                "            </div>" +
                "            <p style=\"font-size: 14px; color: #64748b; line-height: 1.5;\">验证码有效期为 <strong style=\"color: #ef4444;\">5 分钟</strong>。为了保障您的账户安全，请勿将验证码泄露给他人。</p>" +
                "            <p style=\"font-size: 14px; color: #64748b; margin-bottom: 0;\">如非本人操作，请忽略此邮件。</p>" +
                "        </div>" +
                "        <!-- 页脚信息 -->" +
                "        <div style=\"background-color: #f8fafc; padding: 20px 30px; text-align: center; font-size: 12px; color: #94a3b8; border-top: 1px solid #e2e8f0;\">" +
                "            <p style=\"margin: 0;\">此为系统自动发送邮件，请勿直接回复。</p>" +
                "            <p style=\"margin: 5px 0 0;\">&copy; 2026 Caldm Blog. All rights reserved.</p>" +
                "        </div>" +
                "    </div>" +
                "</div>";
    }
}
