package cn.caldm.www.user_context.usermanagement;

import cn.caldm.www.user_context.domain.model.SysUser;
import org.springframework.stereotype.Component;

/**
 * 创建用户响应装配器：SysUser -> UserCreateRespDTO 白名单映射。
 *
 * @author caldm
 */
@Component
public class UserCreateRespAssembler {

    public UserCreateRespDTO toDTO(SysUser domain) {
        if (domain == null) {
            return null;
        }

        UserCreateRespDTO dto = new UserCreateRespDTO();
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
}
