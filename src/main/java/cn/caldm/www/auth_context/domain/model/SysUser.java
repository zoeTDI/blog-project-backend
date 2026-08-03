package cn.caldm.www.auth_context.domain.model;

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
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 盐
     */
    private String salt;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态（0正常 1停用）
     */
    private short status;

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
    private Boolean deleted;
}
