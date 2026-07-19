package cn.caldm.www.infra.service.impl;

import cn.caldm.www.infra.framework.file.core.client.FileClientConfig;
import cn.caldm.www.infra.domain.InfraFileConfig;
import cn.caldm.www.infra.framework.file.core.client.FileClientFactory;
import cn.caldm.www.infra.mapper.InfraFileConfigMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class InfraFileConfigServiceImpl {
    @Autowired
    private InfraFileConfigMapper fileConfigMapper;

    @Autowired
    private FileClientFactory fileClientFactory;

    @Autowired
    private ObjectMapper objectMapper;

    private Long masterConfigId;

    @EventListener(ApplicationReadyEvent.class)
    public void initFileClient() {
        log.info("====== 开始预热加载分布式文件客户端缓存 ======");
        List<InfraFileConfig> configs = fileConfigMapper.selectList(
                new LambdaQueryWrapper<InfraFileConfig>().eq(InfraFileConfig::getDeleted, false)
        );

        for (InfraFileConfig config : configs) {
            try {
                FileClientConfig clientConfig = objectMapper.readValue(config.getConfig(), FileClientConfig.class);
                fileClientFactory.createOrUpdateFileClient(config.getId(), config.getStorage(), clientConfig);

                if (config.getMaster()) {
                    this.masterConfigId = config.getId();
                    log.info("[initFileClients] 成功指派默认主存储客户端，配置编号: {}", config.getId());
                }
            } catch (Exception e) {
                log.error("[initFileClients] 存储配置编号: {} 序列化转换物理驱动失败", config.getId(), e);
            }
        }
        log.info("====== 成功加载分布式文件客户端缓存，总计数量: {} ======", configs.size());
    }

    /**
     * 获取当前系统默认启用的主存储配置编号
     */
    public Long getMasterConfigId() {
        if (this.masterConfigId == null) {
            InfraFileConfig master = fileConfigMapper.selectOne(
                    new LambdaQueryWrapper<InfraFileConfig>()
                            .eq(InfraFileConfig::getMaster, true)
                            .last("LIMIT 1")
            );
            if (master != null) {
                this.masterConfigId = master.getId();
            }
        }
        return this.masterConfigId;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateFileConfig(InfraFileConfig updateParam) throws Exception {
        fileConfigMapper.updateById(updateParam);

        InfraFileConfig newest = fileConfigMapper.selectById(updateParam.getId());
        FileClientConfig clientConfig = objectMapper.readValue(newest.getConfig(), FileClientConfig.class);
        fileClientFactory.createOrUpdateFileClient(newest.getId(), newest.getStorage(), clientConfig);

        if (newest.getMaster()) {
            this.masterConfigId = newest.getId();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void switchMasterConfig(Long configId) {
        fileConfigMapper.update(null, new LambdaUpdateWrapper<InfraFileConfig>()
                .eq(InfraFileConfig::getDeleted, false)
                .setSql("master = b'0'")
        );
        boolean success = fileConfigMapper.update(null, new LambdaUpdateWrapper<InfraFileConfig>()
                .eq(InfraFileConfig::getId, configId)
                .eq(InfraFileConfig::getDeleted, false)
                .setSql("master = b'1'")
        ) > 0;
        if (!success) {
            throw new IllegalArgumentException("切换主存储失败：找不到对应的有效配置记录(ID: " + configId + ")");
        }
        this.masterConfigId = configId;
        log.info("[动态切换] 业务配置层已将主存储器变量内存与数据库同步变更为: {}", configId);
    }
}
