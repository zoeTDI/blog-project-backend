package cn.caldm.www.permission_context.infrastructure.repository;

import cn.caldm.www.permission_context.domain.model.SystemResource;
import cn.caldm.www.permission_context.domain.repository.SystemResourceRepository;
import cn.caldm.www.permission_context.infrastructure.persistence.mapper.SystemResourceMapper;
import cn.caldm.www.permission_context.infrastructure.persistence.po.SystemResourcePO;
import cn.caldm.www.permission_context.infrastructure.persistence.assembler.SystemResourceAssembler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 *
 *
 *
 * @author caldm
 */
@Repository
@RequiredArgsConstructor
public class SystemResourceRepositoryImpl implements SystemResourceRepository {
    private final SystemResourceMapper resourceMapper;
    private final SystemResourceAssembler resourceAssembler;

    @Override
    public Optional<SystemResource> findById(Long id) {
        SystemResourcePO po = resourceMapper.selectById(id);
        return Optional.ofNullable(resourceAssembler.toDomain(po));
    }

    @Override
    public List<SystemResource> findChildren(Long parentId) {
        List<SystemResourcePO> poList = resourceMapper.selectList(
                new LambdaQueryWrapper<SystemResourcePO>().eq(SystemResourcePO::getParentId, parentId)
        );
        return resourceAssembler.toDomainList(poList);
    }

    @Override
    public SystemResource create(SystemResource resource) {
        SystemResourcePO po = resourceAssembler.toPO(resource);
        resourceMapper.insert(po);
        return resourceAssembler.toDomain(po);
    }

    @Override
    public SystemResource update(SystemResource resource) {
        SystemResourcePO po = resourceAssembler.toPO(resource);
        resourceMapper.updateById(po);
        return resourceAssembler.toDomain(po);
    }

    @Override
    public void deleteById(Long id) {
        resourceMapper.deleteById(id);
    }
}
