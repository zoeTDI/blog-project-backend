package cn.caldm.www.infra.annotation;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 文件客户端配置基类
 * 借助 Jackson 的 JsonTypeInfo 注解，实现基于 JSON 中 @class 属性的动态多态反序列化
 *
 * @author caldm
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property =  "@classes")
public interface FileClientConfig {
    // 作为一个标识接口，用来规范各类存储厂商的配置类
}
