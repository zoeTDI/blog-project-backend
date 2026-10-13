package cn.caldm.www.permission_context.domain.model;

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
public class Role {
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
    private String code;

    /**
     * 显示顺序
     */
    private String sort;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否启用
     */
    private boolean enabled;

    public static Role reconstitute(
        Long id,
        String name,
        String code,
        String sort,
        String remark,
        boolean enabled
    ) {
        Role role = new Role();
        role.setId(id);
        role.setName(name);
        role.setCode(code);
        role.setSort(sort);
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

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isValid() {
        return id != null && name != null && !name.isBlank() && code != null && !code.isBlank() && sort != null && !sort.isBlank();
    }
}
