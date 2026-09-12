package cn.caldm.www.auth_context.infrastructure.security;

import cn.caldm.www.auth_context.application.service.AuthUserFacadeService;
import cn.caldm.www.auth_context.interfaces.filter.AccessTokenFilter;
import cn.caldm.www.auth_context.interfaces.filter.RefreshTokenFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import java.io.IOException;

/**
 *
 *
 *
 * @author caldm
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AuthSecurityConfig {

    @Bean
    public AccessTokenFilter accessTokenFilter(AuthUserFacadeService facade, JwtTokenProvider provider) {
        return new AccessTokenFilter(facade, provider);
    }


    @Bean
    public FilterRegistrationBean<AccessTokenFilter> accessTokenFilterRegistration(AccessTokenFilter filter) {
        FilterRegistrationBean<AccessTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }


    @Bean
    public RefreshTokenFilter refreshTokenFilter(AuthUserFacadeService facade, JwtTokenProvider provider) {
        return new RefreshTokenFilter(facade, provider);
    }

    @Bean
    public FilterRegistrationBean<RefreshTokenFilter> refreshTokenFIlterRegistration(RefreshTokenFilter filter) {
        FilterRegistrationBean<RefreshTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain refreshChian(HttpSecurity http, RefreshTokenFilter refreshTokenFilter) throws Exception {
        http
                .securityMatcher("/auth/refresh")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(refreshTokenFilter, AuthorizationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> writeJson(res, 401, "Refresh failed."))
                        .accessDeniedHandler((req, res, e) -> writeJson(res, 403, "Forbidden."))
                );
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AccessTokenFilter accessTokenFilter,
                                           AnonymousAuthorizationManager anonymousAuthorizationManager
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().access(anonymousAuthorizationManager))
                .addFilterBefore(accessTokenFilter, AuthorizationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);
        return http.build();
    }

    private static void writeJson(HttpServletResponse response, int status, String msg) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"code\":" + status + ",\"msg\":\"" + msg + "\"}");
    }
}
