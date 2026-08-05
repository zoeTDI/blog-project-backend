package cn.caldm.www.auth_context.domain.repository;

public interface VerificationCodeRepository {
    /**
     * 保存验证码及过期时间
     * @param target 目标标识（如邮箱地址或手机号）
     * @param code 验证码
     * @param ttlSeconds 有效期（秒）
     */
    void saveCode(String target, String code, long ttlSeconds);

    /**
     * 获取验证码
     */
    String getCode(String target);

    /**
     * 删除验证码（通常在校验成功后消费掉）
     */
    void deleteCode(String target);

    /**
     * 校验验证码是否正确
     * @param target 目标标识
     * @param code 待输入的验证码
     * @return 是否通过
     */
    boolean verifyCode(String target, String code);
}
