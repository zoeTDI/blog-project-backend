package cn.caldm.www.dev.controller;

import cn.caldm.www.common.domain.Result;
import cn.caldm.www.infrastructure.annotation.ApiAccessLog;
import cn.caldm.www.system_context.infrastructure.persistence.po.InfraConfigPO;
import cn.caldm.www.system_context.application.service.InfraConfigService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 统一开发调试与冒烟测试接口
 * 路由前缀统一为 /dev-api，方便后续统一拦截或在生产环境禁用
 *
 * @author caldm
 */
@RestController
@RequestMapping("/dev-api/test")
public class DevTestController {

    @Autowired
    private InfraConfigService infraConfigService;

    /**
     * 测试场景一：模拟无参数的 GET 查询请求
     * 预期：日志记录操作模块为"测试模块"，评分为"基础获取"
     */
    @ApiAccessLog(operateModule = "测试模块", operateName = "基础获取", operateType = 1)
    @GetMapping("/hello")
    public Result<Void> sayHello() {
        return Result.successMsg("基础设施 AOP 拦截测试成功");
    }

    /**
     * 测试场景二：模拟带复杂 RequestBody 参数的 POST 提交请求
     * 预期：日志表的 requestParams 字段能成功序列化存储该 JSON 对象
     */
    @ApiAccessLog(operateModule = "测试模块", operateName = "提交模拟文章", operateType = 2)
    @PostMapping("/article")
    public Result<Void> mockCreateArticle(@RequestBody MockArticleParams params) {
        // 模拟业务耗时（让日志里的 duration 字段有明显数字）
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return Result.successMsg("文章接收成功");
    }

    /**
     * 测试场景三：模拟人为制造的运行时崩溃异常
     * 预期：
     * 1. 浏览器/ApiFox 收到全局异常处理器返回的标准 500 JSON 提示（包含 traceId）
     * 2. 数据库表 infra_api_error_log 自动插入一条记录，准确捕获到由除以零引发的 ArithmeticException 堆栈、文件名、代码行号
     */
    @GetMapping("/exception")
    public Result<Void> mockException() {
        // 人为制造一个经典的运行时算术异常（除以 0）
        int result = 10 / 0;

        return Result.success();
    }

    /**
     * 测试场景四：获取系统参数配置（高频内存读取）
     * 路由：GET /dev-api/test/config/get
     * 预期：
     * 1. 首次或频繁访问该接口时，控制台不会频繁打印 SQL 语句，因为全部走内存 Map。
     * 2. 传入不存在的 key 时，控制台应触发“参数配置缓存未命中，触发数据库降级查询”的警告。
     */
    @GetMapping("/config/get")
    public Result<ConfigTestVo> getConfigValue(@RequestParam("key") String key) {
        String value = infraConfigService.getConfigValueByKey(key);

        ConfigTestVo vo = new ConfigTestVo(key, value);
        return Result.success(vo);
    }

    /**
     * 测试场景五：动态修改系统参数配置（缓存实时刷写）
     * 路由：PUT /dev-api/test/config/update
     * 预期：
     * 1. 数据库对应的配置值被成功修改。
     * 2. 本地缓存同步刷新，紧接着调用场景四获取该 key 时，无延迟直接返回最新的数据。
     */
    @PutMapping("/config/update")
    public Result<Void> updateConfigValue(@RequestBody InfraConfigPO config) {
        // 为了方便冒烟测试，要求入参必须包含 id
        infraConfigService.updateConfig(config);

        return Result.successMsg("参数配置更新成功，本地缓存已刷新");
    }

    /**
     * 模拟测试专用的 DTO 参数类
     */
    @Data
    public static class MockArticleParams {
        private String title;
        private String content;
        private String[] tags;
    }

    @Data
    @AllArgsConstructor
    public static class ConfigTestVo {
        private String configKey;
        private String configValue;
    }
}