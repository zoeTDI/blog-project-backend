package cn.caldm.www.post_context.blogcategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.caldm.www.post_context.domain.model.BlogPostCategory;
import cn.caldm.www.post_context.domain.model.CategoryTreeNode;

public class BlogPostCategoryUtils {
    /**
     * 将扁平的分类列表构建为属性结构，并返回所有根节点
     *
     * @param categoryList 分类列表
     * @return 根节点列表
     */
    public static List<CategoryTreeNode> buildTree(List<BlogPostCategory> categoryList) {
        if (categoryList == null || categoryList.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, CategoryTreeNode> map = new HashMap<>();
        categoryList.forEach(category -> {
            CategoryTreeNode node = new CategoryTreeNode()
                    .setCategory(category)
                    .setChildren(new ArrayList<>());
            map.put(category.getId(), node);
        });
        List<CategoryTreeNode> roots = new ArrayList<>();
        categoryList.forEach(category -> {
            CategoryTreeNode self = map.get(category.getId());
            Long parentId = category.getParentId();
            CategoryTreeNode parentNode = parentId == null ? null : map.get(parentId);
            // 父节点缺失或自引用时按根处理，避免孤儿被静默丢弃与成环
            if (parentNode != null && parentNode != self) {
                parentNode.getChildren().add(self);
            } else {
                roots.add(self);
            }
        });
        return roots;
    }
}
