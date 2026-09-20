package cn.caldm.www.permission_context.infrastructure.persistence.assembler;

import org.springframework.stereotype.Component;

import cn.caldm.www.permission_context.domain.model.SysRole;
import cn.caldm.www.permission_context.domain.model.SysRoleDeletedEnum;
import cn.caldm.www.permission_context.domain.model.SysRoleStatusEnum;
import cn.caldm.www.permission_context.infrastructure.persistence.po.SysRolePO;
import cn.caldm.www.shared_kernel.assembler.BaseAssembler;

@Component
public class SysRoleAssembler implements BaseAssembler<SysRole, SysRolePO> {

    @Override
    public SysRole toDomain(SysRolePO po) {
        return  SysRole.reconstitute(
            po.getId(), 
            po.getName(), 
            po.getCode(), 
            po.getSort(), 
            po.getStatus(),
            po.getRemark(), 
            SysRoleDeletedEnum.DELETED.equals(po.getDeleted())
                && SysRoleStatusEnum.DISABLED.equals(po.getStatus())
        );
    }

    @Override
    public SysRolePO toPO(SysRole domain) {
        SysRolePO po = new SysRolePO();
        po.setId(domain.getId());
        po.setName(domain.getName());
        po.setCode(domain.getCode());
        po.setSort(domain.getSort());
        po.setRemark(domain.getRemark());
        po.setStatus(domain.getStatus());
        po.setDeleted(SysRoleDeletedEnum.NORMAL);
        return po;
    }

}
