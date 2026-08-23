package cn.caldm.www.infrastructure.handler;

import cn.caldm.www.common.domain.ErrorDetail;
import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import cn.caldm.www.shared_kernel.security.SecurityContextHolder;
import cn.caldm.www.system_context.infrastructure.persistence.po.InfraApiErrorLogPO;
import cn.caldm.www.system_context.application.service.ApiErrorLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Collections;
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
    private ApiErrorLogService apiErrorLogService;

    @Autowired
    private ObjectMapper objectMapper;

    // ============================ 4xx 客户端错误，不记录栈堆 ============================

    /**
     * 处理 @Valid 校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<ErrorDetail> handleValidationException(MethodArgumentNotValidException e,
            HttpServletRequest request) {
        String errorMsg = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();
        String traceId = generateTraceId();
        saveErrorLog(e, request, traceId, false);
        ErrorDetail detail = new ErrorDetail(traceId, errorMsg);
        return Result.error(ResultCodeEnum.BAD_REQUEST, detail);
    }

    /**
     * 处理请求体 Json 格式错误 / 缺少必要参数
     */
    @ExceptionHandler({ HttpMessageNotReadableException.class, MissingServletRequestParameterException.class })
    public Result<ErrorDetail> handleRequestFormatException(Exception e, HttpServletRequest request) {
        String traceId = generateTraceId();
        String errorMsg = "请求参数格式错误";
        saveErrorLog(e, request, traceId, false);
        ErrorDetail detail = new ErrorDetail(traceId, errorMsg);
        return Result.error(ResultCodeEnum.BAD_REQUEST, detail);
    }

    /**
     * 处理 Service 层抛出的 IllegalArgumentException（代表业务/参数非法）
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<ErrorDetail> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        saveErrorLog(e, request, traceId, false);
        ErrorDetail detail = new ErrorDetail(traceId, e.getMessage());
        return Result.error(ResultCodeEnum.BAD_REQUEST, detail);
    }

    // ============================ 5xx 系统内部错误，记录栈堆 ============================

    /**
     */
    @ExceptionHandler(IllegalStateException.class)
    public Result<ErrorDetail> handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        saveErrorLog(e, request, traceId, true);
        ErrorDetail detail = new ErrorDetail(traceId, e.getMessage());
        return Result.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, detail);
    }

    /**
     * 处理其他所有未捕获的未知异常（兜底）
     * 只有这里才会记录完整堆栈，方便排查 Bug
     */
    @ExceptionHandler(Exception.class)
    public Result<ErrorDetail> handleSystemException(Exception e, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        // 异步记录完整日志
        saveErrorLog(e, request, traceId, true);
        ErrorDetail detail = new ErrorDetail(traceId, "系统异常，请联系管理员");
        return Result.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, detail);
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private void saveErrorLog(Exception e, HttpServletRequest request, String traceId, boolean needStackTrace) {
        try {
            LocalDateTime exceptionTime = LocalDateTime.now();
            InfraApiErrorLogPO errorLog = new InfraApiErrorLogPO();
            // 基础信息
            errorLog.setTraceId(traceId);
            errorLog.setUserId(SecurityContextHolder.getUserId());
            errorLog.setUserType(1);
            errorLog.setApplicationName("blog-backend");

            // 请求信息
            errorLog.setRequestMethod(request.getMethod());
            errorLog.setRequestUrl(request.getRequestURI());
            errorLog.setRequestParams(objectMapper.writeValueAsString(request.getParameterMap()));
            errorLog.setUserIp(getIpAddr(request));
            errorLog.setUserAgent(request.getHeader("User-Agent"));

            // 异常基础信息
            errorLog.setExceptionTime(exceptionTime);
            errorLog.setExceptionName(e.getClass().getName());
            errorLog.setExceptionMessage(e.getMessage() != null ? e.getMessage() : "");

            // 根因消息
            Throwable rootCause = e;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            errorLog.setExceptionRootCauseMessage(rootCause.getMessage() != null ? rootCause.getMessage() : "");

            if (needStackTrace) {
                // 系统异常：记录完整堆栈
                StringWriter sw = new StringWriter();
                try (PrintWriter pw = new PrintWriter(sw)) {
                    e.printStackTrace(pw);
                }
                errorLog.setExceptionStackTrace(sw.toString());
            } else {
                // 业务/校验异常：不记录堆栈，仅标记为客户端错误
                errorLog.setExceptionStackTrace("【Client Error】Stack trace omitted for business/validation exception.");
            }

            // 定位出错代码位置
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
                errorLog.setExceptionFileName(
                        targetElement.getFileName() != null ? targetElement.getFileName() : "Unknown Source");
                errorLog.setExceptionMethodName(targetElement.getMethodName());
                errorLog.setExceptionLineNumber(targetElement.getLineNumber());
            } else {
                errorLog.setExceptionClassName("");
                errorLog.setExceptionFileName("");
                errorLog.setExceptionMethodName("");
                errorLog.setExceptionLineNumber(-1);
            }

            // 异步保存
            apiErrorLogService.createApiErrorLogAsync(errorLog);

        } catch (Exception ex) {
            // 日志落库失败时，仅输出到控制台，不干扰主业务
            System.err.println("全局异常处理器中持久化错误日志失败: " + ex.getMessage());
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
