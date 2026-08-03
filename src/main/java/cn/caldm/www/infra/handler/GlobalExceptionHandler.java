package cn.caldm.www.infra.handler;

import cn.caldm.www.shared_kernel.security.SecurityContextHolder;
import cn.caldm.www.infra.domain.InfraApiErrorLog;
import cn.caldm.www.infra.service.InfraApiErrorLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 全局异常处理器
 * 核心职责：拦截全局异常、统一返回 500 格式、异步解析并录入 infra_api_error_log 表
 *
 * @author caldm
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private InfraApiErrorLogService infraApiErrorLogService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 拦截系统所有未捕获的根异常 (Exception)
     */
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(Exception e, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime exceptionTime = LocalDateTime.now();

        try {
            saveErrorLog(e, request, traceId, exceptionTime);
        } catch (Exception ex) {
            System.err.println("全局异常处理器中持久化错误日志失败: " + ex.getMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("msg", "系统异常，请联系管理员");
        result.put("traceId", traceId);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 核心解析与保存方法
     */
    private void saveErrorLog(Exception e, HttpServletRequest request, String traceId, LocalDateTime exceptionTime) throws Exception {
        InfraApiErrorLog errorLog = new InfraApiErrorLog();

        errorLog.setTraceId(traceId);
        errorLog.setUserId(SecurityContextHolder.getUserId());
        errorLog.setUserType(1);
        errorLog.setApplicationName("blog-backend");

        errorLog.setRequestMethod(request.getMethod());
        errorLog.setRequestUrl(request.getRequestURI());
        errorLog.setRequestParams(objectMapper.writeValueAsString(request.getParameterMap()));
        errorLog.setUserIp(getIpAddr(request));
        errorLog.setUserAgent(request.getHeader("User-Agent"));

        errorLog.setExceptionTime(exceptionTime);

        errorLog.setExceptionName(e.getClass().getName());
        errorLog.setExceptionMessage(e.getMessage() != null ? e.getMessage() : "");

        Throwable rootCause = e;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        errorLog.setExceptionRootCauseMessage(rootCause.getMessage() != null ? rootCause.getMessage() : "");

        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            errorLog.setExceptionStackTrace(sw.toString());
        }

        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            StackTraceElement targetElement = stackTrace[0];

            for (StackTraceElement element : stackTrace) {
                if (element.getClassName().startsWith("cn.caldm.www")) {
                    targetElement = element;
                    break;
                }
            }

            errorLog.setExceptionClassName(targetElement.getClassName());
            errorLog.setExceptionFileName(targetElement.getFileName() != null ? targetElement.getFileName() : "Unknown Source");
            errorLog.setExceptionMethodName(targetElement.getMethodName());
            errorLog.setExceptionLineNumber(targetElement.getLineNumber());
        } else {
            errorLog.setExceptionClassName("");
            errorLog.setExceptionFileName("");
            errorLog.setExceptionMethodName("");
            errorLog.setExceptionLineNumber(-1);
        }

        infraApiErrorLogService.createApiErrorLogAsync(errorLog);
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
