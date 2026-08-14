package cn.caldm.www.infrastructure.file.core.client.local;

import cn.caldm.www.infrastructure.file.core.client.FileClientConfig;
import lombok.Data;

/**
 * 本地磁盘存储客户端配置
 * 对应存储器类型：10
 *
 * @author caldm
 */
@Data
public class LocalFileClientConfig implements FileClientConfig {
    /**
     * 基础存储物理路径，例如：/Users/caldm/tmp/file 或者 D:\\upload
     */
    private String basePath;

    /**
     * 静态资源访问域名，例如：http://127.0.0.1:8080
     */
    private String domain;
}
