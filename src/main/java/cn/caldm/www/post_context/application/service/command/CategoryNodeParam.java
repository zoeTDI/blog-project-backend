package cn.caldm.www.post_context.application.service.command;

import lombok.Data;

import java.util.List;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class CategoryNodeParam {
    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 子分类节点列表
     */
    private List<CategoryNodeParam> children;
}
