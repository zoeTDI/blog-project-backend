package cn.caldm.www.system_context.application.service;

import cn.caldm.www.system_context.infrastructure.persistence.po.InfraConfigPO;

/**
 * 参数配置 Service 接口
 *
 * @author caldm
 */
public interface InfraConfigService {

    /**
     * 初始化/刷新本地参数配置缓存
     */
    void initLocalCache();

    /**
     * 根据参数键名获取参数键值（核心高频方法：走本地缓存）
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    String getConfigValueByKey(String configKey);

    /**
     * 更新参数配置并刷新本地缓存
     *
     * @param config 待更新的参数实体
     */
    void updateConfig(InfraConfigPO config);
}