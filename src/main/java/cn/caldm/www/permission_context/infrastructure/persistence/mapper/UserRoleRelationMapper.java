package cn.caldm.www.permission_context.infrastructure.persistence.mapper;

import cn.caldm.www.permission_context.infrastructure.persistence.po.UserRoleRelationPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleRelationMapper extends BaseMapper<UserRoleRelationPO> {
}
