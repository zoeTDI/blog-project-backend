package cn.caldm.www.system_context.application.service;

import cn.caldm.www.system_context.infrastructure.persistence.po.InfraApiAccessLogPO;
import cn.caldm.www.system_context.infrastructure.persistence.mapper.InfraApiAccessLogMapper;
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
    public void createApiAccessLogAsync(InfraApiAccessLogPO accessLog) {
        if (accessLog.getCreateTime() == null) {
            accessLog.setCreateTime(LocalDateTime.now());
        }
        if (accessLog.getUpdateTime() == null) {
            accessLog.setUpdateTime(LocalDateTime.now());
        }
        accessLog.setDeleted(false);
        infraApiAccessLogMapper.insert(accessLog);
    }
}
