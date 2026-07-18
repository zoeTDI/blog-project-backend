package cn.caldm.www.infra.service.impl;

import cn.caldm.www.infra.domain.InfraConfig;
import cn.caldm.www.infra.mapper.InfraConfigMapper;
import cn.caldm.www.infra.service.InfraConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参数配置 Service 实现类
 * 兼任 CommandLineRunner，在系统启动时自动预热缓存项
 *
 * @author caldm
 */
@Slf4j
@Service
public class InfraConfigServiceImpl implements InfraConfigService, CommandLineRunner {

    @Autowired
    private InfraConfigMapper infraConfigMapper;

    /**
     * 本地高性能配置缓存容器（Key -> Value）
     */
    private final Map<String, String> configCache = new ConcurrentHashMap<>();

    /**
     * Spring Boot 启动时由框架自动调用的预热钩子
     */
    @Override
    public void run(String... args) {
        log.info("====== 开始初始化本地参数配置 [infra_config] 缓存 ======");
        initLocalCache();
    }

    /**
     * 核心预热与加载方法
     */
    @Override
    public void initLocalCache() {
        // 1. 从数据库捞出所有未被逻辑删除的有效配置
        LambdaQueryWrapper<InfraConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InfraConfig::getDeleted, false);
        List<InfraConfig> configList = infraConfigMapper.selectList(queryWrapper);

        // 2. 转换为本地缓存映射（双稳态：清理旧缓存，装填新缓存）
        configCache.clear();
        for (InfraConfig config : configList) {
            if (config.getConfigKey() != null && config.getValue() != null) {
                configCache.put(config.getConfigKey(), config.getValue());
            }
        }
        log.info("====== 成功加载本地参数配置缓存，当前总条数: {} ======", configCache.size());
    }

    /**
     * 极速读取配置：全走内存，无数据库查询
     */
    @Override
    public String getConfigValueByKey(String configKey) {
        if (configKey == null || configKey.trim().isEmpty()) {
            return "";
        }
        // 如果缓存中存在则直接返回；若不存在，作为兜底去查数据库，防止冷启动或缓存死角
        return configCache.computeIfAbsent(configKey, key -> {
            log.warn("参数配置缓存未命中，触发数据库降级查询: configKey={}", key);
            LambdaQueryWrapper<InfraConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(InfraConfig::getConfigKey, key)
                    .eq(InfraConfig::getDeleted, false)
                    .last("LIMIT 1");
            InfraConfig config = infraConfigMapper.selectOne(queryWrapper);
            return config != null ? config.getValue() : "";
        });
    }

    /**
     * 更新配置：先更新数据库，再刷新本地缓存，确保强一致
     */
    @Override
    public void updateConfig(InfraConfig config) {
        if (config.getId() == null) {
            throw new IllegalArgumentException("更新操作必须包含主键ID");
        }
        config.setUpdateTime(LocalDateTime.now());

        // 1. 更新数据库
        infraConfigMapper.updateById(config);

        // 2. 重新查出最新数据更新本地缓存
        InfraConfig newestConfig = infraConfigMapper.selectById(config.getId());
        if (newestConfig != null && !newestConfig.getDeleted()) {
            configCache.put(newestConfig.getConfigKey(), newestConfig.getValue());
            log.info("本地参数配置缓存已动态实时刷新: {} -> {}", newestConfig.getConfigKey(), newestConfig.getValue());
        }
    }

    /**
     * 定时任务兜底：每隔 5 分钟定时全量同步数据库与内存，防止由于DB直改引发的数据脱节
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void cronRefreshCache() {
        log.info("====== 定时任务启动：开始刷新系统参数配置本地缓存 ======");
        initLocalCache();
    }
}