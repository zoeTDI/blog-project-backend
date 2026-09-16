package cn.caldm.www.permission_context.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResourceTypeEnum {
    DIRECTORY(1, "目录"),
    MENU(2, "菜单"),
    BUTTON(3, "按钮");

    @EnumValue
    private final Integer code;

    @JsonValue
    private final String desc;

    ResourceTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ResourceTypeEnum fromCode(Integer code) {
        if (code == null) return null;
        for (ResourceTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("位置的权限类型：" + code);
    }
}
