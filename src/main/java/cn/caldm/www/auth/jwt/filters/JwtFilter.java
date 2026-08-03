package cn.caldm.www.auth.jwt.filters;

import cn.caldm.www.auth.jwt.utils.JwtUtils;
import cn.caldm.www.auth.utils.SecurityContextHolder;
import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
            return;
        }

        final String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            writeErrorResponse(response, ResultCodeEnum.UNAUTHORIZED, "未携带Token授权信息");
            return;
        }

        Map<String, Claim> userData = JwtUtils.verifyToken(token);
        if (userData == null) {
            writeErrorResponse(response, ResultCodeEnum.UNAUTHORIZED, "Token不合法、已过期或已注销");
            return;
        }

        Long id = userData.get("id").asLong();
        String username = userData.get("username").asString();

        try {
            SecurityContextHolder.setUserId(id);
            SecurityContextHolder.setUsername(username);

            request.setAttribute("id", id);
            request.setAttribute("username", username);

            chain.doFilter(req, res);
        } finally {
            SecurityContextHolder.clear();
        }
    }

    private void writeErrorResponse(HttpServletResponse response, ResultCodeEnum resultCode, String customMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Result<Object> result = Result.error(resultCode, customMessage);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
