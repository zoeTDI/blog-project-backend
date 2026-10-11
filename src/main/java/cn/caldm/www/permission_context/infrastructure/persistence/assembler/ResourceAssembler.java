package cn.caldm.www.permission_context.infrastructure.persistence.assembler;

import cn.caldm.www.permission_context.domain.model.ResourceStatusEnum;
import cn.caldm.www.permission_context.domain.model.Resource;
import cn.caldm.www.permission_context.infrastructure.persistence.po.ResourcePO;
import cn.caldm.www.shared_kernel.assembler.BaseAssembler;
import org.springframework.stereotype.Component;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class ResourceAssembler implements BaseAssembler<Resource, ResourcePO> {
    @Override
    public Resource toDomain(ResourcePO po) {
        if (po == null) {
            return null;
        }
        return Resource.reconstitute(
                po.getId(),
                po.getName(),
                po.getPermission(),
                po.getType(),
                po.getParentId(),
                po.getSort(),
                po.getPath(),
                po.getComponent(),
                po.getIcon(),
                po.getTitleKey(),
                po.getStatus() == ResourceStatusEnum.NORMAL
        );
    }

    @Override
    public ResourcePO toPO(Resource domain) {
        if (domain == null){
            return null;
        }

        ResourcePO po = new ResourcePO();

        po.setId(domain.getId());
        po.setName(domain.getName());
        po.setPermission(domain.getPermission());
        po.setType(domain.getType());
        po.setParentId(domain.getParentId());
        po.setSort(domain.getSort());
        po.setPath(domain.getPath());
        po.setComponent(domain.getComponent());
        po.setIcon(domain.getIcon());
        po.setTitleKey(domain.getTitleKey());

        po.setStatus(
                domain.isEnabled()
                        ? ResourceStatusEnum.NORMAL
                        : ResourceStatusEnum.DISABLED
        );

        return po;
    }
}
