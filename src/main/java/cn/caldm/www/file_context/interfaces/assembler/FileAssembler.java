package cn.caldm.www.file_context.interfaces.assembler;

import cn.caldm.www.file_context.domain.model.File;
import cn.caldm.www.file_context.infrastructure.persistence.po.InfraFilePO;
import cn.caldm.www.shared_kernel.security.assembler.BaseAssembler;
import org.springframework.stereotype.Component;

/**
 * 文件装配器
 *
 * @author caldm
 */
@Component
public class FileAssembler implements BaseAssembler<File, InfraFilePO> {

    @Override
    public File toDomain(InfraFilePO po) {
        if (po == null) {
            return null;
        }
        File file = new File();
        file.setId(po.getId());
        file.setConfigId(po.getConfigId());
        file.setName(po.getName());
        file.setPath(po.getPath());
        file.setUrl(po.getUrl());
        file.setType(po.getType());
        file.setSize(po.getSize());
        file.setCreator(po.getCreator());
        file.setCreateTime(po.getCreateTime());
        file.setUpdater(po.getUpdater());
        file.setUpdateTime(po.getUpdateTime());
        file.setDeleted(po.getDeleted());
        return file;
    }

    @Override
    public InfraFilePO toPO(File domain) {
        if (domain == null) {
            return null;
        }
        InfraFilePO po = new InfraFilePO();
        po.setId(domain.getId());
        po.setConfigId(domain.getConfigId());
        po.setName(domain.getName());
        po.setPath(domain.getPath());
        po.setUrl(domain.getUrl());
        po.setType(domain.getType());
        po.setSize(domain.getSize());
        po.setCreator(domain.getCreator());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdater(domain.getUpdater());
        po.setUpdateTime(domain.getUpdateTime());
        po.setDeleted(domain.getDeleted());
        return po;
    }
}