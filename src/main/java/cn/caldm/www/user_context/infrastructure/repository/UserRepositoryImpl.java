package cn.caldm.www.user_context.infrastructure.repository;

import cn.caldm.www.user_context.domain.model.*;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import cn.caldm.www.user_context.infrastructure.persistence.mapper.SysUserMapper;
import cn.caldm.www.user_context.infrastructure.persistence.mapper.SysUserRoleMapper;
import cn.caldm.www.user_context.infrastructure.persistence.po.SysUserPO;
import cn.caldm.www.user_context.infrastructure.persistence.po.SysUserRolePO;
import cn.caldm.www.user_context.infrastructure.persistence.assembler.UserAssembler;
import lombok.RequiredArgsConstructor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    private final SysUserRoleMapper userRoleMapper;
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
    public SysUser insert(String creator, String username, String password, List<UserRole> roles) {
        SysUserPO sysUserPO = new SysUserPO();
        sysUserPO.setUsername(username);
        sysUserPO.setPassword(password);
        sysUserPO.setStatus(SysUserStatusEnum.NORMAL);
        sysUserPO.setDeleted(SysUserDeletedEnum.NORMAL);
        sysUserPO.setCreator(creator);
        sysUserPO.setCreateTime(LocalDateTime.now());
        sysUserPO.setUpdater(creator);
        sysUserPO.setUpdateTime(LocalDateTime.now());

        sysUserPO.setNickname(username);
        sysUserPO.setEmail(username + "example@xx.com");

        int inserted = userMapper.insert(sysUserPO);
        if (inserted != 1 || sysUserPO.getId() == null) {
            return null;
        }

        // 写入关联表
        if (roles != null && !roles.isEmpty()) {
            List<Long> roleIds = roles.stream()
                    .filter(Objects::nonNull)
                    .map(UserRole::getId)
                    .toList();

            if (!roleIds.isEmpty()) {
                for (Long roleId : roleIds) {
                    SysUserRolePO userRolePO = new SysUserRolePO();
                    userRolePO.setUserId(sysUserPO.getId());
                    userRolePO.setRoleId(roleId);
                    userRolePO.setCreator(creator);
                    userRolePO.setCreateTime(LocalDateTime.now());

                    userRoleMapper.insert(userRolePO);
                }
            }
        }

        SysUser sysUser = userAssembler.toDomain(sysUserPO);
        sysUser.setRoles(roles);
        return sysUser;
    }

    @Override
    public boolean update(SysUser sysUser) {
        SysUserPO po = userAssembler.toPO(sysUser);
        int i = userMapper.updateById(po);
        return i == 1;
    }
}
