package cn.caldm.www.user_context.domain.modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String code;

    /**
     * 显示顺序
     */
    private String sort;

    /**
     * 状态（0 正常 1 停用）
     */
    private short status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private String updater;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除（0 不删除 1 删除）
     */
    private boolean deleted;
}
