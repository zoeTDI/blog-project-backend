package cn.caldm.www.user_context.infrastructure.repository;

import cn.caldm.www.user_context.domain.modal.RoleEnum;
import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.domain.modal.SysUserDeletedEnum;
import cn.caldm.www.user_context.domain.modal.SysUserStatusEnum;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import cn.caldm.www.user_context.infrastructure.persistence.mapper.SysRoleMapper;
import cn.caldm.www.user_context.infrastructure.persistence.mapper.SysUserMapper;
import cn.caldm.www.user_context.infrastructure.persistence.mapper.SysUserRoleMapper;
import cn.caldm.www.user_context.infrastructure.persistence.po.SysRolePO;
import cn.caldm.www.user_context.infrastructure.persistence.po.SysUserPO;
import cn.caldm.www.user_context.infrastructure.persistence.po.SysUserRolePO;
import cn.caldm.www.user_context.interfaces.assembler.UserAssembler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Autowired
    SysRoleMapper roleMapper;

    @Autowired
    SysUserRoleMapper userRoleMapper;

    @Autowired
    UserAssembler userAssembler;

    @Override
    public SysUser findByEmail(String email) {
        LambdaQueryWrapper<SysUserPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserPO::getEmail, email);
        SysUserPO po = userMapper.selectOne(queryWrapper);
        if (po == null) {
            return null;
        }

        SysUser sysUser = userAssembler.toDomain(po);
        sysUser.setRoles(getRolesByUserId(sysUser.getId()));
        return sysUser;
    }

    @Override
    public SysUser findByUsername(String username) {
        LambdaQueryWrapper<SysUserPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserPO::getUsername, username);
        SysUserPO po = userMapper.selectOne(queryWrapper);
        if (po == null) {
            return null;
        }

        SysUser sysUser = userAssembler.toDomain(po);
        sysUser.setRoles(getRolesByUserId(sysUser.getId()));
        return sysUser;
    }

    @Override
    public SysUser findById(Long id) {
        SysUserPO po = userMapper.selectById(id);
        if (po == null) {
            return null;
        }

        SysUser sysUser = userAssembler.toDomain(po);
        sysUser.setRoles(getRolesByUserId(sysUser.getId()));
        return sysUser;
    }

    @Override
    public SysUser insert(String creator, String username, String password, List<RoleEnum> roles) {
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
            List<String> roleCodes = roles.stream()
                    .filter(Objects::nonNull)
                    .map(RoleEnum::getCode)
                    .collect(Collectors.toList());

            if (!roleCodes.isEmpty()) {
                List<SysRolePO> rolePOS = roleMapper.selectByCodes(roleCodes);

                if (rolePOS != null && !rolePOS.isEmpty()) {
                    for (SysRolePO rolePO : rolePOS) {
                        SysUserRolePO userRolePO = new SysUserRolePO();
                        userRolePO.setUserId(sysUserPO.getId());
                        userRolePO.setRoleId(rolePO.getId());
                        userRolePO.setCreator(creator);
                        userRolePO.setCreateTime(LocalDateTime.now());

                        userRoleMapper.insert(userRolePO);
                    }
                }
            }
        }

        SysUser sysUser = userAssembler.toDomain(sysUserPO);
        sysUser.setRoles(roles);
        return sysUser;
    }

    @Override
    public boolean update(SysUserPO sysUserPO) {
        int i = userMapper.updateById(sysUserPO);
        return i == 1;
    }

    private List<RoleEnum> getRolesByUserId(Long userId) {
        List<SysRolePO> rolePOS = roleMapper.selectRolesByUserId(userId);
        if (rolePOS != null && !rolePOS.isEmpty()) {
            return rolePOS.stream()
                    .map(rolePO -> RoleEnum.fromCode(rolePO.getCode()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    // todo 添加用户菜单
}
