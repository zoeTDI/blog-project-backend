package cn.caldm.www.shared_kernel.domain;

/**
 * 业务异常
 * 
 * @author caldm
 */
public class ResourceNotFoundException extends RuntimeException {

    private final ResultCodeEnum code;

    public ResourceNotFoundException(ResultCodeEnum code) {
        super(code.getMessage());
        this.code = code;
    }

    public ResourceNotFoundException(ResultCodeEnum code, String message) {
        super(message);
        this.code = code;
    }

    public ResourceNotFoundException(ResultCodeEnum code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ResultCodeEnum getCode() {
        return code;
    }
}
