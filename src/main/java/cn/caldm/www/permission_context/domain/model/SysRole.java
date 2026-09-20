package cn.caldm.www.permission_context.domain.model;

import cn.caldm.www.user_context.domain.modal.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *
 *
 * @author caldm
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SysRole {
    /**
     * 角色主键
     */
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色权限代码
     */
    private RoleEnum code;

    /**
     * 显示顺序
     */
    private String sort;

    /**
     * 状态（0 正常 1 停用）
     */
    private SysRoleStatusEnum status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否启用
     */
    private boolean enabled;

    public static SysRole reconstitute(
        Long id,
        String name,
        RoleEnum code,
        String sort,
        SysRoleStatusEnum status,
        String remark,
        boolean enabled
    ) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setName(name);
        role.setCode(code);
        role.setSort(sort);
        role.setStatus(status);
        role.setRemark(remark);
        role.setEnabled(enabled);
        return role;
    }

    public void disable() {
        this.enabled = false;
    }

    public void enable() {
        this.enabled = true;
    }
}
