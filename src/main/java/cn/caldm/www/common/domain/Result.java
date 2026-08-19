package cn.caldm.www.common.domain;

import lombok.Data;

import java.util.Map;

/**
 * 统一全局返回结果类
 *
 * @author caldm
 */
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> successMsg(String message) {
        return success(message, null);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(ResultCodeEnum codeEnum) {
        return error(codeEnum.getCode(), codeEnum.getMessage());
    }

    public static <T> Result<T> error(ResultCodeEnum codeEnum, String customMessage) {
        return error(codeEnum.getCode(), customMessage);
    }

    public static <T> Result<T> error() {
        return error(ResultCodeEnum.INTERNAL_SERVER_ERROR);
    }

    public static <T> Result<T> error(Integer code, String message, T data) {
        Result<T> result = error(code, message);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(ResultCodeEnum codeEnum, T data) {
        Result<T> result = new Result<>();
        result.setCode(codeEnum.getCode());
        result.setMessage(codeEnum.getMessage());
        result.setData(data);
        return result;
    }

    public boolean isSuccess() {
        return ResultCodeEnum.SUCCESS.getCode().equals(this.code);
    }

    public static <T> Result<T> cast(Result<?> originalResult) {
        Result<T> result = new Result<>();
        result.setCode(originalResult.getCode());
        result.setMessage(originalResult.getMessage());
        result.setTimestamp(originalResult.getTimestamp());
        return result;
    }
}
