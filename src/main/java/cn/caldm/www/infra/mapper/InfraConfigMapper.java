package cn.caldm.www.infra.mapper;

import cn.caldm.www.infra.domain.InfraConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 参数配置表 Mapper 接口
 *
 * @author caldm
 */
@Mapper
public interface InfraConfigMapper extends BaseMapper<InfraConfig> {

}