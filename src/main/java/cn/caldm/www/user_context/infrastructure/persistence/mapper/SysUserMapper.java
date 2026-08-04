package cn.caldm.www.user_context.infrastructure.persistence.mapper;

import cn.caldm.www.user_context.infrastructure.persistence.po.SysUserPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUserPO> {
}
