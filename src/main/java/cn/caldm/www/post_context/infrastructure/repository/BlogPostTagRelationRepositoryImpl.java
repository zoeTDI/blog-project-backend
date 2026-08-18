package cn.caldm.www.post_context.infrastructure.repository;

import cn.caldm.www.post_context.domain.model.BlogPostTagRelation;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRelationRepository;
import cn.caldm.www.post_context.infrastructure.persistence.mapper.BlogPostTagRelationMapper;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostTagRelationPO;
import cn.caldm.www.post_context.interfaces.assembler.BlogPostTagRelationAssembler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *
 *
 * @author caldm
 */
@Repository
public class BlogPostTagRelationRepositoryImpl implements BlogPostTagRelationRepository {
    @Autowired
    BlogPostTagRelationMapper tagRelationMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private BlogPostTagRelationAssembler assembler;

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
    public int deleteByTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return 0;
        }
        LambdaQueryWrapper<BlogPostTagRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BlogPostTagRelationPO::getTagId, tagIds);
        return tagRelationMapper.delete(wrapper);
    }

    @Override
    public boolean save(BlogPostTagRelation relation) {
        if (relation == null || relation.getPostId() == null || relation.getTagId() == null) {
            return false;
        }
        BlogPostTagRelationPO po = assembler.toPO(relation);
        return tagRelationMapper.insert(po) == 1;
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
    public List<Long> findPostIdsByTagId(Long tagId) {
        if (tagId == null) {
            return List.of();
        }
        LambdaQueryWrapper<BlogPostTagRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostTagRelationPO::getTagId, tagId)
                .select(BlogPostTagRelationPO::getPostId);
        return tagRelationMapper.selectList(wrapper).stream()
                .map(BlogPostTagRelationPO::getPostId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findPostIdsByTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<BlogPostTagRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BlogPostTagRelationPO::getTagId, tagIds)
                .select(BlogPostTagRelationPO::getPostId)
                .groupBy(BlogPostTagRelationPO::getPostId);
        return tagRelationMapper.selectList(wrapper).stream()
                .map(BlogPostTagRelationPO::getPostId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByPostIdAndTagId(Long postId, Long tagId) {
        if (postId == null || tagId == null) {
            return false;
        }
        LambdaQueryWrapper<BlogPostTagRelationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostTagRelationPO::getPostId, postId)
                .eq(BlogPostTagRelationPO::getTagId, tagId);
        return tagRelationMapper.exists(wrapper);
    }
}
