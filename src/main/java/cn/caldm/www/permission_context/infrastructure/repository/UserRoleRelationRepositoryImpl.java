package cn.caldm.www.permission_context.infrastructure.repository;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.domain.repository.UserRoleRelationRepository;
import cn.caldm.www.permission_context.infrastructure.persistence.assembler.RoleAssembler;
import cn.caldm.www.permission_context.infrastructure.persistence.mapper.RoleMapper;
import cn.caldm.www.permission_context.infrastructure.persistence.po.RolePO;
import cn.caldm.www.shared_kernel.security.SecurityUtils;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRoleRelationRepositoryImpl implements UserRoleRelationRepository {

    private final RoleMapper roleMapper;
    private final RoleAssembler roleAssembler;

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        Assert.notNull(userId, "UserId must not be null");
        List<RolePO> pos = roleMapper.selectRolesByUserId(userId);
        return List.copyOf(roleAssembler.toDomainList(pos));
    }

    @Override
    public void assignRoleToUser(Long userId, Long roleId) {
        Assert.notNull(userId, "UserId must not be null");
        Assert.notNull(roleId, "RoleId must not be null");
        String creator = SecurityUtils.getUsername();
        roleMapper.insertUserRole(userId, roleId, creator);

    }

    @Override
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        Assert.notNull(userId, "UserId must not be null");
        Assert.notEmpty(roleIds, "RoleIds must not be empty");
        List<Long> filteredList = roleIds.stream().filter(Objects::nonNull).toList();
        String creator = SecurityUtils.getUsername();
        roleMapper.insertUserRoles(userId, filteredList, creator);
    }

    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        Assert.notNull(userId, "UserId must not be null");
        Assert.notNull(roleId, "RoleId must not be null");
        roleMapper.deleteUserRole(userId, roleId);
    }

    @Override
    public void removeRolesFromUser(Long userId, List<Long> roleIds) {
        Assert.notNull(userId, "UserId must not be null");
        Assert.notEmpty(roleIds, "RoleIds must not be empty");
        List<Long> filteredList = roleIds.stream().filter(Objects::nonNull).toList();
        roleMapper.deleteUserRoles(userId, filteredList);
    }

    @Override
    public List<Role> getRolesByCodes(List<String> codes) {
        Assert.isNull(codes, "Codes must not be null");
        Assert.notEmpty(codes, "Codes must contain element");
        List<RolePO> pos = roleMapper.selectList(
                Wrappers.<RolePO>lambdaQuery()
                        .in(RolePO::getCode, codes));
        List<Role> domains = roleAssembler.toDomainList(pos);
        return List.copyOf(domains);
    }

}
