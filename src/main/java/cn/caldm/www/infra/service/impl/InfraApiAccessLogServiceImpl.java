package cn.caldm.www.infra.service.impl;

import cn.caldm.www.infra.domain.InfraApiAccessLog;
import cn.caldm.www.infra.mapper.InfraApiAccessLogMapper;
import cn.caldm.www.infra.service.InfraApiAccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * API 访问日志 Service 实现类
 *
 * @author caldm
 */
@Service
public class InfraApiAccessLogServiceImpl implements InfraApiAccessLogService {
    @Autowired
    private InfraApiAccessLogMapper infraApiAccessLogMapper;

    @Override
    @Async
    public void createApiAccessLogAsync(InfraApiAccessLog accessLog) {
        if (accessLog.getCreateTime() == null) {
            accessLog.setCreateTime(LocalDateTime.now());
        }
        if (accessLog.getUpdateTime() == null) {
            accessLog.setUpdateTime(LocalDateTime.now());
        }
        accessLog.setDeleted(0);
        infraApiAccessLogMapper.insert(accessLog);
    }
}
