package cn.caldm.www.post_context.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 *
 *
 * @author caldm
 */
@Data
@Accessors(chain = true)
public class BlogCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long parentId;
    private String name;
    private String slug;
    private String description;
    private Integer sortWeight;
    private CategoryStatusEnum status;
    private String creator;
    private LocalDateTime createTime;
    private String updater;
    private LocalDateTime updateTime;
    private Boolean deleted;
}
