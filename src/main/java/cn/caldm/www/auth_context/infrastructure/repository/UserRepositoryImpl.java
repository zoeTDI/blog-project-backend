package cn.caldm.www.auth_context.infrastructure.repository;

import cn.caldm.www.auth_context.domain.model.SysUser;
import cn.caldm.www.auth_context.domain.repository.UserRepository;
import cn.caldm.www.auth_context.infrastructure.persistence.mapper.SysUserMapper;
import cn.caldm.www.auth_context.infrastructure.persistence.po.SysUserPO;
import cn.caldm.www.auth_context.interfaces.assembler.UserAssembler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 *
 * 
 * @author caldm
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    SysUserMapper userMapper;

    @Override
    public SysUser findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }

        LambdaQueryWrapper<SysUserPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserPO::getUsername, username)
                .eq(SysUserPO::getDeleted, false);
        SysUserPO po = userMapper.selectOne(queryWrapper);
        return UserAssembler.toDomain(po);
    }

    @Override
    public SysUser findById(Long id) {
        if (id == null) {
            return null;
        }
        SysUserPO po = userMapper.selectById(id);
        return UserAssembler.toDomain(po);
    }
}
