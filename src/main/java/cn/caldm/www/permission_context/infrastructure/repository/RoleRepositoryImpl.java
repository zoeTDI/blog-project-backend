package cn.caldm.www.permission_context.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.domain.model.RoleDeletedEnum;
import cn.caldm.www.permission_context.domain.repository.RoleRepository;
import cn.caldm.www.permission_context.infrastructure.persistence.assembler.RoleAssembler;
import cn.caldm.www.permission_context.infrastructure.persistence.mapper.RoleMapper;
import cn.caldm.www.permission_context.infrastructure.persistence.po.RolePO;
import cn.caldm.www.shared_kernel.security.SecurityUtils;
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
    public Map<String, Role> getRoleMap() {
        LambdaQueryWrapper<RolePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePO::getDeleted, RoleDeletedEnum.NORMAL);
        List<RolePO> pos = roleMapper.selectList(wrapper);
        List<Role> domains = roleAssembler.toDomainList(pos)
                .stream()
                .filter(role -> role != null)
                .toList();
        HashMap<String, Role> map = new HashMap<>();
        domains.stream()
                .forEach(role -> map.put(role.getCode(), role));
        return map;
    }

    @Override
    public void saveRole(Role role) {
        if (role == null) {
            return;
        }
        RolePO po = roleAssembler.toPO(role);
        String operatorName = SecurityUtils.getUsername();
        po.setCreator(operatorName);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdater(operatorName);
        po.setUpdateTime(LocalDateTime.now());
        roleMapper.insert(po);
    }

    @Override
    public void updateRole(Role newRole) {
        if (newRole == null || newRole.getId() == null) {
            return;
        }

        RolePO po = roleAssembler.toPO(newRole);
        String operatorName = SecurityUtils.getUsername();
        po.setUpdater(operatorName);
        po.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(po);
    }

    @Override
    public void deleteRole(Long id) {
        roleMapper.deleteById(id);
    }
}
