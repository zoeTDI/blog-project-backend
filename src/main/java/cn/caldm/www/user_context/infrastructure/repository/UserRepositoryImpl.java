package cn.caldm.www.user_context.infrastructure.repository;

import cn.caldm.www.user_context.domain.model.*;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import cn.caldm.www.user_context.infrastructure.persistence.mapper.SysUserMapper;
import cn.caldm.www.user_context.infrastructure.persistence.po.SysUserPO;
import cn.caldm.www.user_context.infrastructure.persistence.assembler.UserAssembler;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 *
 *
 *
 * @author caldm
 */
@Repository("userContextUserRepositoryImpl")
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final SysUserMapper userMapper;
    private final UserAssembler userAssembler;

    @Override
    public SysUser findByEmail(String email) {
        LambdaQueryWrapper<SysUserPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserPO::getEmail, email);
        SysUserPO po = userMapper.selectOne(queryWrapper);
        if (po == null) {
            return null;
        }

        return userAssembler.toDomain(po);
    }

    @Override
    public SysUser findByUsername(String username) {
        LambdaQueryWrapper<SysUserPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserPO::getUsername, username);
        SysUserPO po = userMapper.selectOne(queryWrapper);
        if (po == null) {
            return null;
        }

        return userAssembler.toDomain(po);
    }

    @Override
    public SysUser findById(Long id) {
        SysUserPO po = userMapper.selectById(id);
        if (po == null) {
            return null;
        }

        return userAssembler.toDomain(po);
    }

    @Override
    public SysUser insert(String creator, String username, String password, String email, String nickname) {
        SysUserPO sysUserPO = new SysUserPO();
        sysUserPO.setUsername(username);
        sysUserPO.setPassword(password);
        sysUserPO.setEmail(email);
        sysUserPO.setNickname(nickname);
        sysUserPO.setStatus(SysUserStatusEnum.NORMAL);
        sysUserPO.setDeleted(SysUserDeletedEnum.NORMAL);
        sysUserPO.setCreator(creator);
        sysUserPO.setCreateTime(LocalDateTime.now());
        sysUserPO.setUpdater(creator);
        sysUserPO.setUpdateTime(LocalDateTime.now());

        int inserted = userMapper.insert(sysUserPO);
        if (inserted != 1 || sysUserPO.getId() == null) {
            return null;
        }

        return userAssembler.toDomain(sysUserPO);
    }

    @Override
    public boolean updateStatus(Long id, SysUserStatusEnum status, String updater) {
        LambdaUpdateWrapper<SysUserPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUserPO::getId, id);
        wrapper.set(SysUserPO::getStatus, status);
        wrapper.set(SysUserPO::getUpdater, updater);
        wrapper.set(SysUserPO::getUpdateTime, LocalDateTime.now());
        return userMapper.update(wrapper) == 1;
    }

    @Override
    public boolean markDeleted(Long id, String updater) {
        LambdaUpdateWrapper<SysUserPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUserPO::getId, id);
        wrapper.set(SysUserPO::getDeleted, SysUserDeletedEnum.DELETED);
        wrapper.set(SysUserPO::getUpdater, updater);
        wrapper.set(SysUserPO::getUpdateTime, LocalDateTime.now());
        return userMapper.update(wrapper) == 1;
    }

    @Override
    public boolean updatePassword(Long id, String encodedPassword, String updater) {
        LambdaUpdateWrapper<SysUserPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUserPO::getId, id);
        wrapper.set(SysUserPO::getPassword, encodedPassword);
        wrapper.set(SysUserPO::getUpdater, updater);
        wrapper.set(SysUserPO::getUpdateTime, LocalDateTime.now());
        return userMapper.update(wrapper) == 1;
    }
}
