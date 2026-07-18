package cn.caldm.www.infra.service;

import cn.caldm.www.infra.domain.InfraApiErrorLog;

/**
 * 系统异常日志 Service 接口
 *
 * @author caldm
 */
public interface InfraApiErrorLogService {
    /**
     * 异步创建系统异常日志
     *
     * @param errorLog 异常日志信息
     */
    void createApiErrorLogAsync(InfraApiErrorLog errorLog);
}
