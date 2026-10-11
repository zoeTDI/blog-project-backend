package cn.caldm.www.permission_context.infrastructure.persistence.mapper;

import cn.caldm.www.permission_context.infrastructure.persistence.po.UserRoleRelationPO;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleRelationMapper extends MppBaseMapper<UserRoleRelationPO> {
    @Insert("<script>"
            + "INSERT IGNORE INTO `system_user_role` (`user_id`, `role_id`, `creator`) VALUES "
            + "<foreach collection='roleIds' item='roleId' separator=','>"
            + "(#{userId}, #{roleId}, #{creator})"
            + "</foreach>"
            + "</script>")
    int batchInsertUserRoles(@Param("userId") Long userId,
                             @Param("roleIds") List<Long> roleIds,
                             @Param("creator") String creator);
}
