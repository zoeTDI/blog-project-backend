package cn.caldm.www.file_context.infrastructure.persistence.mapper;

import cn.caldm.www.file_context.infrastructure.persistence.po.InfraFilePO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InfraFileMapper extends BaseMapper<InfraFilePO> {
}
