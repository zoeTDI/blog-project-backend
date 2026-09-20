package cn.caldm.www.permission_context.infrastructure.persistence.assembler;

import org.springframework.stereotype.Component;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.domain.model.RoleDeletedEnum;
import cn.caldm.www.permission_context.domain.model.RoleStatusEnum;
import cn.caldm.www.permission_context.infrastructure.persistence.po.RolePO;
import cn.caldm.www.shared_kernel.assembler.BaseAssembler;

@Component
public class SysRoleAssembler implements BaseAssembler<Role, RolePO> {

    @Override
    public Role toDomain(RolePO po) {
        return  Role.reconstitute(
            po.getId(), 
            po.getName(), 
            po.getCode(), 
            po.getSort(), 
            po.getStatus(),
            po.getRemark(), 
            RoleDeletedEnum.DELETED.equals(po.getDeleted())
                && RoleStatusEnum.DISABLED.equals(po.getStatus())
        );
    }

    @Override
    public RolePO toPO(Role domain) {
        RolePO po = new RolePO();
        po.setId(domain.getId());
        po.setName(domain.getName());
        po.setCode(domain.getCode());
        po.setSort(domain.getSort());
        po.setRemark(domain.getRemark());
        po.setStatus(domain.getStatus());
        po.setDeleted(RoleDeletedEnum.NORMAL);
        return po;
    }

}
