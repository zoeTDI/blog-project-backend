package cn.caldm.www.post_context.application.service;

import cn.caldm.www.post_context.application.service.command.BlogPostCategoryCreateCommand;
import cn.caldm.www.post_context.application.service.command.BlogPostCategoryMoveCommand;
import cn.caldm.www.post_context.application.service.command.BlogPostCategoryRenameCommand;
import cn.caldm.www.post_context.domain.model.BlogPostCategory;
import cn.caldm.www.post_context.domain.model.BlogPostCategoryRelation;
import cn.caldm.www.post_context.domain.model.BlogPostCategoryStatusEnum;
import cn.caldm.www.post_context.domain.model.CategoryTreeNode;
import cn.caldm.www.post_context.domain.repository.BlogPostCategoryRelationRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostCategoryRepository;
import cn.caldm.www.shared_kernel.security.SecurityContextHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 *
 *
 * @author caldm
 */
@Service
@RequiredArgsConstructor
public class BlogPostCategoryService {
    @Autowired
    private BlogPostCategoryRepository categoryRepository;

    @Autowired
    private BlogPostCategoryRelationRepository categoryRelationRepository;

    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(BlogPostCategoryCreateCommand command) {
        Long currentUserId = SecurityContextHolder.getUserId();
        String currentUsername = SecurityContextHolder.getUsername();

        if (categoryRepository.existsByUserIdAndName(currentUserId, command.getName())) {
            throw new IllegalArgumentException("分类名称 [" + command.getName() + "] 已存在");
        }

        Long parentId = command.getParentId() == null ? 0L : command.getParentId();
        if (parentId > 0) {
            BlogPostCategory parentCategory = categoryRepository.findById(parentId);
            if (parentCategory == null) {
                throw new IllegalArgumentException("指定的父级分类不存在");
            }
            if (!parentCategory.getUserId().equals(currentUserId)) {
                throw new IllegalArgumentException("不能在其他用户的分类下创建子分类");
            }
            if (BlogPostCategoryStatusEnum.DISABLED.equals(parentCategory.getStatus())) {
                throw new IllegalArgumentException("父级分类已被禁用，不能在其下创建子分类");
            }
        }

        BlogPostCategory category = new BlogPostCategory();
        category.setUserId(currentUserId);
        category.setParentId(parentId);
        category.setName(command.getName());
        category.setSlug(command.getSlug() != null ? command.getSlug() : "");
        category.setDescription(command.getDescription() != null ? command.getDescription() : "");
        category.setSortWeight(command.getSortWeight() != null ? command.getSortWeight() : 0);
        category.setStatus(command.getStatus() != null ? command.getStatus() : BlogPostCategoryStatusEnum.ENABLED);
        category.setCreator(currentUsername);
        category.setUpdater(currentUsername);

        BlogPostCategory savedCategory = categoryRepository.save(category);
        return savedCategory.getId();
    }

    /**
     * 递归级联软删除分类及其所有子孙分类，并物理删除对应的文章分类关联记录
     *
     * @param categoryId 待删除分类ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long categoryId) {
        Long currentUserId = SecurityContextHolder.getUserId();

        BlogPostCategory category = categoryRepository.findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("待删除的分类不存在");
        }
        if (!category.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("无权删除其他用户的分类");
        }

        List<BlogPostCategory> userCategories = categoryRepository.findListByUserId(currentUserId);
        Map<Long, List<BlogPostCategory>> parentGroupMap = userCategories.stream()
                .collect(Collectors.groupingBy(BlogPostCategory::getParentId));

        List<Long> targetCategoryIds = new ArrayList<>();
        collectChildCategoryIds(categoryId, parentGroupMap, targetCategoryIds);

        categoryRepository.deleteByIds(targetCategoryIds);

        categoryRelationRepository.deleteByCategoryIds(targetCategoryIds);
    }

    /**
     * 分类移动（调整父节点）
     *
     * @param categoryId 待移动的分类ID
     * @param command    移动参数（包含目标父分类ID）
     */
    @Transactional(rollbackFor = Exception.class)
    public void moveCategory(Long categoryId, BlogPostCategoryMoveCommand command) {
        Long currentUserId = SecurityContextHolder.getUserId();
        String currentUsername = SecurityContextHolder.getUsername();
        Long targetParentId = command.getTargetParentId();

        BlogPostCategory category = categoryRepository.findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("待移动的分类不存在");
        }
        if (!category.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("无权移动其他用户的分类");
        }

        // 不能移动到自身
        if (categoryId.equals(targetParentId)) {
            throw new IllegalArgumentException("分类不能移动到自身下");
        }

        // 如果父节点没有变动，直接返回
        if (category.getParentId().equals(targetParentId)) {
            return;
        }

        if (targetParentId > 0) {
            BlogPostCategory parentCategory = categoryRepository.findById(targetParentId);
            if (parentCategory == null) {
                throw new IllegalArgumentException("目标父级分类不存在");
            }
            if (!parentCategory.getUserId().equals(currentUserId)) {
                throw new IllegalArgumentException("不能移动到其他用户的分类下");
            }
            if (BlogPostCategoryStatusEnum.DISABLED.equals(parentCategory.getStatus())) {
                throw new IllegalArgumentException("目标父级分类已被禁用，无法移动到该分类下");
            }
        }

        List<BlogPostCategory> userCategories = categoryRepository.findListByUserId(currentUserId);
        Map<Long, List<BlogPostCategory>> parentGroupMap = userCategories.stream()
                .collect(Collectors.groupingBy(BlogPostCategory::getParentId));

        List<Long> descendantCategoryIds = new ArrayList<>();
        collectChildCategoryIds(categoryId, parentGroupMap, descendantCategoryIds);

        if (descendantCategoryIds.contains(targetParentId)) {
            throw new IllegalArgumentException("不能将分类移动到其子孙分类下");
        }

        // 4. 更新分类父节点 parent_id
        category.setParentId(targetParentId);
        category.setUpdater(currentUsername);
        categoryRepository.save(category);

        // 5. 同步受影响文章对祖先分类的冗余记录
        syncIndirectRelationsAfterMove(currentUserId, descendantCategoryIds);
    }

    @Transactional(rollbackFor = Exception.class)
    public void renameCategory(@Valid BlogPostCategoryRenameCommand command) {
        Long targetId = command.getTargetId();
        String newName = command.getNewName();
        Long userId = SecurityContextHolder.getUserId();
        BlogPostCategory targetCategory = categoryRepository.findById(targetId);
        if (targetCategory == null) {
            throw new IllegalArgumentException("Target category not exists or already deleted.");
        }
        if (!Objects.equals(userId, targetCategory.getUserId())) {
            throw new IllegalArgumentException("No permission to modify this category.");
        }

        BlogPostCategory category = new BlogPostCategory();
        category.setId(targetId)
                .setName(newName)
                .setUpdater(SecurityContextHolder.getUsername())
                .setUpdateTime(LocalDateTime.now());
        boolean renamed = categoryRepository.rename(category);
        if (!renamed) {
            throw new IllegalStateException("Rename operation failed.");
        }
    }

    public List<CategoryTreeNode> getCategoryTreeByAuthorId(Long authorId) {
        if (authorId == null) {
            return List.of();
        }
        List<BlogPostCategory> categoryList = categoryRepository.findListByUserId(authorId);

        // Filter out deleted categories.
        List<BlogPostCategory> filtered = categoryList.stream()
                .filter(category -> category.getDeleted() == false)
                .collect(Collectors.toList());

        // Build id -> node mapping.
        Map<Long, CategoryTreeNode> map = new HashMap<>();
        filtered.forEach(category -> {
            CategoryTreeNode node = new CategoryTreeNode();
            node.setCategory(category);
            node.setChildren(new ArrayList<>());
            map.put(category.getId(), node);
        });
        // Attach child nodes to parent node.
        filtered.forEach(category -> {
            Long parentId = category.getParentId();
            if (parentId != null && !parentId.equals(0L)) {
                CategoryTreeNode parent = map.get(parentId);

                if (parent != null) {
                    parent.getChildren().add(map.get(category.getId()));
                }
            }
        });

        // Collect all root nodes.
        return filtered.stream()
                .filter(category -> {
                    Long parentId = category.getParentId();
                    return parentId == null || parentId.equals(0L);
                })
                .map(category -> map.get(category.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 分类移动后刷新同步文章对祖先分类的冗余关联记录
     */
    private void syncIndirectRelationsAfterMove(Long userId, List<Long> affectedCategoryIds) {
        // 5.1 查询关联了受影响分类的所有直接关联记录（is_direct = true）
        List<BlogPostCategoryRelation> directRelations = categoryRelationRepository
                .findByCategoryIdsAndIsDirect(affectedCategoryIds, true);

        if (CollectionUtils.isEmpty(directRelations)) {
            return;
        }

        // 获取受影响的全部文章 ID
        Set<Long> affectedPostIds = directRelations.stream()
                .map(BlogPostCategoryRelation::getPostId)
                .collect(Collectors.toSet());

        // 物理删除这些文章原有的所有间接冗余记录（is_direct = false）
        categoryRelationRepository.deleteByPostIdsAndIsDirect(affectedPostIds, false);

        // 重新获取最新全量分类 Map，用于向上递归计算新祖先
        List<BlogPostCategory> latestCategories = categoryRepository.findListByUserId(userId);
        Map<Long, BlogPostCategory> categoryMap = latestCategories.stream()
                .collect(Collectors.toMap(BlogPostCategory::getId, Function.identity()));

        // 查询受影响文章当前所有的直接关联记录（以防一篇文章绑定多个直接分类）
        List<BlogPostCategoryRelation> allPostDirectRelations = categoryRelationRepository
                .findByPostIdsAndIsDirect(affectedPostIds, true);

        Map<Long, List<BlogPostCategoryRelation>> postDirectRelationMap = allPostDirectRelations.stream()
                .collect(Collectors.groupingBy(BlogPostCategoryRelation::getPostId));

        // 5.5 为每篇文章计算并重建与所有祖先分类的间接关联记录
        List<BlogPostCategoryRelation> newIndirectRelations = new ArrayList<>();

        for (Long postId : affectedPostIds) {
            List<BlogPostCategoryRelation> pDirects = postDirectRelationMap.get(postId);
            if (CollectionUtils.isEmpty(pDirects)) {
                continue;
            }

            Set<Long> ancestorCategoryIds = new HashSet<>();
            for (BlogPostCategoryRelation direct : pDirects) {
                collectAncestorCategoryIds(direct.getCategoryId(), categoryMap, ancestorCategoryIds);
            }

            for (Long ancestorId : ancestorCategoryIds) {
                BlogPostCategoryRelation relation = new BlogPostCategoryRelation();
                relation.setPostId(postId);
                relation.setCategoryId(ancestorId);
                relation.markAsIndirect(); // isDirect = false
                newIndirectRelations.add(relation);
            }
        }

        // 批量插入最新推算的间接冗余关联
        if (!newIndirectRelations.isEmpty()) {
            categoryRelationRepository.batchSave(newIndirectRelations);
        }
    }

    /**
     * 向上递归收集祖先节点 ID 辅助方法（排除直接分类本身）
     */
    private void collectAncestorCategoryIds(Long currentCategoryId, Map<Long, BlogPostCategory> categoryMap,
            Set<Long> ancestorIds) {
        BlogPostCategory current = categoryMap.get(currentCategoryId);
        if (current == null || current.getParentId() == null || current.getParentId() <= 0) {
            return;
        }

        Long parentId = current.getParentId();
        if (categoryMap.containsKey(parentId)) {
            ancestorIds.add(parentId);
            collectAncestorCategoryIds(parentId, categoryMap, ancestorIds);
        }
    }

    /**
     * 递归收集当前分类及其所有子孙节点的 ID 辅助方法
     */
    private void collectChildCategoryIds(Long currentId, Map<Long, List<BlogPostCategory>> parentGroupMap,
            List<Long> resultIds) {
        resultIds.add(currentId);
        List<BlogPostCategory> children = parentGroupMap.get(currentId);
        if (children != null && !children.isEmpty()) {
            for (BlogPostCategory child : children) {
                collectChildCategoryIds(child.getId(), parentGroupMap, resultIds);
            }
        }
    }
}
