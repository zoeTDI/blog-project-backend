package cn.caldm.www.file_context.interfaces.assembler;

import cn.caldm.www.file_context.domain.model.File;
import cn.caldm.www.file_context.infrastructure.persistence.po.InfraFilePO;

/**
 * 文件装配器
 *
 * @author caldm
 */
public class FileAssembler {

    public static File toDomain(InfraFilePO po) {
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
}