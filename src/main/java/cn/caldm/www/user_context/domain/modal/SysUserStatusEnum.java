package cn.caldm.www.user_context.domain.modal;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 *
 *
 *
 * @author caldm
 */
@Getter
public enum SysUserStatusEnum {
    NORMAL(0, "正常"),
    DISABLED(1, "停用");

    @EnumValue // 标记数据库存储的值（即 tinyint 的 0 或 1）
    private final Integer code;

    @JsonValue // （可选）让接口返回时显示 "NORMAL" 或 "DISABLED"，而非数字
    private final String desc;

    SysUserStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 根据 code 获取枚举（方便前端传数字时转换）
    public static SysUserStatusEnum fromCode(Integer code) {
        if (code == null) return null;
        for (SysUserStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的用户状态: " + code);
    }
}
