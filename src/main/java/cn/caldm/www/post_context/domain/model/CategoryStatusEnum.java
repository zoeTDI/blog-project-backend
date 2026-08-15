package cn.caldm.www.post_context.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 分类状态枚举
 */
@Getter
public enum CategoryStatusEnum {
    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    @EnumValue
    private final Integer code;

    @JsonValue
    private final String desc;

    CategoryStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CategoryStatusEnum fromCode(Integer code) {
        if (code == null) return null;
        for (CategoryStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }
}
