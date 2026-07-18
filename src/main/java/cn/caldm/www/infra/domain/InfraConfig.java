package cn.caldm.www.infra.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 参数配置表实体类
 *
 * @author caldm
 */
@Data
@TableName("infra_config")
public class InfraConfig {

    /**
     * 参数主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 参数分组
     */
    private String category;

    /**
     * 参数类型
     */
    private Integer type;

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数键名
     */
    private String configKey;

    /**
     * 参数键值
     */
    private String value;

    /**
     * 是否可见
     */
    private Boolean visible;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private String updater;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Boolean deleted;
}