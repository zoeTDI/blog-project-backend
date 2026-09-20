package cn.caldm.www.permission_context.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter 
public enum RoleDeletedEnum {
    NORMAL(0, "未删除"),
    DELETED(1, "已删除");

        @EnumValue // 标记数据库存储的值（tinyint/bit 的 0 或 1）
    private final Integer code;

    @JsonValue // 接口返回时显示 "NORMAL" 或 "DELETED"
    private final String desc;

    RoleDeletedEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RoleDeletedEnum fromCode(Integer code) {
        if (code == null) return null;
        for (RoleDeletedEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知的删除状态：" + code);
    }

    public boolean isDeleted() {
        return this == DELETED;
    }

    public boolean isNormal() {
        return this == NORMAL;
    }
}
