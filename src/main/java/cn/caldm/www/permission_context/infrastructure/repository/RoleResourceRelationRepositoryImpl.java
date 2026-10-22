package cn.caldm.www.permission_context.infrastructure.repository;

import cn.caldm.www.permission_context.domain.repository.RoleResourceRelationRepository;
import cn.caldm.www.permission_context.infrastructure.persistence.mapper.RoleResourceRelationMapper;
import cn.caldm.www.permission_context.infrastructure.persistence.po.RoleResourceRelationPO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 *
 *
 *
 * @author caldm
 */
@Repository
@RequiredArgsConstructor
public class RoleResourceRelationRepositoryImpl implements RoleResourceRelationRepository {
    private final RoleResourceRelationMapper roleResourceRelationMapper;

    @Override
    public void grant(Long roleId, Long resourceId) {
        RoleResourceRelationPO po = new RoleResourceRelationPO();
        po.setRoleId(roleId);
        po.setResourceId(resourceId);
        roleResourceRelationMapper.insert(po);
    }

    @Override
    public void revoke(Long roleId, Long resourceId) {
        roleResourceRelationMapper.delete(new LambdaQueryWrapper<RoleResourceRelationPO>()
                .eq(RoleResourceRelationPO::getRoleId, roleId)
                .eq(RoleResourceRelationPO::getResourceId, resourceId)
        );
    }

    @Override
    public List<Long> findResourceIdsByRoleId(Long roleId) {
        return roleResourceRelationMapper.selectList(new LambdaQueryWrapper<RoleResourceRelationPO>()
                        .select(RoleResourceRelationPO::getResourceId)
                        .eq(RoleResourceRelationPO::getRoleId, roleId)
                )
                .stream()
                .map(RoleResourceRelationPO::getResourceId)
                .toList();
    }

    @Override
    public List<Long> findResourceIdsByRoleIds(List<Long> roleIds) {
        Assert.notNull(roleIds, "The list of role id must be not null.");
        List<Long> filtered = roleIds.stream().filter(Objects::nonNull).toList();
        LambdaQueryWrapper<RoleResourceRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RoleResourceRelationPO::getRoleId, filtered);
        List<RoleResourceRelationPO> relationPOS = roleResourceRelationMapper.selectList(wrapper);
        return relationPOS.stream().map(RoleResourceRelationPO::getResourceId).toList();
    }

    @Override
    public boolean exists(Long roleId, Long resourceId) {
        return roleResourceRelationMapper.selectCount(new LambdaQueryWrapper<RoleResourceRelationPO>()
                .eq(RoleResourceRelationPO::getRoleId, roleId)
                .eq(RoleResourceRelationPO::getResourceId, resourceId)
        ) > 0;
    }
}
