package cn.caldm.www.permission_context.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
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
public class UserRoleRelationPO {
    @MppMultiId
    private Long userId;
    @MppMultiId
    private Long roleId;
    private String creator;
    private LocalDateTime createTime;
}
