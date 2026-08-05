package cn.caldm.www.notification_context.infrastructure.email;

import org.springframework.stereotype.Component;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class EmailTemplateBuilder {
    /**
     * 构建通用的 HTML 邮件内容
     */
    public String buildHtml(String headerTitle, String headerSub, String greeting,
                            String description, String code, String tip) {
        String codeBlock = "";
        if (code != null && !code.trim().isEmpty()) {
            codeBlock = "<div style=\"background-color: #f8fafc; border: 1px dashed #cbd5e1; border-radius: 6px; padding: 20px; text-align: center; margin: 30px 0;\">" +
                    "    <span style=\"font-size: 32px; font-weight: bold; letter-spacing: 6px; color: #4f46e5;\">" + code + "</span>" +
                    "</div>";
        }

        return "<div style=\"font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #f4f6f8; margin: 0; padding: 40px 0;\">" +
                "    <div style=\"max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05);\">" +
                "        <div style=\"background: linear-gradient(135deg, #4f46e5 0%, #3b82f6 100%); padding: 30px; text-align: center; color: #ffffff;\">" +
                "            <h2 style=\"margin: 0; font-size: 24px; font-weight: 600;\">" + headerTitle + "</h2>" +
                "            <p style=\"margin: 8px 0 0; font-size: 14px; opacity: 0.9;\">" + headerSub + "</p>" +
                "        </div>" +
                "        <div style=\"padding: 40px 30px; color: #333333;\">" +
                "            <p style=\"font-size: 16px; line-height: 1.5; margin-top: 0;\">" + greeting + "</p>" +
                "            <p style=\"font-size: 15px; line-height: 1.5;\">" + description + "</p>" +
                "            " + codeBlock +
                "            <p style=\"font-size: 14px; color: #64748b; line-height: 1.5;\">" + tip + "</p>" +
                "        </div>" +
                "        <div style=\"background-color: #f8fafc; padding: 20px 30px; text-align: center; font-size: 12px; color: #94a3b8; border-top: 1px solid #e2e8f0;\">" +
                "            <p style=\"margin: 0;\">此为系统自动发送邮件，请勿直接回复。</p>" +
                "            <p style=\"margin: 5px 0 0;\">&copy; 2026 Caldm Blog. All rights reserved.</p>" +
                "        </div>" +
                "    </div>" +
                "</div>";
    }
}
