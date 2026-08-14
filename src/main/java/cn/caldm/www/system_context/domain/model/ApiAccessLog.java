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
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiAccessLog {

    /**
     * 日志主键
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
    private ApiAccessLogUserTypeEnum userType;

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
     * 响应结果
     */
    private String responseBody;

    /**
     * 用户 IP
     */
    private String userIp;

    /**
     * 浏览器 UA
     */
    private String userAgent;

    /**
     * 操作模块
     */
    private String operateModule;

    /**
     * 操作名
     */
    private String operateName;

    /**
     * 操作分类
     */
    private Integer operateType;

    /**
     * 开始请求时间
     */
    private LocalDateTime beginTime;

    /**
     * 结束请求时间
     */
    private LocalDateTime endTime;

    /**
     * 执行时长 (毫秒)
     */
    private Integer duration;

    /**
     * 结果码
     */
    private Integer resultCode;

    /**
     * 结果提示
     */
    private String resultMsg;

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
