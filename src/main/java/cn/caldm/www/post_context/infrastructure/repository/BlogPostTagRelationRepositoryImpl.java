package cn.caldm.www.post_context.infrastructure.repository;

import cn.caldm.www.post_context.domain.model.BlogPostTagRelation;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRelationRepository;
import cn.caldm.www.post_context.infrastructure.persistence.mapper.BlogPostTagRelationMapper;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostTagRelationPO;
import cn.caldm.www.post_context.infrastructure.persistence.assembler.BlogPostTagRelationAssembler;
import lombok.RequiredArgsConstructor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
public class BlogPostTagRelationRepositoryImpl implements BlogPostTagRelationRepository {

    private final BlogPostTagRelationMapper tagRelationMapper;
    private final BlogPostTagRelationAssembler assembler;

    @Override
    public int deleteByTagId(Long tagId) {
        if (tagId == null) {
            return 0;
        }
        LambdaQueryWrapper<BlogPostTagRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostTagRelationPO::getTagId, tagId);
        return tagRelationMapper.delete(wrapper);
    }

    @Override
    public int batchSave(List<BlogPostTagRelation> relations) {
        if (relations == null || relations.isEmpty()) {
            return 0;
        }
        List<BlogPostTagRelationPO> pos = relations.stream()
                .map(assembler::toPO)
                .collect(Collectors.toList());
        return tagRelationMapper.insertBatch(pos);
    }

    @Override
    public void deleteByPostId(Long postId) {
        if (postId == null) {
            return;
        }
        LambdaQueryWrapper<BlogPostTagRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostTagRelationPO::getPostId, postId);
        tagRelationMapper.delete(wrapper);
    }

    @Override
    public List<BlogPostTagRelation> findByPostId(Long postId) {
        if (postId == null) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<BlogPostTagRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostTagRelationPO::getPostId, postId);
        List<BlogPostTagRelationPO> pos = tagRelationMapper.selectList(wrapper);
        return assembler.toDomainList(pos);
    }

}
