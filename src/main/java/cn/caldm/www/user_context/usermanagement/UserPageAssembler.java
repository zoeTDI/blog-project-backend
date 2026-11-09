package cn.caldm.www.user_context.usermanagement;

import cn.caldm.www.user_context.domain.model.SysUser;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户分页行装配器：SysUser -> UserPageDTO 白名单映射。
 *
 * @author caldm
 */
@Component
public class UserPageAssembler {

    public UserPageDTO toDTO(SysUser domain) {
        if (domain == null) {
            return null;
        }

        UserPageDTO dto = new UserPageDTO();
        dto.setId(domain.getId());
        dto.setUsername(domain.getUsername());
        dto.setNickname(domain.getNickname());
        dto.setEmail(domain.getEmail());
        dto.setAvatar(domain.getAvatar());
        dto.setStatus(domain.getStatus());
        dto.setRoles(domain.getRoles());
        dto.setCreateTime(domain.getCreateTime());
        return dto;
    }

    public List<UserPageDTO> toDTOList(List<SysUser> domains) {
        if (domains == null || domains.isEmpty()) {
            return List.of();
        }
        return domains.stream()
                .map(this::toDTO)
                .toList();
    }
}
