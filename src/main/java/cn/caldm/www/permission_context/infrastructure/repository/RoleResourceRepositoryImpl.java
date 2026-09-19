package cn.caldm.www.permission_context.infrastructure.repository;

import cn.caldm.www.permission_context.domain.repository.RoleResourceRepository;
import cn.caldm.www.permission_context.infrastructure.persistence.mapper.RoleResourceRelationMapper;
import cn.caldm.www.permission_context.infrastructure.persistence.po.RoleResourceRelationPO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 *
 *
 * @author caldm
 */
@Repository
@RequiredArgsConstructor
public class RoleResourceRepositoryImpl implements RoleResourceRepository {
    private final RoleResourceRelationMapper mapper;

    @Override
    public void grant(Long roleId, Long resourceId) {
        RoleResourceRelationPO po = new RoleResourceRelationPO();
        po.setRoleId(roleId);
        po.setResourceId(resourceId);
        mapper.insert(po);
    }

    @Override
    public void revoke(Long roleId, Long resourceId) {
        mapper.delete(new LambdaQueryWrapper<RoleResourceRelationPO>()
                .eq(RoleResourceRelationPO::getRoleId, roleId)
                .eq(RoleResourceRelationPO::getResourceId, resourceId)
        );
    }

    @Override
    public void revokeAllByResourceId(Long resourceId) {
        mapper.delete(new LambdaQueryWrapper<RoleResourceRelationPO>().eq(RoleResourceRelationPO::getResourceId, resourceId));
    }

    @Override
    public void revokeAllByRoleId(Long roleId) {
        mapper.delete(new LambdaQueryWrapper<RoleResourceRelationPO>().eq(RoleResourceRelationPO::getRoleId, roleId));
    }

    @Override
    public List<Long> findResourceIdsByRoleId(Long roleId) {
        return mapper.selectList(new LambdaQueryWrapper<RoleResourceRelationPO>()
                        .select(RoleResourceRelationPO::getResourceId)
                        .eq(RoleResourceRelationPO::getRoleId, roleId)
                )
                .stream()
                .map(RoleResourceRelationPO::getResourceId)
                .toList();
    }

    @Override
    public List<Long> findRoleIdsByResourceId(Long resourceId) {
        return mapper.selectList(new LambdaQueryWrapper<RoleResourceRelationPO>()
                        .select(RoleResourceRelationPO::getRoleId)
                        .eq(RoleResourceRelationPO::getResourceId, resourceId)
                )
                .stream()
                .map(RoleResourceRelationPO::getRoleId)
                .toList();
    }

    @Override
    public boolean exists(Long roleId, Long resourceId) {
        return mapper.selectCount(new LambdaQueryWrapper<RoleResourceRelationPO>()
                .eq(RoleResourceRelationPO::getRoleId, roleId)
                .eq(RoleResourceRelationPO::getResourceId, resourceId)
        ) > 0;
    }
}
