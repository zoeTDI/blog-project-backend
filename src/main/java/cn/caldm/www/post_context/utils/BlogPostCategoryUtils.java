package cn.caldm.www.post_context.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Map<Long, CategoryTreeNode> map = new HashMap<>();
        categoryList.stream()
                .forEach(category -> {
                    Long id = category.getId();
                    CategoryTreeNode node = new CategoryTreeNode()
                            .setCategory(category)
                            .setChildren(new ArrayList<>());
                    map.put(id, node);
                });
        categoryList.stream()
                .forEach(category -> {
                    Long id = category.getId();
                    Long parentId = category.getParentId();
                    CategoryTreeNode parentNode = map.get(parentId);
                    if (parentNode == null) {
                        return;
                    }
                    parentNode.getChildren().add(map.get(id));
                });
        return map.values()
                .stream()
                .filter(node -> node.getCategory().getParentId() == 0L)
                .collect(Collectors.toList());
    }
}
