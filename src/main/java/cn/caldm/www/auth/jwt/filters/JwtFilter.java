package cn.caldm.www.auth.jwt.filters;

import cn.caldm.www.auth.jwt.utils.JwtUtils;
import com.auth0.jwt.interfaces.Claim;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 *
 * JWT过滤器
 *
 * @author caldm
 */
@Slf4j
@WebFilter(filterName = "JwtFilter", urlPatterns = "/secure/*")
public class JwtFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        response.setCharacterEncoding("UTF-8");
        final String token = request.getHeader("Authorization");
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
        } else {
            if (token == null) {
                response.getWriter().write("没有token");
                return;
            }
            Map<String, Claim> userData = JwtUtils.verifyToken(token);
            if (userData == null) {
                response.getWriter().write("token不合法");
                return;
            }
            Long id = userData.get("id").asLong();
            String username = userData.get("username").asString();
            String password = userData.get("password").asString();
            request.setAttribute("id", id);
            request.setAttribute("username", username);
            request.setAttribute("password", password);
            chain.doFilter(req, res);
        }
    }
}
