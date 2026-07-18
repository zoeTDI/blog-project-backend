package cn.caldm.www.infra.mapper;

import cn.caldm.www.infra.domain.InfraApiAccessLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * API 访问日志表 Mapper 接口
 *
 * @author caldm
 */
@Mapper
public interface InfraApiAccessLogMapper extends BaseMapper<InfraApiAccessLog> {

}
