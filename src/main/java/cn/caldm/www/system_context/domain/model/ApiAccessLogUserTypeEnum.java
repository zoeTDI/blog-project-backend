package cn.caldm.www.system_context.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ApiAccessLogUserTypeEnum {
    MANAGER(1, "管理用户"),
    REGISTERED(2, "注册用户");

    @EnumValue
    private final Integer code;

    @JsonValue
    private final String desc;

    ApiAccessLogUserTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ApiAccessLogUserTypeEnum fromCode(Integer code) {
        if (code == null) return null;
        for (ApiAccessLogUserTypeEnum apiAccessLogEnum : values()) {
            if (apiAccessLogEnum.code.equals(code)) {
                return apiAccessLogEnum;
            }
        }
        throw new IllegalArgumentException("未知的用户类型: " + code);
    }
}
