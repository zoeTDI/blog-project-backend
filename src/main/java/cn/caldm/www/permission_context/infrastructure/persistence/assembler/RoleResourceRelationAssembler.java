package cn.caldm.www.permission_context.infrastructure.persistence.assembler;

import cn.caldm.www.permission_context.domain.model.RoleResourceRelation;
import cn.caldm.www.permission_context.infrastructure.persistence.po.RoleResourceRelationPO;
import cn.caldm.www.shared_kernel.assembler.BaseAssembler;
import org.springframework.stereotype.Component;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class RoleResourceRelationAssembler implements BaseAssembler<RoleResourceRelation, RoleResourceRelationPO> {
    @Override
    public RoleResourceRelation toDomain(RoleResourceRelationPO po) {
        if (po == null) {
            return null;
        }
        return RoleResourceRelation.create(po.getRoleId(), po.getResourceId());
    }

    @Override
    public RoleResourceRelationPO toPO(RoleResourceRelation domain) {
        if (domain == null) {
            return null;
        }
        RoleResourceRelationPO po = new RoleResourceRelationPO();
        po.setRoleId(domain.getRoleId());
        po.setResourceId(domain.getResourceId());
        return po;
    }
}
