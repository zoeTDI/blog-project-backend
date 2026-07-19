package cn.caldm.www.infra.service.impl;

import cn.caldm.www.infra.domain.InfraFile;
import cn.caldm.www.infra.framework.file.core.client.FileClient;
import cn.caldm.www.infra.framework.file.core.client.FileClientFactory;
import cn.caldm.www.infra.mapper.InfraFileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文件日志持久化与物理上传控制服务
 *
 * @author caldm
 */
@Slf4j
@Service
public class InfraFileServiceImpl {

    @Autowired
    private InfraFileMapper fileMapper;

    @Autowired
    private InfraFileConfigServiceImpl fileConfigService;

    @Autowired
    private FileClientFactory fileClientFactory;

    /**
     * 核心业务方法：一键上传文件（使用默认主存储介质）
     *
     * @param name    原始文件名
     * @param path    相对路径路径
     * @param content 文件字节流
     * @return 最终可访问路径 URL
     */
    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(String name, String path, byte[] content) throws Exception {
        Long configId = fileConfigService.getMasterConfigId();
        if (configId == null) {
            throw new IllegalStateException("当前系统未配置任何主存储器，请先检查 infra_file_config 表");        }

        FileClient fileClient = fileClientFactory.getFileClient(configId);

        String url = fileClient.upload(content, path);

        InfraFile fileRecord = new InfraFile();
        fileRecord.setConfigId(configId);
        fileRecord.setName(name);
        fileRecord.setPath(path);
        fileRecord.setUrl(url);
        fileRecord.setSize(content.length);

        if (name != null && name.contains(".")) {
            fileRecord.setType(name.substring(name.lastIndexOf(".") + 1));
        }
        fileRecord.setCreator("system_user");

        fileMapper.insert(fileRecord);
        return url;
    }

    /**
     * 物理与逻辑同步删除文件
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long id) throws Exception {
        InfraFile fileRecord = fileMapper.selectById(id);
        if (fileRecord == null) {
            return;
        }

        FileClient fileClient = fileClientFactory.getFileClient(fileRecord.getConfigId());
        fileClient.delete(fileRecord.getPath());

        fileMapper.deleteById(id);
    }
}
