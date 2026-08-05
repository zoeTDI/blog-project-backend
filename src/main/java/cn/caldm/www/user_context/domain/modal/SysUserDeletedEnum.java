package cn.caldm.www.user_context.domain.modal;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum SysUserDeletedEnum {
    NORMAL(0, "未删除"),
    DELETED(1, "已删除");

    @EnumValue // 标记数据库存储的值（tinyint/bit 的 0 或 1）
    private final Integer code;

    @JsonValue // 接口返回时显示 "NORMAL" 或 "DELETED"
    private final String desc;

    SysUserDeletedEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 根据 code 获取枚举
    public static SysUserDeletedEnum fromCode(Integer code) {
        if (code == null) return null;
        for (SysUserDeletedEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的删除状态: " + code);
    }

    // 便捷判断方法，让业务代码更可读
    public boolean isDeleted() {
        return this == DELETED;
    }

    public boolean isNormal() {
        return this == NORMAL;
    }
}
