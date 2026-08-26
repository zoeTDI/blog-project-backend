package cn.caldm.www.common.domain;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "暂未登录或Token已过期"),
    REFRESH_FAILED(486, "Token 刷新失败"),
    FORBIDDEN(403, "没有相关权限"),
    INTERNAL_SERVER_ERROR(500, "系统内部异常");

    private final Integer code;
    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
