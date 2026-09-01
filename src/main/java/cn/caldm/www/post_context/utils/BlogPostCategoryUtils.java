package cn.caldm.www.post_context.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
        categoryList.forEach(category -> {
            Long id = category.getId();
            CategoryTreeNode node = new CategoryTreeNode()
                    .setCategory(category)
                    .setChildren(new ArrayList<>());
            map.put(id, node);
        });
        categoryList.forEach(category -> {
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

    /**
     * 将树形结构的分类节点扁平化
     *
     * @param rootNodes 根节点列表
     * @return 所有节点对应的分类，若输入null或空则返回空列表
     */
    public static List<BlogPostCategory> flattenTree(List<CategoryTreeNode> rootNodes) {
        if (rootNodes == null || rootNodes.isEmpty()) {
            return new ArrayList<>();
        }
        List<BlogPostCategory> result = new ArrayList<>();
        Queue<CategoryTreeNode> queue = new LinkedList<>(rootNodes);
        while (!queue.isEmpty()) {
            CategoryTreeNode node = queue.poll();
            result.add(node.getCategory());
            if (node.getChildren() != null) {
                queue.addAll(node.getChildren());
            }
        }
        return result;
    }
}
