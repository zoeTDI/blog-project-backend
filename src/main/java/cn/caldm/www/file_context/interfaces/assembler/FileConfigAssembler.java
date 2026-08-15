package cn.caldm.www.file_context.interfaces.assembler;

import cn.caldm.www.file_context.domain.model.FileConfig;
import cn.caldm.www.file_context.infrastructure.persistence.po.InfraFileConfigPO;

/**
 * 文件配置装配器
 *
 * @author caldm
 */
public class FileConfigAssembler {

    public static FileConfig toDomain(InfraFileConfigPO po) {
        FileConfig fileConfig = new FileConfig();
        fileConfig.setId(po.getId());
        fileConfig.setName(po.getName());
        fileConfig.setStorage(po.getStorage());
        fileConfig.setRemark(po.getRemark());
        fileConfig.setMaster(po.getMaster());
        fileConfig.setConfig(po.getConfig());
        fileConfig.setCreator(po.getCreator());
        fileConfig.setCreateTime(po.getCreateTime());
        fileConfig.setUpdater(po.getUpdater());
        fileConfig.setUpdateTime(po.getUpdateTime());
        fileConfig.setDeleted(po.getDeleted());
        return fileConfig;
    }
}