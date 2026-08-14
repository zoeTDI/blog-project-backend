package cn.caldm.www.system_context.infrastructure.persistence.mapper;

import cn.caldm.www.system_context.infrastructure.persistence.po.InfraConfigPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 参数配置表 Mapper 接口
 *
 * @author caldm
 */
@Mapper
public interface InfraConfigMapper extends BaseMapper<InfraConfigPO> {

}