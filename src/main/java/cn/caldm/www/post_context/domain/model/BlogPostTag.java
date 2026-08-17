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
public class BlogPostTag implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long authorId;
    private String name;
    private Integer postCount;
    private String creator;
    private String updater;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
}
