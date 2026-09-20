package cn.caldm.www.permission_context.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.caldm.www.permission_context.infrastructure.persistence.po.RolePO;

@Mapper 
public interface RoleMapper extends BaseMapper<RolePO> {

}
