package cn.caldm.www.post_context.interfaces.dto;

import lombok.Data;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class BlogPostAddReqDTO {
    private Long authorId;
    private String title;
    private String subtitle;
    private String contentMd;
    private String contentHtml;
}
