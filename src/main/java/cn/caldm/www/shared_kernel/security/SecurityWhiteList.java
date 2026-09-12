package cn.caldm.www.shared_kernel.security;

/**
 *
 *
 *
 * @author caldm
 */
public final class SecurityWhiteList {
    private SecurityWhiteList() {}

    public static final String[] PATTERNS = {
            "/auth/login",
            "/auth/login/username-password",
            "/auth/login/email-password",
            "/auth/login/email-code",
            "/auth/send-login-code",
            "/*/public/**",
            "/file/**"
    };

    public static final String REFRESH_PATH = "/auth/refresh";
}
