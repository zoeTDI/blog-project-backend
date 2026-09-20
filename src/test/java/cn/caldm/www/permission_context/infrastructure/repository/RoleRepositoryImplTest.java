package cn.caldm.www.permission_context.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.infrastructure.persistence.assembler.RoleAssembler;
import cn.caldm.www.permission_context.infrastructure.persistence.mapper.RoleMapper;
import cn.caldm.www.permission_context.infrastructure.persistence.po.RolePO;
import cn.caldm.www.shared_kernel.security.SecurityUtils;

@ExtendWith(MockitoExtension.class)
public class RoleRepositoryImplTest {
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private RoleAssembler roleAssembler;
    @InjectMocks
    private RoleRepositoryImpl roleRepository;

// ==================== getRoleById ====================

    @Test
    void getRoleById_shouldReturnNull_whenIdIsNull() {
        Role result = roleRepository.getRoleById(null);

        assertThat(result).isNull();
        verify(roleMapper, never()).selectById(any());
    }

    @Test
    void getRoleById_shouldReturnMappedRole() {
        RolePO po = new RolePO();
        po.setId(1L);
        Role domain = new Role();
        domain.setId(1L);

        when(roleMapper.selectById(1L)).thenReturn(po);
        when(roleAssembler.toDomain(po)).thenReturn(domain);

        Role result = roleRepository.getRoleById(1L);

        assertThat(result).isSameAs(domain);
        verify(roleMapper).selectById(1L);
    }

    // ==================== getRoleByCode ====================

    @Test
    void getRoleByCode_shouldReturnNull_whenCodeIsNull() {
        Role result = roleRepository.getRoleByCode(null);

        assertThat(result).isNull();
        verify(roleMapper, never()).selectOne(any());
    }

    @Test
    void getRoleByCode_shouldQueryByCodeAndMap() {
        RolePO po = new RolePO();
        po.setId(1L);
        po.setCode("ADMIN");
        Role domain = new Role();
        domain.setId(1L);
        domain.setCode("ADMIN");

        when(roleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(po);
        when(roleAssembler.toDomain(po)).thenReturn(domain);

        Role result = roleRepository.getRoleByCode("ADMIN");

        assertThat(result).isSameAs(domain);
    }

    // ==================== getRoleMap ====================

    @Test
    void getRoleMap_shouldReturnCodeToRoleMap() {
        RolePO po1 = new RolePO();
        RolePO po2 = new RolePO();
        List<RolePO> pos = List.of(po1, po2);

        Role r1 = new Role();
        r1.setCode("ADMIN");
        Role r2 = new Role();
        r2.setCode("USER");

        when(roleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(pos);
        when(roleAssembler.toDomainList(pos)).thenReturn(List.of(r1, r2));

        var map = roleRepository.getRoleMap();

        assertThat(map).containsOnlyKeys("ADMIN", "USER");
        assertThat(map.get("ADMIN")).isSameAs(r1);
        assertThat(map.get("USER")).isSameAs(r2);
    }

    @Test
    void getRoleMap_shouldFilterNullRoles() {
        RolePO po = new RolePO();
        Role r1 = new Role();
        r1.setCode("ADMIN");

        when(roleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(po));
        // 故意在列表里塞 null，验证 filter 生效
        when(roleAssembler.toDomainList(List.of(po))).thenReturn(java.util.Arrays.asList(r1, null));

        var map = roleRepository.getRoleMap();

        assertThat(map).containsOnlyKeys("ADMIN");
    }

    // ==================== saveRole ====================

    @Test
    void saveRole_shouldDoNothing_whenRoleIsNull() {
        roleRepository.saveRole(null);

        verify(roleMapper, never()).insert(any());
    }

    @Test
    void saveRole_shouldSetAuditFieldsAndInsert() {
        Role role = new Role();
        role.setCode("ADMIN");

        RolePO po = new RolePO();
        when(roleAssembler.toPO(role)).thenReturn(po);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getUsername).thenReturn("tester");
            roleRepository.saveRole(role);
        }

        assertThat(po.getCreator()).isEqualTo("tester");
        assertThat(po.getUpdater()).isEqualTo("tester");
        assertThat(po.getCreateTime()).isNotNull();
        assertThat(po.getUpdateTime()).isNotNull();

        ArgumentCaptor<RolePO> captor = ArgumentCaptor.forClass(RolePO.class);
        verify(roleMapper).insert(captor.capture());
        assertThat(captor.getValue()).isSameAs(po);
    }

    // ==================== updateRole ====================

    @Test
    void updateRole_shouldDoNothing_whenRoleIsNull() {
        roleRepository.updateRole(null);
        verify(roleMapper, never()).updateById(any());
    }

    @Test
    void updateRole_shouldDoNothing_whenIdIsNull() {
        Role role = new Role(); // id = null
        roleRepository.updateRole(role);
        verify(roleMapper, never()).updateById(any());
    }

    @Test
    void updateRole_shouldSetAuditFieldsAndUpdate() {
        Role role = new Role();
        role.setId(1L);

        RolePO po = new RolePO();
        when(roleAssembler.toPO(role)).thenReturn(po);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getUsername).thenReturn("tester");
            roleRepository.updateRole(role);
        }

        assertThat(po.getUpdater()).isEqualTo("tester");
        assertThat(po.getUpdateTime()).isNotNull();
        // update 不应覆写 creator/createTime
        assertThat(po.getCreator()).isNull();
        assertThat(po.getCreateTime()).isNull();

        verify(roleMapper).updateById(po);
    }

    // ==================== deleteRole ====================

    @Test
    void deleteRole_shouldCallDeleteById() {
        roleRepository.deleteRole(100L);

        verify(roleMapper).deleteById(100L);
    }
}
