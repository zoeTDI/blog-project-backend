package cn.caldm.www.user_context.infrastructure.persistence.mapper;

import cn.caldm.www.user_context.infrastructure.persistence.po.SysRolePO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRolePO> {

    /**
     * 通过用户ID联表查询其拥有的所有角色信息
     */
    @Select("SELECT r.* FROM `system_role` r "
            + "JOIN `system_user_role` ur ON r.id = ur.role_id "
            + "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<SysRolePO> selectRolesByUserId(Long userId);

    /**
     * 根据角色 Code 集合批量查询
     */
    @Select("<script>" +
            "SELECT * FROM `system_role` WHERE code IN " +
            "<foreach item='code' collection='codes' open='(' separator=',' close=')'>" +
            "#{code}" +
            "</foreach>" +
            " AND deleted = 0" +
            "</script>")
    List<SysRolePO> selectByCodes(@Param("codes") List<String> codes);
}
