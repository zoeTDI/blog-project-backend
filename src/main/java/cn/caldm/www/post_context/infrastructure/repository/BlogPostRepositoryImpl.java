package cn.caldm.www.post_context.infrastructure.repository;

import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.domain.model.BlogPostTag;
import cn.caldm.www.post_context.domain.model.CategoryTreeNode;
import cn.caldm.www.post_context.domain.repository.BlogPostRepository;
import cn.caldm.www.post_context.infrastructure.persistence.mapper.BlogPostCategoryRelationMapper;
import cn.caldm.www.post_context.infrastructure.persistence.mapper.BlogPostMapper;
import cn.caldm.www.post_context.infrastructure.persistence.mapper.BlogPostTagRelationMapper;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostCategoryRelationPO;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostPO;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostTagRelationPO;
import cn.caldm.www.post_context.interfaces.assembler.BlogPostAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 *
 *
 * @author caldm
 */
@Repository
public class BlogPostRepositoryImpl implements BlogPostRepository {
    @Autowired
    private BlogPostMapper blogPostMapper;
    @Autowired
    private BlogPostTagRelationMapper blogPostTagRelationMapper;
    @Autowired
    private BlogPostCategoryRelationMapper blogPostCategoryRelationMapper;
    @Autowired
    private BlogPostAssembler blogPostAssembler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BlogPost save(BlogPost blogPost) {
        if (blogPost == null) {
            return null;
        }
        BlogPostPO po = blogPostAssembler.toPO(blogPost);
        blogPostMapper.insert(po);

        blogPost.setId(po.getId());

        saveTagRelations(blogPost.getId(), blogPost.getTags());

        saveCategoryRelations(blogPost.getId(), blogPost.getCategories());

        return blogPost;
    }

    private void saveTagRelations(Long postId, List<BlogPostTag> tags) {
        if (postId == null || tags == null || tags.isEmpty()) {
            return;
        }

        Set<Long> tagIds = new HashSet<>();
        List<BlogPostTagRelationPO> relationPOList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (BlogPostTag tag : tags) {
            if (tag != null && tag.getId() != null && tagIds.add(tag.getId())) {
                BlogPostTagRelationPO tagRelationPO = new BlogPostTagRelationPO();
                tagRelationPO.setPostId(postId);
                tagRelationPO.setTagId(tag.getId());
                tagRelationPO.setCreateTime(now);
                relationPOList.add(tagRelationPO);
            }
        }

        if (!relationPOList.isEmpty()) {
            blogPostTagRelationMapper.insert(relationPOList);
        }
    }

    /**
     * 批量保存分类关联关系（展平树节点并标记直接/间接关联）
     */
    private void saveCategoryRelations(Long postId, List<CategoryTreeNode> categoryTrees) {
        if (postId == null || categoryTrees == null || categoryTrees.isEmpty()) {
            return;
        }

        List<BlogPostCategoryRelationPO> relationPOList = new ArrayList<>();
        Set<Long> processedCategoryIds = new HashSet<>();

        // 1. 递归展平树结构并在内存中构建关联列表
        for (CategoryTreeNode treeNode : categoryTrees) {
            flattenCategoryTree(postId, treeNode, true, relationPOList, processedCategoryIds);
        }

        // 2. 批量插入数据库（非循环内单独操作）
        if (!relationPOList.isEmpty()) {
            blogPostCategoryRelationMapper.insert(relationPOList);
        }
    }

    /**
     * 递归展平分类树结构
     */
    private void flattenCategoryTree(Long postId,
                                     CategoryTreeNode node,
                                     boolean isDirect,
                                     List<BlogPostCategoryRelationPO> resultList,
                                     Set<Long> processedCategoryIds) {
        if (node == null || node.getCategory() == null || node.getCategory().getId() == null) {
            return;
        }

        Long categoryId = node.getCategory().getId();

        // 防止重复关联相同分类
        if (processedCategoryIds.add(categoryId)) {
            BlogPostCategoryRelationPO relationPO = new BlogPostCategoryRelationPO();
            relationPO.setPostId(postId);
            relationPO.setCategoryId(categoryId);
            relationPO.setIsDirect(isDirect);
            relationPO.setDeleted(false);
            resultList.add(relationPO);
        }

        // 递归处理子节点（子节点标记为非直接关联/祖先冗余）
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            for (CategoryTreeNode childNode : node.getChildren()) {
                flattenCategoryTree(postId, childNode, false, resultList, processedCategoryIds);
            }
        }
    }
}
