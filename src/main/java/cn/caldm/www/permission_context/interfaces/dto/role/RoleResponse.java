package cn.caldm.www.permission_context.interfaces.dto.role;

import lombok.Data;

/**
 * 角色响应 DTO（白名单字段，与领域模型解耦）。
 *
 * @author caldm
 */
@Data
public class RoleResponse {
    private Long id;
    private String name;
    private String code;
    private int sort;
    private String remark;
    private boolean enabled;
}
