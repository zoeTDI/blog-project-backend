package cn.caldm.www.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * 慢哈希/密钥派生工具类（用于高安全性密码加密）
 * 基于 BCrypt 及 PBKDF2 算法，具备抗暴力破解能力
 *
 * @author caldm
 */
public class SlowHashUtils {

    // 默认 BCrypt 工作因子（Log Rounds，范围 4-31，数值越大计算越慢越安全）
    private static final int DEFAULT_BCRYPT_STRENGTH = 10;

    private static final BCryptPasswordEncoder BCRYPT_ENCODER = new BCryptPasswordEncoder(DEFAULT_BCRYPT_STRENGTH);

    // PBKDF2 默认参数配置
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int PBKDF2_ITERATIONS = 60000; // 迭代次数
    private static final int PBKDF2_KEY_LENGTH = 256;   // 密钥长度（bit）
    private static final int PBKDF2_SALT_LENGTH = 16;   // 盐长度（byte）

    /**
     * 【方案一：BCrypt 算法】
     * 对密码进行慢哈希加密（自动生成并内嵌随机盐，输出包含算法、工作因子和盐值，形如 "$2a$10$..."）
     *
     * @param rawPassword 明文密码
     * @return 加密后的密文字符串
     */
    public static String bcryptEncode(String rawPassword) {
        if (!StringUtils.hasText(rawPassword)) {
            return null;
        }
        return BCRYPT_ENCODER.encode(rawPassword);
    }

    /**
     * 校验明文密码与 BCrypt 密文是否匹配
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 密文密码
     * @return 是否匹配
     */
    public static boolean bcryptMatches(String rawPassword, String encodedPassword) {
        if (!StringUtils.hasText(rawPassword) || !StringUtils.hasText(encodedPassword)) {
            return false;
        }
        return BCRYPT_ENCODER.matches(rawPassword, encodedPassword);
    }

    /**
     * 【方案二：PBKDF2 算法】
     * 使用 PBKDF2WithHmacSHA256 生成慢哈希，并返回 "salt:hash" 格式的结果以便存储
     *
     * @param rawPassword 明文密码
     * @return 格式化的哈希字符串（十六进制拼装）
     */
    public static String pbkdf2Encode(String rawPassword) {
        if (!StringUtils.hasText(rawPassword)) {
            return null;
        }
        try {
            // 生成随机盐
            byte[] salt = KeyGenerators.secureRandom(PBKDF2_SALT_LENGTH).generateKey();
            byte[] hash = pbkdf2Hash(rawPassword.toCharArray(), salt, PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH);

            // 组装存储格式：saltHex:hashHex
            return new String(Hex.encode(salt)) + ":" + new String(Hex.encode(hash));
        } catch (Exception e) {
            throw new RuntimeException("PBKDF2 加密计算失败", e);
        }
    }

    /**
     * 校验明文密码与 PBKDF2 格式密文是否匹配
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 存储的密文（salt:hash）
     * @return 是否匹配
     */
    public static boolean pbkdf2Matches(String rawPassword, String encodedPassword) {
        if (!StringUtils.hasText(rawPassword) || !StringUtils.hasText(encodedPassword)) {
            return false;
        }
        try {
            String[] parts = encodedPassword.split(":");
            if (parts.length != 2) {
                return false;
            }
            byte[] salt = Hex.decode(parts[0]);
            byte[] targetHash = Hex.decode(parts[1]);

            byte[] computedHash = pbkdf2Hash(rawPassword.toCharArray(), salt, PBKDF2_ITERATIONS, targetHash.length * 8);

            // 比较长度及内容（防时序攻击可直接用常量时间比较，此处简化）
            return slowEquals(targetHash, computedHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * PBKDF2 核心计算实现
     */
    private static byte[] pbkdf2Hash(char[] password, byte[] salt, int iterations, int keyLength)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        return factory.generateSecret(spec).getEncoded();
    }

    /**
     * 字节数组安全比较（防止时序攻击）
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}