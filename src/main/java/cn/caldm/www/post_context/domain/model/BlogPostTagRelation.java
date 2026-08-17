package cn.caldm.www.post_context.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 *
 *
 * @author caldm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BlogPostTagRelation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联记录编号
     */
    private Long id;

    /**
     * 文章ID
     */
    private Long postId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 关联绑定时间
     */
    private LocalDateTime createTime;
}
