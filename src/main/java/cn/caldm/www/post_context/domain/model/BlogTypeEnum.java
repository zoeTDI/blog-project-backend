package cn.caldm.www.post_context.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 文章类型枚举
 */
@Getter
public enum BlogTypeEnum {
    /**
     * 普通文章
     */
    NORMAL(1, "普通文章"),
    /**
     * 技术笔记
     */
    TECH_NOTE(2, "技术笔记"),
    /**
     * 生活随笔
     */
    ESSAY(3, "生活随笔"),
    /**
     * 其他
     */
    OTHER(4, "其他");

    @EnumValue
    private final Integer code;

    @JsonValue
    private final String desc;

    BlogTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据 code 获取枚举
     */
    public static BlogTypeEnum fromCode(Integer code) {
        if (code == null) return null;
        for (BlogTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
