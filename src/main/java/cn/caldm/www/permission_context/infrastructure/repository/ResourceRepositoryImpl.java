package cn.caldm.www.permission_context.infrastructure.repository;

import cn.caldm.www.permission_context.domain.model.Resource;
import cn.caldm.www.permission_context.domain.repository.ResourceRepository;
import cn.caldm.www.permission_context.infrastructure.persistence.mapper.ResourceMapper;
import cn.caldm.www.permission_context.infrastructure.persistence.po.ResourcePO;
import cn.caldm.www.permission_context.infrastructure.persistence.assembler.ResourceAssembler;
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
public class ResourceRepositoryImpl implements ResourceRepository {
    private final ResourceMapper resourceMapper;
    private final ResourceAssembler resourceAssembler;

    @Override
    public Optional<Resource> findById(Long id) {
        ResourcePO po = resourceMapper.selectById(id);
        return Optional.ofNullable(resourceAssembler.toDomain(po));
    }

    @Override
    public List<Resource> findByIds(List<Long> ids) {
        List<ResourcePO> selectByIds = resourceMapper.selectByIds(ids);
        return resourceAssembler.toDomainList(selectByIds);
    }

    @Override
    public List<Resource> findChildren(Long parentId) {
        List<ResourcePO> poList = resourceMapper.selectList(
                new LambdaQueryWrapper<ResourcePO>().eq(ResourcePO::getParentId, parentId)
        );
        return resourceAssembler.toDomainList(poList);
    }

    @Override
    public Resource create(Resource resource) {
        ResourcePO po = resourceAssembler.toPO(resource);
        resourceMapper.insert(po);
        return resourceAssembler.toDomain(po);
    }

    @Override
    public Resource update(Resource resource) {
        ResourcePO po = resourceAssembler.toPO(resource);
        resourceMapper.updateById(po);
        return resourceAssembler.toDomain(po);
    }

    @Override
    public void deleteById(Long id) {
        resourceMapper.deleteById(id);
    }
}
