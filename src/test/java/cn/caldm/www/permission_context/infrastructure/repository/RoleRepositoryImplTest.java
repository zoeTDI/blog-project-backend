package cn.caldm.www.permission_context.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.infrastructure.persistence.assembler.RoleAssembler;
import cn.caldm.www.permission_context.infrastructure.persistence.mapper.RoleMapper;
import cn.caldm.www.permission_context.infrastructure.persistence.po.RolePO;

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

    @Test
    void getRoleById_shouldReturnNull_whenNotFound() {
        when(roleMapper.selectById(99L)).thenReturn(null);
        when(roleAssembler.toDomain(null)).thenReturn(null);

        Role result = roleRepository.getRoleById(99L);

        assertThat(result).isNull();
    }

    @Test
    void getRolesByIds_shouldReturnEmpty_whenIdsEmpty() {
        List<Role> result = roleRepository.getRolesByIds(List.of());

        assertThat(result).isEmpty();
        verify(roleMapper, never()).selectList(any());
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

    // ==================== findAll ====================

    @Test
    void findAll_shouldReturnEmpty_whenNoRows() {
        when(roleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());

        List<Role> result = roleRepository.findAll();

        assertThat(result).isEmpty();
        verify(roleAssembler, never()).toDomainList(any());
    }

    @Test
    void findAll_shouldReturnEmpty_whenMapperReturnsNull() {
        when(roleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(null);

        List<Role> result = roleRepository.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldQueryAndMapAllRoles() {
        RolePO po1 = new RolePO();
        po1.setId(1L);
        RolePO po2 = new RolePO();
        po2.setId(2L);
        List<RolePO> pos = List.of(po1, po2);

        Role admin = new Role();
        admin.setId(1L);
        Role author = new Role();
        author.setId(2L);

        when(roleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(pos);
        when(roleAssembler.toDomainList(pos)).thenReturn(List.of(admin, author));

        List<Role> result = roleRepository.findAll();

        assertThat(result).containsExactly(admin, author);
    }

}
