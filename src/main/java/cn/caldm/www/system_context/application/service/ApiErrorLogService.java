package cn.caldm.www.system_context.application.service;

import cn.caldm.www.system_context.infrastructure.persistence.po.InfraApiErrorLogPO;

/**
 * 系统异常日志 Service 接口
 *
 * @author caldm
 */
public interface ApiErrorLogService {
    /**
     * 异步创建系统异常日志
     *
     * @param errorLog 异常日志信息
     */
    void createApiErrorLogAsync(InfraApiErrorLogPO errorLog);
}
