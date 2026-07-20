package cn.caldm.www.dev.controller;

import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import cn.caldm.www.infra.framework.file.core.client.FileClient;
import cn.caldm.www.infra.framework.file.core.client.FileClientFactory;
import cn.caldm.www.infra.service.impl.InfraFileConfigServiceImpl;
import cn.caldm.www.infra.service.impl.InfraFileServiceImpl;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/dev-api/test/file")
public class DevFileTestController {
    @Resource
    private InfraFileServiceImpl infraFileService;

    @Resource
    private InfraFileConfigServiceImpl infraFileConfigService;

    @Resource
    private FileClientFactory fileClientFactory;

    @PostMapping("/upload")
    public Result<FileUploadVo> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error(ResultCodeEnum.BAD_REQUEST, "上传文件不能为空");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String path = "test/" + UUID.randomUUID().toString().replace("-", "") + extension;

            byte[] content = file.getBytes();

            String uploadUrl = infraFileService.uploadFile(originalFilename, path, content);
            log.info("[测试上传] 成功通过 Service 服务上传文件至路径: {}, 访问URL: {}", path, uploadUrl);

            return Result.success(new FileUploadVo(path, uploadUrl));
        } catch (Exception e) {
            log.error("[测试上传] 文件上传发生未知异常", e);
            return Result.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "上传异常: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public Result<Void> deleteFile(@RequestParam("id") Long id) {
        try {
            infraFileService.deleteFile(id);
            log.info("[测试删除] 已通过 Service 清理文件记录及物理资产，ID: {}", id);

            return Result.successMsg("物理删除请求执行成功");
        } catch (Exception e) {
            log.error("[测试删除] 物理文件删除失败", e);
            return Result.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "删除失败: " + e.getMessage());
        }
    }

    @PutMapping("/switch-master")
    public Result<Void> switchMaster(@RequestParam("configId") Long configId) {
        Map<String, Object> response = new HashMap<>();
        try {
            FileClient client = fileClientFactory.getFileClient(configId);
            if (client == null) {
                return Result.error(ResultCodeEnum.BAD_REQUEST, "切换失败：该配置编号没有可用的物理驱动实例");
            }

            infraFileConfigService.switchMasterConfig(configId);
            log.info("[动态切换] 系统主存储客户端已成功切换至有效配置编号: {}", configId);

            return Result.successMsg("测试环境主存储客户端已成功切换为 " + configId);
        } catch (Exception e) {
            return Result.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "切换异常: " + e.getMessage());
        }
    }

    @Data
    @AllArgsConstructor
    public static class FileUploadVo {
        private String path;
        private String url;
    }
}
