package cn.caldm.www.system_context.interfaces.assembler;

import cn.caldm.www.system_context.domain.model.Config;
import cn.caldm.www.system_context.infrastructure.persistence.po.InfraConfigPO;

/**
 * 参数配置装配器
 *
 * @author caldm
 */
public class ConfigAssembler {

    public static Config toDomain(InfraConfigPO po) {
        Config config = new Config();
        config.setId(po.getId());
        config.setCategory(po.getCategory());
        config.setType(po.getType());
        config.setName(po.getName());
        config.setConfigKey(po.getConfigKey());
        config.setValue(po.getValue());
        config.setVisible(po.getVisible());
        config.setRemark(po.getRemark());
        config.setCreator(po.getCreator());
        config.setCreateTime(po.getCreateTime());
        config.setUpdater(po.getUpdater());
        config.setUpdateTime(po.getUpdateTime());
        config.setDeleted(po.getDeleted());
        return config;
    }
}