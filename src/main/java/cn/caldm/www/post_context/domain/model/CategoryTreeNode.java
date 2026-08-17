package cn.caldm.www.post_context.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *
 * @author caldm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CategoryTreeNode implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private BlogPostCategory category;

    private List<CategoryTreeNode> children = new ArrayList<>();
}
