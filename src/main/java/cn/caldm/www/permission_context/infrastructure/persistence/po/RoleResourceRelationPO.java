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
@TableName("system_role_resource")
public class RoleResourceRelationPO {
    @MppMultiId
    private Long roleId;
    @MppMultiId
    private Long resourceId;
    private String creator;
    private LocalDateTime createTime;
}
