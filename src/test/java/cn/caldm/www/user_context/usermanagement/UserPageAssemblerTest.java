package cn.caldm.www.user_context.usermanagement;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import cn.caldm.www.user_context.domain.model.SysUser;
import cn.caldm.www.user_context.domain.model.SysUserStatusEnum;
import cn.caldm.www.user_context.domain.model.UserRole;

/**
 * 用户分页行 DTO 装配回归钉：白名单字段映射、敏感字段不出域、空输入防御。
 */
public class UserPageAssemblerTest {

    private final UserPageAssembler assembler = new UserPageAssembler();

    @Test
    void toDTO_shouldReturnNullForNullInput() {
        assertThat(assembler.toDTO(null)).isNull();
    }

    @Test
    void toDTO_shouldMapWhitelistFieldsOnly() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("alice");
        user.setPassword("secret");
        user.setNickname("爱丽丝");
        user.setEmail("alice@example.com");
        user.setAvatar("avatar.png");
        user.setStatus(SysUserStatusEnum.NORMAL);
        user.setRoles(List.of(new UserRole(1L, "AUTHOR")));
        user.setCreateTime(java.time.LocalDateTime.of(2026, 1, 1, 12, 0));

        UserPageDTO dto = assembler.toDTO(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUsername()).isEqualTo("alice");
        assertThat(dto.getNickname()).isEqualTo("爱丽丝");
        assertThat(dto.getEmail()).isEqualTo("alice@example.com");
        assertThat(dto.getAvatar()).isEqualTo("avatar.png");
        assertThat(dto.getStatus()).isEqualTo(SysUserStatusEnum.NORMAL);
        assertThat(dto.getRoles()).extracting(UserRole::getCode).containsExactly("AUTHOR");
        assertThat(dto.getCreateTime()).isEqualTo(java.time.LocalDateTime.of(2026, 1, 1, 12, 0));
        // 敏感字段不存在于 DTO 类型上，由白名单投影保证（password 不可达）
        assertThat(UserPageDTO.class.getDeclaredFields())
                .extracting(java.lang.reflect.Field::getName)
                .doesNotContain("password", "deleted", "loginIp", "loginDate");
    }

    @Test
    void toDTOList_shouldReturnEmptyForNullOrEmptyInput() {
        assertThat(assembler.toDTOList(null)).isEmpty();
        assertThat(assembler.toDTOList(List.of())).isEmpty();
    }

    @Test
    void toDTOList_shouldMapAllUsers() {
        SysUser alice = new SysUser();
        alice.setId(1L);
        alice.setUsername("alice");
        SysUser bob = new SysUser();
        bob.setId(2L);
        bob.setUsername("bob");

        List<UserPageDTO> dtos = assembler.toDTOList(List.of(alice, bob));

        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting(UserPageDTO::getUsername).containsExactly("alice", "bob");
    }
}
