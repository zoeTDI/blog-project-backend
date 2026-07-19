package cn.caldm.www.infra.framework.file.core.client.s3;

import cn.caldm.www.infra.annotation.FileClientConfig;
import lombok.Data;

/**
 * 通用 S3 协议对象存储客户端配置
 * 对应存储器类型：20
 *
 * @author caldm
 */
@Data
public class S3FileClientConfig implements FileClientConfig {
    /**
     * 节点地址
     */
    private String endpoint;

    /**
     * 自定义 CDN 静态访问域名
     */
    private String domain;

    /**
     * 存储桶名称
     */
    private String bucket;

    /**
     * 访问密钥 Access Key
     */
    private String accessKey;

    /**
     * 访问密钥 Access Secret
     */
    private String accessSecret;

    /**
     * 是否使用 Path-Style 路径格式（如 MinIO 通常需要设为 true）
     */
    private Boolean enablePathStyleAccess = false;

    /**
     * 是否公开访问
     */
    private Boolean enablePublicAccess = true;

    /**
     * 区域（部分云厂商可选）
     */
    private String region;
}
