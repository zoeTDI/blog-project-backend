package cn.caldm.www.user_context.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 *
 *
 * @author caldm
 */
@Data
@TableName("system_user_role")
public class SysUserRolePO {
    private Long userId;
    private Long roleId;
    private String creator;
    private LocalDateTime createTime;
}