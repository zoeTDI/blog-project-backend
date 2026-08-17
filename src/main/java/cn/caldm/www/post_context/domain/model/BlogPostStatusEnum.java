package cn.caldm.www.post_context.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 文章状态枚举
 */
@Getter
public enum BlogPostStatusEnum {
    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    REVIEWING(2, "审核中"),
    RECYCLE(3, "回收站"),
    PRIVATE(4, "私密");

    @EnumValue
    private final Integer code;

    @JsonValue
    private final String desc;

    BlogPostStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static BlogPostStatusEnum fromCode(Integer code) {
        if (code == null) return null;
        for (BlogPostStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
