package cn.caldm.www.permission_context.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import cn.caldm.www.permission_context.domain.model.UserRoleRelation;
import cn.caldm.www.permission_context.infrastructure.persistence.assembler.UserRoleRelationAssembler;
import cn.caldm.www.permission_context.infrastructure.persistence.mapper.UserRoleRelationMapper;
import cn.caldm.www.permission_context.infrastructure.persistence.po.UserRoleRelationPO;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import cn.caldm.www.permission_context.domain.repository.UserRoleRelationRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRoleRelationRepositoryImpl implements UserRoleRelationRepository {

    private final UserRoleRelationMapper userRoleRelationMapper;
    private final UserRoleRelationAssembler userRoleRelationAssembler;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public List<UserRoleRelation> getRoleRelationByUserId(Long userId) {
        Assert.notNull(userId, "UserId must not be null.");

        LambdaQueryWrapper<UserRoleRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoleRelationPO::getUserId, userId);
        List<UserRoleRelationPO> pos = userRoleRelationMapper.selectList(wrapper);
        if (pos.isEmpty()) {
            return List.of();
        }
        List<UserRoleRelation> domains = userRoleRelationAssembler.toDomainList(pos);
        return List.copyOf(domains);

    }

    @Override
    public void assignRoleToUser(Long userId, Long roleId) {
        Assert.notNull(userId, "UserId must not be null");
        Assert.notNull(roleId, "RoleId must not be null");
        String creator = currentUserProvider.getUsername();
        UserRoleRelationPO po = new UserRoleRelationPO();
        po.setUserId(userId);
        po.setRoleId(roleId);
        po.setCreator(creator);
        po.setCreateTime(LocalDateTime.now());
        userRoleRelationMapper.insert(po);

    }

    @Override
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        Assert.notNull(userId, "UserId must not be null");
        Assert.notEmpty(roleIds, "RoleIds must not be empty");
        List<Long> filteredList = roleIds.stream().filter(Objects::nonNull).toList();
        String creator = currentUserProvider.getUsername();
        userRoleRelationMapper.batchInsertUserRoles(userId, filteredList, creator);
    }

    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        Assert.notNull(userId, "UserId must not be null");
        Assert.notNull(roleId, "RoleId must not be null");
        UserRoleRelationPO po = new UserRoleRelationPO();
        po.setUserId(userId);
        po.setRoleId(roleId);
        userRoleRelationMapper.deleteByMultiId(po);
    }

    @Override
    public void removeRolesFromUser(Long userId, List<Long> roleIds) {
        Assert.notNull(userId, "UserId must not be null");
        Assert.notEmpty(roleIds, "RoleIds must not be empty");
        List<Long> filteredList = roleIds.stream().filter(Objects::nonNull).toList();
        LambdaQueryWrapper<UserRoleRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoleRelationPO::getUserId, userId);
        wrapper.in(UserRoleRelationPO::getRoleId, filteredList);
        userRoleRelationMapper.delete(wrapper);
    }
}
