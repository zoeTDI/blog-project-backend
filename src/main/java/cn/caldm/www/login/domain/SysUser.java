package cn.caldm.www.login.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 * 用户实体表
 *
 * @author caldm
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("system_user")
public class SysUser {
    /**
     * 用户主键
     */
    @TableId(value = "id", type = IdType.AUTO)
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
