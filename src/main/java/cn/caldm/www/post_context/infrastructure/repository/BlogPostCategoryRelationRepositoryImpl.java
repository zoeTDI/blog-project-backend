package cn.caldm.www.post_context.infrastructure.repository;

import cn.caldm.www.post_context.domain.model.BlogPostCategoryRelation;
import cn.caldm.www.post_context.domain.repository.BlogPostCategoryRelationRepository;
import cn.caldm.www.post_context.infrastructure.persistence.mapper.BlogPostCategoryRelationMapper;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostCategoryRelationPO;
import cn.caldm.www.post_context.interfaces.assembler.BlogPostCategoryRelationAssembler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *
 *
 * @author caldm
 */
@Repository
@RequiredArgsConstructor
public class BlogPostCategoryRelationRepositoryImpl implements BlogPostCategoryRelationRepository {
    @Autowired
    private BlogPostCategoryRelationMapper relationMapper;

    @Autowired
    private BlogPostCategoryRelationAssembler relationAssembler;

    @Override
    public BlogPostCategoryRelation save(BlogPostCategoryRelation relation) {
        BlogPostCategoryRelationPO po = relationAssembler.toPO(relation);
        if (po.getId() == null) {
            relationMapper.insert(po);
        } else {
            relationMapper.updateById(po);
        }
        return relationAssembler.toDomain(po);
    }

    @Override
    public void batchSave(List<BlogPostCategoryRelation> relations) {
        if (CollectionUtils.isEmpty(relations)) {
            return;
        }
        for (BlogPostCategoryRelation relation : relations) {
            save(relation);
        }
    }

    @Override
    public void deleteByCategoryId(Long categoryId) {
        if (categoryId == null) {
            return;
        }
        LambdaQueryWrapper<BlogPostCategoryRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostCategoryRelationPO::getCategoryId, categoryId);
        relationMapper.delete(wrapper);
    }

    @Override
    public void deleteByCategoryIds(Collection<Long> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return;
        }
        LambdaQueryWrapper<BlogPostCategoryRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BlogPostCategoryRelationPO::getCategoryId, categoryIds);
        relationMapper.delete(wrapper);
    }

    @Override
    public void deleteByPostId(Long postId) {
        if (postId == null) {
            return;
        }
        LambdaQueryWrapper<BlogPostCategoryRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostCategoryRelationPO::getPostId, postId);
        relationMapper.delete(wrapper);
    }

    @Override
    public void deleteByPostIdAndCategoryIds(Long postId, Collection<Long> categoryIds) {
        if (postId == null || CollectionUtils.isEmpty(categoryIds)) {
            return;
        }
        LambdaQueryWrapper<BlogPostCategoryRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostCategoryRelationPO::getPostId, postId)
                .in(BlogPostCategoryRelationPO::getCategoryId, categoryIds);
        relationMapper.delete(wrapper);
    }

    @Override
    public List<BlogPostCategoryRelation> findByCategoryIdsAndIsDirect(Collection<Long> categoryIds, Boolean isDirect) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<BlogPostCategoryRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BlogPostCategoryRelationPO::getCategoryId, categoryIds)
                .eq(BlogPostCategoryRelationPO::getIsDirect, isDirect);
        List<BlogPostCategoryRelationPO> pos = relationMapper.selectList(wrapper);
        return pos.stream().map(relationAssembler::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<BlogPostCategoryRelation> findByPostIdsAndIsDirect(Collection<Long> postIds, Boolean isDirect) {
        if (CollectionUtils.isEmpty(postIds)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<BlogPostCategoryRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BlogPostCategoryRelationPO::getPostId, postIds)
                .eq(BlogPostCategoryRelationPO::getIsDirect, isDirect);
        List<BlogPostCategoryRelationPO> pos = relationMapper.selectList(wrapper);
        return pos.stream().map(relationAssembler::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteByPostIdsAndIsDirect(Collection<Long> postIds, Boolean isDirect) {
        if (CollectionUtils.isEmpty(postIds)) {
            return;
        }
        LambdaQueryWrapper<BlogPostCategoryRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BlogPostCategoryRelationPO::getPostId, postIds)
                .eq(BlogPostCategoryRelationPO::getIsDirect, isDirect);
        relationMapper.delete(wrapper);
    }
}
