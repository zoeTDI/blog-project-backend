package cn.caldm.www.system_context.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 *
 *
 * @author caldm
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Config {

    /**
     * 参数主键
     */
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
