package cn.caldm.www.system_context.interfaces.assembler;

import cn.caldm.www.system_context.domain.model.ApiAccessLog;
import cn.caldm.www.system_context.infrastructure.persistence.po.InfraApiAccessLogPO;

/**
 *
 *
 *
 * @author caldm
 */
public class ApiAccessLogAssembler {

    public static ApiAccessLog toDomain(InfraApiAccessLogPO po) {
        ApiAccessLog apiAccessLog = new ApiAccessLog();
        apiAccessLog.setId(po.getId());
        apiAccessLog.setTraceId(po.getTraceId());
        apiAccessLog.setUserId(po.getUserId());
        apiAccessLog.setUserType(po.getUserType());
        apiAccessLog.setApplicationName(po.getApplicationName());
        apiAccessLog.setRequestMethod(po.getRequestMethod());
        apiAccessLog.setRequestUrl(po.getRequestUrl());
        apiAccessLog.setRequestParams(po.getRequestParams());
        apiAccessLog.setResponseBody(po.getResponseBody());
        apiAccessLog.setUserIp(po.getUserIp());
        apiAccessLog.setUserAgent(po.getUserAgent());
        apiAccessLog.setOperateModule(po.getOperateModule());
        apiAccessLog.setOperateName(po.getOperateName());
        apiAccessLog.setOperateType(po.getOperateType());
        apiAccessLog.setBeginTime(po.getBeginTime());
        apiAccessLog.setEndTime(po.getEndTime());
        apiAccessLog.setDuration(po.getDuration());
        apiAccessLog.setResultCode(po.getResultCode());
        apiAccessLog.setResultMsg(po.getResultMsg());
        apiAccessLog.setCreator(po.getCreator());
        apiAccessLog.setCreateTime(po.getCreateTime());
        apiAccessLog.setUpdater(po.getUpdater());
        apiAccessLog.setUpdateTime(po.getUpdateTime());
        apiAccessLog.setDeleted(po.getDeleted());
        return apiAccessLog;
    }
}
