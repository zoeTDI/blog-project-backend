package cn.caldm.www.auth_context.domain.repository;

public interface TokenBlacklistRepository {
    void addBlacklist(String token, long expireMillis);

    /**
     * 原子抢占：仅当 token 尚未在黑名单时将其加入，返回是否抢占成功。
     * 并发场景下只有一个调用能返回true，用于 refresh token 轮换放重放
     * @param token
     * @param ttlMillis
     * @return 抢占结果
     */
    boolean tryBlacklist(String token, long ttlMillis);
    boolean isBlacklisted(String token);
}
