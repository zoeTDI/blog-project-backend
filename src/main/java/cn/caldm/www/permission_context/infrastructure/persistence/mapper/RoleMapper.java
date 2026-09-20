package cn.caldm.www.permission_context.infrastructure.persistence.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.caldm.www.permission_context.infrastructure.persistence.po.RolePO;
import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface RoleMapper extends BaseMapper<RolePO> {

        @Select("SELECT r.* FROM `system_role` r "
                        + "JOIN `system_user_role` ur ON r.id = ur.role_id "
                        + "WHERE ur.user_id = #{userId} AND r.deleted = 0")
        List<RolePO> selectRolesByUserId(@Param("userId") Long userId);

        @Insert("INSERT IGNORE INTO `system_user_role` (`user_id`, `role_id`, `creator`) "
                        + "VALUES(#{userId}, #{roleId}), #{creator}")
        int insertUserRole(@Param("userId") Long userId,
                        @Param("roleId") Long roleId,
                        @Param("creator") String creator);

        @Insert("<script>"
                        + "INSERT IGNORE INTO `system_user_role` (`user_id`, `role_id`) VALUES "
                        + "<foreach collection='roleIds' item='roleId' separator=','>"
                        + "(#{userId}, #{roleId})"
                        + "</foreach>"
                        + "</script>")
        int insertUserRoles(@Param("userId") Long userId,
                        @Param("roleIds") List<Long> roleIds,
                        @Param("creator") String creator);

        @Delete("DELETE FROM `system_user_role` "
                        + "WHERE `user_id` = ${userId} AND `role_id` = #{roleId}")
        int deleteUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

        @Delete("<script>"
                        + "DELETE FROM `system_user_role` "
                        + "WHERE `user_id` = #{userId} "
                        + "AND `role_id` IN "
                        + "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>"
                        + "#{roleId}"
                        + "</foreach>"
                        + "</script>")
        int deleteUserRoles(@Param("userId") Long userId,
                        @Param("roleIds") List<Long> roleIds);
}
