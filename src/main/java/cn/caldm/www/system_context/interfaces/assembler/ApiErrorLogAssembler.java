package cn.caldm.www.system_context.interfaces.assembler;

import cn.caldm.www.system_context.domain.model.ApiErrorLog;
import cn.caldm.www.system_context.infrastructure.persistence.po.InfraApiErrorLogPO;

/**
 *
 *
 *
 * @author caldm
 */
public class ApiErrorLogAssembler {

    public static ApiErrorLog toDomain(InfraApiErrorLogPO po) {
        ApiErrorLog apiErrorLog = new ApiErrorLog();
        apiErrorLog.setId(po.getId());
        apiErrorLog.setTraceId(po.getTraceId());
        apiErrorLog.setUserId(po.getUserId());
        apiErrorLog.setUserType(po.getUserType());
        apiErrorLog.setApplicationName(po.getApplicationName());
        apiErrorLog.setRequestMethod(po.getRequestMethod());
        apiErrorLog.setRequestUrl(po.getRequestUrl());
        apiErrorLog.setRequestParams(po.getRequestParams());
        apiErrorLog.setUserIp(po.getUserIp());
        apiErrorLog.setUserAgent(po.getUserAgent());
        apiErrorLog.setExceptionTime(po.getExceptionTime());
        apiErrorLog.setExceptionName(po.getExceptionName());
        apiErrorLog.setExceptionMessage(po.getExceptionMessage());
        apiErrorLog.setExceptionRootCauseMessage(po.getExceptionRootCauseMessage());
        apiErrorLog.setExceptionStackTrace(po.getExceptionStackTrace());
        apiErrorLog.setExceptionClassName(po.getExceptionClassName());
        apiErrorLog.setExceptionFileName(po.getExceptionFileName());
        apiErrorLog.setExceptionMethodName(po.getExceptionMethodName());
        apiErrorLog.setExceptionLineNumber(po.getExceptionLineNumber());
        apiErrorLog.setProcessStatus(po.getProcessStatus());
        apiErrorLog.setProcessTime(po.getProcessTime());
        apiErrorLog.setProcessUserId(po.getProcessUserId());
        apiErrorLog.setCreator(po.getCreator());
        apiErrorLog.setCreateTime(po.getCreateTime());
        apiErrorLog.setUpdater(po.getUpdater());
        apiErrorLog.setUpdateTime(po.getUpdateTime());
        apiErrorLog.setDeleted(po.getDeleted());
        return apiErrorLog;
    }

}
