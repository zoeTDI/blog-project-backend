package cn.caldm.www.file_context.interfaces.assembler;

import cn.caldm.www.file_context.domain.model.FileConfig;
import cn.caldm.www.file_context.infrastructure.persistence.po.InfraFileConfigPO;
import cn.caldm.www.shared_kernel.security.assembler.BaseAssembler;
import org.springframework.stereotype.Component;

/**
 * 文件配置装配器
 *
 * @author caldm
 */
@Component
public class FileConfigAssembler implements BaseAssembler<FileConfig, InfraFileConfigPO> {

    @Override
    public FileConfig toDomain(InfraFileConfigPO po) {
        if (po == null) {
            return null;
        }
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

    @Override
    public InfraFileConfigPO toPO(FileConfig domain) {
        if (domain == null) {
            return null;
        }
        InfraFileConfigPO po = new InfraFileConfigPO();
        po.setId(domain.getId());
        po.setName(domain.getName());
        po.setStorage(domain.getStorage());
        po.setRemark(domain.getRemark());
        po.setMaster(domain.getMaster());
        po.setConfig(domain.getConfig());
        po.setCreator(domain.getCreator());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdater(domain.getUpdater());
        po.setUpdateTime(domain.getUpdateTime());
        po.setDeleted(domain.getDeleted());
        return po;
    }

}