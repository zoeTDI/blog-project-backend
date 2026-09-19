package cn.caldm.www.user_context.domain.modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *
 * @author caldm
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SysUser {
    /**
     * 用户主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（密文）
     */
    private String password;

    /**
     * 用户角色
     */
    private List<RoleEnum> roles = new ArrayList<>();

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态（0正常 1停用）
     */
    private SysUserStatusEnum status;

    /**
     * 上次登录ip
     */
    private String loginIp;

    /**
     * 上次登录时间
     */
    private LocalDateTime loginDate;

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
     * 上次更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除（0 不删除 1 删除）
     */
    private SysUserDeletedEnum deleted;

    /**
     * 判断当前用户是否具备某个角色
     * @param targetRole 目标角色
     * @return 是否拥有该角色
     */
    public boolean hasRole(RoleEnum targetRole) {
        if (roles == null || targetRole == null) {
            return false;
        }
        for (RoleEnum role : roles) {
            if (role.equalsRole(targetRole)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前用户是否被停用
     *
     * @return true 表示已停用，false 表示未停用（包含状态为 null 的情况）
     */
    public boolean isDisabled() {
        return SysUserStatusEnum.DISABLED.equals(this.status);
    }

    /**
     * 判断当前用户是否已被软删除
     *
     * @return true 表示已删除，false 表示未删除或删除状态为 null
     */
    public boolean isDeleted() {
        return SysUserDeletedEnum.DELETED.equals(this.deleted);
    }
}
