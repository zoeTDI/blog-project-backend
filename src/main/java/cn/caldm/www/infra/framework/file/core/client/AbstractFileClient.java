package cn.caldm.www.infra.framework.file.core.client;

import lombok.extern.slf4j.Slf4j;

/**
 * 文件客户端抽象基类，实现通用逻辑
 *
 * @param <Config> 具体的配置子类
 * @author caldm
 */
@Slf4j
public abstract class AbstractFileClient<Config extends FileClientConfig> implements FileClient {
    /**
     * 客户端编号
     */
    private final Long id;

    /**
     * 客户端配置
     */
    protected Config config;

    public AbstractFileClient(Long id, Config config) {
        this.id = id;
        this.config = config;
    }

    /**
     * 初始化客户端（子类实现具体的第三方 SDK 客户端构建）
     */
    public final void init() {
        try {
            doInit();
            log.info("[init][文件客户端构建成功, 编号: {}, 存储器子类: {}]", id, getClass().getSimpleName());
        } catch (Exception e) {
            log.error("[init][文件客户端构建失败, 编号: {}]", id, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 子类专有的初始化逻辑
     */
    protected abstract void doInit() throws Exception;

    /**
     * 当配置发生动态变更时，刷新并重新初始化客户端配置
     *
     * @param config 新的配置项
     */
    public final void refresh(Config config) {
        this.config = config;
        this.init();
    }

    @Override
    public Long getId() {
        return id;
    }
}
