package cn.caldm.www.user_context.usermanagement;

import cn.caldm.www.user_context.domain.model.SysUserStatusEnum;
import cn.caldm.www.user_context.domain.model.UserRole;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建用户响应 DTO。字段白名单投影，password 等敏感字段永不进入。
 *
 * @author caldm
 */
@Data
public class UserCreateRespDTO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatar;
    private SysUserStatusEnum status;
    private List<UserRole> roles = new ArrayList<>();
    private LocalDateTime createTime;
}
