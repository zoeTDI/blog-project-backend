package cn.caldm.www.permission_context.infrastructure.persistence.assembler;

import cn.caldm.www.permission_context.domain.model.UserRoleRelation;
import cn.caldm.www.permission_context.infrastructure.persistence.po.UserRoleRelationPO;
import cn.caldm.www.shared_kernel.assembler.BaseAssembler;
import org.springframework.stereotype.Component;

/**
 *
 * “用户-角色”关联类domain和po转换器
 *
 * @author caldm
 */
@Component
public class UserRoleRelationAssembler implements BaseAssembler<UserRoleRelation, UserRoleRelationPO> {
    @Override
    public UserRoleRelation toDomain(UserRoleRelationPO po) {
        if (po == null) {
            return null;
        }
        UserRoleRelation domain = new UserRoleRelation();
        domain.setUserId(po.getUserId());
        domain.setRoleId(po.getRoleId());
        domain.setCreator(po.getCreator());
        domain.setCreateTime(po.getCreateTime());
        return domain;
    }

    @Override
    public UserRoleRelationPO toPO(UserRoleRelation domain) {
        if (domain == null) {
            return null;
        }
        UserRoleRelationPO po = new UserRoleRelationPO();
        po.setUserId(domain.getUserId());
        po.setRoleId(domain.getRoleId());
        po.setCreator(domain.getCreator());
        po.setCreateTime(domain.getCreateTime());
        return po;
    }
}
