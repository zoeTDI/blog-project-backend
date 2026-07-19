package cn.caldm.www.infra.framework.file.core.client;

import cn.caldm.www.infra.framework.file.core.client.local.LocalFileClient;
import cn.caldm.www.infra.framework.file.core.client.local.LocalFileClientConfig;
import cn.caldm.www.infra.framework.file.core.client.s3.S3FileClient;
import cn.caldm.www.infra.framework.file.core.client.s3.S3FileClientConfig;
import cn.caldm.www.infra.framework.file.core.enums.FileStorageEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 文件客户端工厂实现类
 *
 * @author caldm
 */
@Slf4j
@Component
public class FileClientFactoryImpl implements FileClientFactory {

    /**
     * 文件客户端缓存池
     * Key: 配置编号 (configId)
     * Value: 具体的物理文件客户端实例
     */
    private final ConcurrentMap<Long, AbstractFileClient<?>> clients = new ConcurrentHashMap<>();

    @Override
    public FileClient getFileClient(Long configId) {
        AbstractFileClient<?> client = clients.get(configId);
        if (client == null) {
            log.error("[getFileClient][获取文件客户端失败，configId: {} 不存在，请检查配置是否初始化]", configId);
            throw new IllegalArgumentException("文件存储客户端不存在，配置ID: " + configId);
        }

        return client;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void createOrUpdateFileClient(Long configId, Integer storage, FileClientConfig config) {
        Assert.notNull(configId, "配置编号不能为空");
        Assert.notNull(storage, "存储器类型不能为空");
        Assert.notNull(config, "存储配置不能为空");

        FileStorageEnum storageEnum = FileStorageEnum.getByStorage(storage);
        if (storageEnum == null) {
            throw new IllegalArgumentException("未知的存储器类型: " + storage);
        }

        AbstractFileClient<?> masterClient = clients.get(configId);
        if (masterClient != null) {
            log.info("[createOrUpdateFileClient][文件客户端已存在，执行动态配置刷新，configId: {}]", configId);
            ((AbstractFileClient<FileClientConfig>) masterClient).refresh(config);
            return;
        }

        AbstractFileClient<?> newClient = createLocalOrS3Client(configId, storageEnum, config);
        if (newClient != null) {
            newClient.init();
            clients.put(configId, newClient);
        }

    }

    private AbstractFileClient<?> createLocalOrS3Client(Long configId, FileStorageEnum storageEnum, FileClientConfig config) {
        return switch (storageEnum) {
            case LOCAL -> new LocalFileClient(configId, (LocalFileClientConfig) config);
            case S3 -> new S3FileClient(configId, (S3FileClientConfig) config);
            default -> {
                // 对于暂未支持的类型（如原生的DB或SFTP），这里抛出异常或返回 null 以便扩展
                log.warn("[createLocalOrS3Client][暂不支持的存储器类型: {}, configId: {}]", storageEnum.getName(), configId);
                yield null;
            }
        };
    }
}
