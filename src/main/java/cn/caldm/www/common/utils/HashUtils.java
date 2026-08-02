package cn.caldm.www.common.utils;

import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 常规哈希/摘要工具类
 * 提供 MD5、SHA-256、SHA-512 等常用加密算法及加盐哈希支持
 *
 * @author caldm
 */
public class HashUtils {
    public static final String MD5_ALGORITHM = "MD5";
    public static final String SHA256_ALGORITHM = "SHA-256";
    public static final String SHA512_ALGORITHM = "SHA-512";

    /**
     * 生成 MD5 哈希值（默认 32 位小写）
     *
     * @param input 明文字符串
     * @return 32位小写十六进制哈希值
     */
    public static String md5(String input) {
        return hash(input, MD5_ALGORITHM);
    }

    /**
     * 生成 SHA-256 哈希值
     *
     * @param input 明文字符串
     * @return 64位十六进制哈希值
     */
    public static String sha256(String input) {
        return hash(input, SHA256_ALGORITHM);
    }

    /**
     * 生成 SHA-512 哈希值
     *
     * @param input 明文字符串
     * @return 128位十六进制哈希值
     */
    public static String sha512(String input) {
        return hash(input, SHA512_ALGORITHM);
    }

    /**
     * 生成随机盐值（Base64 编码格式）
     *
     * @return 随机盐值字符串
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    /**
     * 加盐 SHA-256 哈希运算（常用于密码安全存储校验）
     *
     * @param input 明文字符串
     * @param salt  盐值
     * @return 加盐后的哈希值
     */
    public static String sha256WithSalt(String input, String salt) {
        if (!StringUtils.hasText(input)) {
            return null;
        }
        String saltedInput = (salt != null ? salt : "") + input;
        return sha256(saltedInput);
    }

    /**
     * 校验明文和加盐哈希是否匹配
     *
     * @param input      明文字符串
     * @param salt       盐值
     * @param targetHash 目标哈希值
     * @return 是否匹配
     */
    public static boolean verifySha256WithSalt(String input, String salt, String targetHash) {
        if (!StringUtils.hasText(input) || !StringUtils.hasText(targetHash)) {
            return false;
        }
        String computedHash = sha256WithSalt(input, salt);
        return computedHash.equalsIgnoreCase(targetHash);
    }

    /**
     * 通用哈希计算核心方法
     */
    private static String hash(String input, String algorithm) {
        if (!StringUtils.hasText(input)) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("系统不支持该哈希算法: " + algorithm, e);
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] hashBytes) {
        StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
