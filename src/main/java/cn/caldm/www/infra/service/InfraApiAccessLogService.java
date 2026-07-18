package cn.caldm.www.infra.service;

import cn.caldm.www.infra.domain.InfraApiAccessLog;

/**
 * API 访问日志 Service 接口
 *
 * @author caldm
 */
public interface InfraApiAccessLogService {
    void createApiAccessLogAsync(InfraApiAccessLog accessLog);
}
