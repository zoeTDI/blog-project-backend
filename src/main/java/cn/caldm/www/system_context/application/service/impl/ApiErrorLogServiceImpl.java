package cn.caldm.www.system_context.application.service.impl;

import cn.caldm.www.system_context.application.service.ApiErrorLogService;
import cn.caldm.www.system_context.infrastructure.persistence.po.InfraApiErrorLogPO;
import lombok.RequiredArgsConstructor;
import cn.caldm.www.system_context.infrastructure.persistence.mapper.InfraApiErrorLogMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 系统异常日志 Service 实现类
 *
 * @author caldm
 */
@Service
@RequiredArgsConstructor 
public class ApiErrorLogServiceImpl implements ApiErrorLogService {
    
    private final InfraApiErrorLogMapper infraApiErrorLogMapper;

    @Override
    @Async
    public void createApiErrorLogAsync(InfraApiErrorLogPO errorLog) {
        if (errorLog.getExceptionTime() == null) {
            errorLog.setExceptionTime(LocalDateTime.now());
        }
        if (errorLog.getCreateTime() == null) {
            errorLog.setCreateTime(LocalDateTime.now());
        }
        if (errorLog.getUpdateTime() == null) {
            errorLog.setUpdateTime(LocalDateTime.now());
        }

        errorLog.setProcessStatus(0);
        errorLog.setDeleted(false);

        infraApiErrorLogMapper.insert(errorLog);
    }
}
