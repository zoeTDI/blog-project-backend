package cn.caldm.www.system_context.infrastructure.persistence.mapper;

import cn.caldm.www.system_context.infrastructure.persistence.po.InfraApiErrorLogPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统异常日志表 Mapper 接口
 *
 * @author caldm
 */
@Mapper
public interface InfraApiErrorLogMapper extends BaseMapper<InfraApiErrorLogPO> {
}
