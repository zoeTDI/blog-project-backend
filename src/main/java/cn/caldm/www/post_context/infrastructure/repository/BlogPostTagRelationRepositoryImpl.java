package cn.caldm.www.post_context.infrastructure.repository;

import cn.caldm.www.post_context.domain.repository.BlogPostTagRelationRepository;
import cn.caldm.www.post_context.infrastructure.persistence.mapper.BlogPostTagRelationMapper;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostTagRelationPO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
