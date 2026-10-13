package cn.caldm.www.permission_context.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class UserRoleRelation {
    private Long userId;
    private Long roleId;
    private String creator;
    private LocalDateTime createTime;
}
