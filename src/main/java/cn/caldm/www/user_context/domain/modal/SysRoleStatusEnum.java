package cn.caldm.www.user_context.domain.modal;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter 
public enum SysRoleStatusEnum {
    NORMAL(0, "正常"),
    DISABLED(1, "停用");

    @EnumValue 
    private final Integer code;

    @JsonValue 
    private final String desc;

    SysRoleStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SysRoleStatusEnum fromCode(Integer code) {
        if (code == null) return null;
        for (SysRoleStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知的角色状态：" + code);
    }

}
