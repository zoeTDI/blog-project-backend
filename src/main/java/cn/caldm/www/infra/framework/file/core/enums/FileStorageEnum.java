package cn.caldm.www.infra.framework.file.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件存储器类型枚举
 * 对应 infra_file_config 表中的 storage 字段
 *
 * @author caldm
 */
@Getter
@AllArgsConstructor
public enum FileStorageEnum {
    DB(1, "数据库"),
    LOCAL(10, "本地存储"),
    SFTP(12, "SFTP 存储"),
    S3(20, "S3 存储协议(阿里云、腾讯云、MinIO、七牛云等共用)");

    /**
     * 存储器编号
     */
    private final Integer storage;

    /**
     * 存储器名称
     */
    private final String name;

    public static FileStorageEnum getByStorage(Integer storage) {
        for (FileStorageEnum value : values()) {
            if (value.getStorage().equals(storage)) {
                return value;
            }
        }
        return null;
    }
}
