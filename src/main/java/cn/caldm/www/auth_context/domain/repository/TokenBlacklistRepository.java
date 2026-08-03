package cn.caldm.www.auth_context.domain.repository;

public interface TokenBlacklistRepository {
    void addBlacklist(String token, long expireMillis);
    boolean isBlacklisted(String token);
}
