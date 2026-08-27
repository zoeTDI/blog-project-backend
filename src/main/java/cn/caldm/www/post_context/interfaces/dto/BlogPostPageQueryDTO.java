package cn.caldm.www.post_context.interfaces.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class BlogPostPageQueryDTO {
    @Min(value = 1, message = "page must be greater than or equal to 1")
    private long page = 1;

    @Min(value = 1, message = "size must be greater than or equal to 1")
    @Max(value = 100, message = "size must be less than or equal to 100")
    private long size = 10;
}
