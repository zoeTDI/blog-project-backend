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

import cn.caldm.www.permission_context.domain.model.UserRoleRelation;
import cn.caldm.www.permission_context.infrastructure.persistence.assembler.UserRoleRelationAssembler;
import cn.caldm.www.permission_context.infrastructure.persistence.mapper.UserRoleRelationMapper;
import cn.caldm.www.permission_context.infrastructure.persistence.po.UserRoleRelationPO;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
public class UserRoleRelationRepositoryImplTest {

    @Mock
    private UserRoleRelationMapper userRoleRelationMapper;
    @Mock
    private UserRoleRelationAssembler userRoleRelationAssembler;
    @Mock
    private CurrentUserProvider currentUserProvider;
    @InjectMocks
    private UserRoleRelationRepositoryImpl userRoleRelationRepository;

    @Test
    void getRoleRelationsByUserIds_shouldReturnEmpty_whenUserIdsEmpty() {
        assertThat(userRoleRelationRepository.getRoleRelationsByUserIds(List.of())).isEmpty();
        verify(userRoleRelationMapper, never()).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void getRoleRelationsByUserIds_shouldReturnEmpty_whenNoRows() {
        when(userRoleRelationMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());

        assertThat(userRoleRelationRepository.getRoleRelationsByUserIds(List.of(100L))).isEmpty();
    }

    @Test
    void getRoleRelationsByUserIds_shouldQueryAndMap() {
        UserRoleRelationPO po1 = new UserRoleRelationPO();
        po1.setUserId(100L);
        po1.setRoleId(1L);
        UserRoleRelationPO po2 = new UserRoleRelationPO();
        po2.setUserId(200L);
        po2.setRoleId(2L);
        List<UserRoleRelationPO> pos = List.of(po1, po2);

        UserRoleRelation domain1 = new UserRoleRelation();
        domain1.setUserId(100L);
        domain1.setRoleId(1L);
        UserRoleRelation domain2 = new UserRoleRelation();
        domain2.setUserId(200L);
        domain2.setRoleId(2L);

        when(userRoleRelationMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(pos);
        when(userRoleRelationAssembler.toDomainList(pos)).thenReturn(List.of(domain1, domain2));

        List<UserRoleRelation> result = userRoleRelationRepository.getRoleRelationsByUserIds(List.of(100L, 200L));

        assertThat(result).containsExactly(domain1, domain2);
        verify(userRoleRelationMapper).selectList(any(LambdaQueryWrapper.class));
    }
}
