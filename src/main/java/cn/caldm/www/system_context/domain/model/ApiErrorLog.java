package cn.caldm.www.system_context.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 *
 *
 * @author caldm
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiErrorLog {

    /**
     * 编号
     */
    private Long id;

    /**
     * 链路追踪编号
     */
    private String traceId;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 应用名
     */
    private String applicationName;

    /**
     * 请求方法名
     */
    private String requestMethod;

    /**
     * 请求地址
     */
    private String requestUrl;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 用户 IP
     */
    private String userIp;

    /**
     * 浏览器 UA
     */
    private String userAgent;

    /**
     * 异常发生时间
     */
    private LocalDateTime exceptionTime;

    /**
     * 异常名
     */
    private String exceptionName;

    /**
     * 异常导致的消息
     */
    private String exceptionMessage;

    /**
     * 异常导致的根消息
     */
    private String exceptionRootCauseMessage;

    /**
     * 异常的栈轨迹
     */
    private String exceptionStackTrace;

    /**
     * 异常发生的类全名
     */
    private String exceptionClassName;

    /**
     * 异常发生的类文件
     */
    private String exceptionFileName;

    /**
     * 异常发生的方法名
     */
    private String exceptionMethodName;

    /**
     * 异常发生的方法所在行
     */
    private Integer exceptionLineNumber;

    /**
     * 处理状态 (0-未处理，1-已处理)
     */
    private Integer processStatus;

    /**
     * 处理时间
     */
    private LocalDateTime processTime;

    /**
     * 处理用户编号
     */
    private Integer processUserId;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private String updater;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Boolean deleted;
}
