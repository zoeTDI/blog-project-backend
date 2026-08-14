package cn.caldm.www.system_context.application.service;

import cn.caldm.www.system_context.infrastructure.persistence.po.InfraApiAccessLogPO;

/**
 * API 访问日志 Service 接口
 *
 * @author caldm
 */
public interface InfraApiAccessLogService {
    void createApiAccessLogAsync(InfraApiAccessLogPO accessLog);
}
