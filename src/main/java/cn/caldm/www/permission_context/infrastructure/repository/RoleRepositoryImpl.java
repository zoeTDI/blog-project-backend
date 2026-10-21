package cn.caldm.www.permission_context.infrastructure.repository;

import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.domain.repository.RoleRepository;
import cn.caldm.www.permission_context.infrastructure.persistence.assembler.RoleAssembler;
import cn.caldm.www.permission_context.infrastructure.persistence.mapper.RoleMapper;
import cn.caldm.www.permission_context.infrastructure.persistence.po.RolePO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper roleMapper;
    private final RoleAssembler roleAssembler;

    @Override
    public Role getRoleById(Long id) {
        if (id == null) {
            return null;
        }
        RolePO po = roleMapper.selectById(id);
        return roleAssembler.toDomain(po);
    }

    @Override
    @Nonnull
    public List<Role> getRolesByIds(List<Long> ids) {
        Assert.notNull(ids, "ids must not be null.");

        List<Long> validIds = ids.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (validIds.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<RolePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RolePO::getId, validIds);
        List<RolePO> pos = roleMapper.selectList(wrapper);
        if (pos.isEmpty()) {
            return List.of();
        }
        List<Role> domainList = roleAssembler.toDomainList(pos);
        return List.copyOf(domainList);
    }

    @Override
    public Role getRoleByCode(String code) {
        if (code == null) {
            return null;
        }
        LambdaQueryWrapper<RolePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePO::getCode, code);
        RolePO po = roleMapper.selectOne(wrapper);
        return roleAssembler.toDomain(po);
    }

    @Override
    public List<Role> getRolesByCodes(List<String> codes) {
        if (codes == null) {
            throw new IllegalArgumentException("The code list must be not null.");
        }
        if (codes.isEmpty()) {
            return List.of();
        }
        List<RolePO> pos = roleMapper.selectList(Wrappers.<RolePO>lambdaQuery()
                .in(RolePO::getCode, codes));
        List<Role> domains = roleAssembler.toDomainList(pos);
        return List.copyOf(domains);
    }

    @Override
    public boolean existsRole(Long roleId) {
        if (roleId == null) {
            return false;
        }
        LambdaQueryWrapper<RolePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePO::getId, roleId);
        return roleMapper.exists(wrapper);
    }

}
