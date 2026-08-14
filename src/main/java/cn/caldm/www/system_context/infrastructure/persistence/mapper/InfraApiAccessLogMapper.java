package cn.caldm.www.system_context.infrastructure.persistence.mapper;

import cn.caldm.www.system_context.infrastructure.persistence.po.InfraApiAccessLogPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * API 访问日志表 Mapper 接口
 *
 * @author caldm
 */
@Mapper
public interface InfraApiAccessLogMapper extends BaseMapper<InfraApiAccessLogPO> {

}
