package cn.caldm.www.user_context.infrastructure.repository;

import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import cn.caldm.www.user_context.infrastructure.persistence.mapper.SysUserMapper;
import cn.caldm.www.user_context.infrastructure.persistence.po.SysUserPO;
import cn.caldm.www.user_context.interfaces.assembler.UserAssembler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 *
 *
 * 
 * @author caldm
 */
@Repository("userContextUserRepositoryImpl")
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

    @Override
    public SysUser insert(String creator, String username, String password) {
        SysUserPO sysUserPO = new SysUserPO();
        sysUserPO.setUsername(username);
        sysUserPO.setPassword(password);
        sysUserPO.setStatus((short) 0);
        sysUserPO.setDeleted(false);
        sysUserPO.setCreator(creator);
        sysUserPO.setCreateTime(LocalDateTime.now());
        sysUserPO.setUpdater(creator);
        sysUserPO.setUpdateTime(LocalDateTime.now());

        sysUserPO.setNickname(username);
        sysUserPO.setEmail("example@xx.com");
        sysUserPO.setSalt("salt");
        int inserted = userMapper.insert(sysUserPO);
        if (inserted == 1) {
            return UserAssembler.toDomain(sysUserPO);
        } else {
            return null;
        }
    }
}
