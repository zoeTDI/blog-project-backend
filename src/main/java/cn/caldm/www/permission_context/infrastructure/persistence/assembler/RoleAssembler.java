package cn.caldm.www.permission_context.infrastructure.persistence.assembler;

import cn.caldm.www.permission_context.domain.model.RoleStatusEnum;
import org.springframework.stereotype.Component;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.domain.model.RoleDeletedEnum;
import cn.caldm.www.permission_context.infrastructure.persistence.po.RolePO;
import cn.caldm.www.shared_kernel.assembler.BaseAssembler;

@Component
public class RoleAssembler implements BaseAssembler<Role, RolePO> {

    @Override
    public Role toDomain(RolePO po) {
        if (po == null) {
            return null;
        }
        boolean enabled = RoleStatusEnum.NORMAL.equals(po.getStatus());
        return  Role.reconstitute(
            po.getId(), 
            po.getName(), 
            po.getCode(), 
            po.getSort(), 
            po.getRemark(), 
            enabled
        );
    }

    @Override
    public RolePO toPO(Role domain) {
        RolePO po = new RolePO();
        po.setId(domain.getId());
        po.setName(domain.getName());
        po.setCode(domain.getCode());
        po.setStatus(domain.isEnabled() ? RoleStatusEnum.NORMAL : RoleStatusEnum.DISABLED);
        po.setSort(domain.getSort());
        po.setRemark(domain.getRemark());
        po.setDeleted(RoleDeletedEnum.NORMAL);
        return po;
    }

}
