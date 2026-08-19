package cn.caldm.www.post_context.application.service;
import cn.caldm.www.post_context.application.service.command.BlogPostCategoryCreateCommand;
import cn.caldm.www.post_context.domain.model.BlogPostCategory;
import cn.caldm.www.post_context.domain.model.BlogPostCategoryStatusEnum;
import cn.caldm.www.post_context.domain.repository.BlogPostCategoryRepository;
import cn.caldm.www.shared_kernel.security.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
