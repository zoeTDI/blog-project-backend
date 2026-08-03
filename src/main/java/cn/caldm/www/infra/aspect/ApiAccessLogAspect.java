package cn.caldm.www.infra.aspect;

import cn.caldm.www.shared_kernel.security.SecurityContextHolder;
import cn.caldm.www.infra.annotation.ApiAccessLog;
import cn.caldm.www.infra.domain.InfraApiAccessLog;
import cn.caldm.www.infra.service.InfraApiAccessLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * API 访问日志 AOP 切面
 *
 * @author caldm
 */
@Aspect
@Component
public class ApiAccessLogAspect {

    @Autowired
    private InfraApiAccessLogService infraApiAccessLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Around("@annotation(apiAccessLog)")
    public Object doAround(ProceedingJoinPoint joinPoint, ApiAccessLog apiAccessLog) throws Throwable {
        LocalDateTime beginTime = LocalDateTime.now();
        long startTimeMills = System.currentTimeMillis();

        String traceId = UUID.randomUUID().toString().replace("-", "");

        Object result = null;
        Integer resultCode = 0;
        String resultMsg = "成功";

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            resultCode = 500;
            resultMsg = e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTimeMills;
            LocalDateTime endTime = LocalDateTime.now();

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                try {
                    InfraApiAccessLog accessLog = new InfraApiAccessLog();
                    accessLog.setTraceId(traceId);
                    accessLog.setUserId(SecurityContextHolder.getUserId());
                    accessLog.setUserType(1); // 假设 1 为后台管理用户
                    accessLog.setApplicationName("blog-backend");

                    accessLog.setRequestMethod(request.getMethod());
                    accessLog.setRequestUrl(request.getRequestURI());

                    Object[] args = joinPoint.getArgs();
                    if (args != null && args.length > 0) {
                        accessLog.setRequestParams(objectMapper.writeValueAsString(args));
                    }

                    if (result != null) {
                        accessLog.setResponseBody(objectMapper.writeValueAsString(result));
                    }

                    accessLog.setUserIp(getIpAddr(request));
                    accessLog.setUserAgent(request.getHeader("User-Agent"));

                    accessLog.setOperateModule(apiAccessLog.operateModule());
                    accessLog.setOperateName(apiAccessLog.operateName());
                    accessLog.setOperateType(apiAccessLog.operateType());

                    accessLog.setBeginTime(beginTime);
                    accessLog.setEndTime(endTime);
                    accessLog.setDuration((int) duration);
                    accessLog.setResultCode(resultCode);
                    accessLog.setResultMsg(resultMsg);

                    infraApiAccessLogService.createApiAccessLogAsync(accessLog);
                } catch (Exception ex) {
                    System.err.println("解析或保存 API 访问日志失败: " + ex.getMessage());                }
            }
        }
    }

    /**
     * 获取客户端真实 IP 地址的工具方法
     */
    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }
}
