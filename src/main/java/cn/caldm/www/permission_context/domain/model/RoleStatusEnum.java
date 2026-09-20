package cn.caldm.www.permission_context.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter 
public enum RoleStatusEnum {
    NORMAL(0, "正常"),
    DISABLED(1, "停用");

    @EnumValue 
    private final Integer code;

    @JsonValue 
    private final String desc;

    RoleStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RoleStatusEnum fromCode(Integer code) {
        if (code == null) return null;
        for (RoleStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知的角色状态：" + code);
    }

}
