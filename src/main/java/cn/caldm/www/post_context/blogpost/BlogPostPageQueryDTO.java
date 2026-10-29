package cn.caldm.www.post_context.blogpost;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class BlogPostPageQueryDTO {
    @Min(value = 1, message = "page 必须大于等于 1")
    private long page = 1;

    @Min(value = 1, message = "size 必须大于等于 1")
    @Max(value = 100, message = "size 必须小于等于 100")
    private long size = 10;
}
