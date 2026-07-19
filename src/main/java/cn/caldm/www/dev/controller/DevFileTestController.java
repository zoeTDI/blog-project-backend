package cn.caldm.www.dev.controller;

import cn.caldm.www.infra.framework.file.core.client.FileClient;
import cn.caldm.www.infra.framework.file.core.client.FileClientFactory;
import cn.caldm.www.infra.service.impl.InfraFileConfigServiceImpl;
import cn.caldm.www.infra.service.impl.InfraFileServiceImpl;
import jakarta.annotation.Resource;
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
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (file.isEmpty()) {
                response.put("code", 400);
                response.put("msg", "上传文件不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String path = "test/" + UUID.randomUUID().toString().replace("-", "") + extension;

            byte[] content = file.getBytes();

            String uploadUrl = infraFileService.uploadFile(originalFilename, path, content);
            log.info("[测试上传] 成功通过 Service 服务上传文件至路径: {}, 访问URL: {}", path, uploadUrl);

            response.put("code", 200);
            response.put("msg", "上传成功");
            response.put("data", Map.of(
                    "path", path,
                    "url", uploadUrl
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[测试上传] 文件上传发生未知异常", e);
            response.put("code", 500);
            response.put("msg", "上传异常: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteFile(@RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            infraFileService.deleteFile(id);
            log.info("[测试删除] 已通过 Service 清理文件记录及物理资产，ID: {}", id);

            response.put("code", 200);
            response.put("msg", "物理删除请求执行成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[测试删除] 物理文件删除失败", e);
            response.put("code", 500);
            response.put("msg", "删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/switch-master")
    public ResponseEntity<Map<String, Object>> switchMaster(@RequestParam("configId") Long configId) {
        Map<String, Object> response = new HashMap<>();
        try {
            FileClient client = fileClientFactory.getFileClient(configId);
            if (client == null) {
                response.put("code", 400);
                response.put("msg", "切换失败：该配置编号没有可用的物理驱动实例");
                return ResponseEntity.badRequest().body(response);
            }

            // 本地变量覆盖实现无侵入式动态测试切换
            infraFileConfigService.switchMasterConfig(configId);
            log.info("[动态切换] 系统主存储客户端已成功切换至有效配置编号: {}", configId);

            response.put("code", 200);
            response.put("msg", "测试环境主存储客户端已成功切换为 " + configId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "切换异常: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
