package cn.caldm.www.permission_context.domain.model;

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
public enum ResourceStatusEnum {
    NORMAL(0, "正常"),
    DISABLED(1, "停用");

    @EnumValue // 标记数据库存储的值（即 tinyint 的 0 或 1）
    private final Integer code;

    @JsonValue // （可选）让接口返回时显示 "NORMAL" 或 "DISABLED"，而非数字
    private final String desc;

    ResourceStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ResourceStatusEnum fromCode(Integer code) {
        if (code == null)
            return null;
        for (ResourceStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的权限状态: " + code);
    }
}
