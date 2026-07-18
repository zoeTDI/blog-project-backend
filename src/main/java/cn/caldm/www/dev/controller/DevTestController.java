package cn.caldm.www.dev.controller;

import cn.caldm.www.infra.annotation.ApiAccessLog;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一开发调试与冒烟测试接口
 * 路由前缀统一为 /dev-api，方便后续统一拦截或在生产环境禁用
 *
 * @author caldm
 */
@RestController
@RequestMapping("/dev-api/test")
public class DevTestController {
    /**
     * 测试场景一：模拟无参数的 GET 查询请求
     * 预期：日志记录操作模块为"测试模块"，评分为"基础获取"
     */
    @ApiAccessLog(operateModule = "测试模块", operateName = "基础获取", operateType = 1)
    @GetMapping("/hello")
    public Map<String, Object> sayHello() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "基础设施 AOP 拦截测试成功");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 测试场景二：模拟带复杂 RequestBody 参数的 POST 提交请求
     * 预期：日志表的 requestParams 字段能成功序列化存储该 JSON 对象
     */
    @ApiAccessLog(operateModule = "测试模块", operateName = "提交模拟文章", operateType = 2)
    @PostMapping("/article")
    public Map<String, Object> mockCreateArticle(@RequestBody MockArticleParams params) {
        // 模拟业务耗时（让日志里的 duration 字段有明显数字）
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "文章接收成功");
        result.put("data", params);
        return result;
    }

    /**
     * 测试场景三：模拟人为制造的运行时崩溃异常
     * 预期：
     * 1. 浏览器/ApiFox 收到全局异常处理器返回的标准 500 JSON 提示（包含 traceId）
     * 2. 数据库表 infra_api_error_log 自动插入一条记录，准确捕获到由除以零引发的 ArithmeticException 堆栈、文件名、代码行号
     */
    @GetMapping("/exception")
    public Map<String, Object> mockException() {
        // 人为制造一个经典的运行时算术异常（除以 0）
        int result = 10 / 0;

        Map<String, Object> successResult = new HashMap<>();
        successResult.put("code", 200);
        successResult.put("data", result);
        return successResult;
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
}
