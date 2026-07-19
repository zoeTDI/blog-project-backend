package cn.caldm.www.infra.framework.file.core.client;

import cn.caldm.www.infra.annotation.FileClientConfig;

/**
 * 文件客户端工厂接口
 *
 * @author caldm
 */
public interface FileClientFactory {
    /**
     * 获取指定配置编号的文件客户端
     *
     * @param configId 配置编号
     * @return 文件客户端实例
     */
    FileClient getFileClient(Long configId);

    /**
     * 创建或更新文件客户端实例（支持动态刷新缓存）
     *
     * @param configId 配置编号
     * @param storage  存储器类型（对应 FileStorageEnum）
     * @param config   具体的配置对象
     */
    void createOrUpdateFileClient(Long configId, Integer storage, FileClientConfig config);
}
