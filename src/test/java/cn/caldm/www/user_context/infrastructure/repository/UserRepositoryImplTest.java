package cn.caldm.www.user_context.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.caldm.www.shared_kernel.domain.PageResult;
import cn.caldm.www.user_context.domain.model.SysUser;
import cn.caldm.www.user_context.infrastructure.persistence.assembler.UserAssembler;
import cn.caldm.www.user_context.infrastructure.persistence.mapper.SysUserMapper;
import cn.caldm.www.user_context.infrastructure.persistence.po.SysUserPO;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryImplTest {

    @Mock
    private SysUserMapper userMapper;
    @Mock
    private UserAssembler userAssembler;
    @InjectMocks
    private UserRepositoryImpl userRepository;

    @Test
    void findPage_shouldMapPageResult() {
        SysUserPO po1 = new SysUserPO();
        po1.setId(1L);
        SysUserPO po2 = new SysUserPO();
        po2.setId(2L);
        Page<SysUserPO> dbPage = new Page<>(2, 10);
        dbPage.setRecords(List.of(po1, po2));
        dbPage.setTotal(25);

        SysUser u1 = new SysUser();
        u1.setId(1L);
        SysUser u2 = new SysUser();
        u2.setId(2L);

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(dbPage);
        when(userAssembler.toDomainList(List.of(po1, po2))).thenReturn(List.of(u1, u2));

        PageResult<SysUser> result = userRepository.findPage(2, 10);

        assertThat(result.getRecords()).containsExactly(u1, u2);
        assertThat(result.getTotal()).isEqualTo(25);
        assertThat(result.getPage()).isEqualTo(2);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getPages()).isEqualTo(3);
    }

    @Test
    void findPage_shouldReturnEmptyRecords_whenNoRows() {
        Page<SysUserPO> dbPage = new Page<>(1, 10);
        dbPage.setRecords(List.of());
        dbPage.setTotal(0);

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(dbPage);
        when(userAssembler.toDomainList(anyList())).thenReturn(List.of());

        PageResult<SysUser> result = userRepository.findPage(1, 10);

        assertThat(result.getRecords()).isEmpty();
        assertThat(result.getTotal()).isZero();
        assertThat(result.getPages()).isZero();
    }
}
