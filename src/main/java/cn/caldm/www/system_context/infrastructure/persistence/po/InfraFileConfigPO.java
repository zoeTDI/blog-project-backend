package cn.caldm.www.system_context.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件配置表实体
 *
 * @author caldm
 */
@Data
@TableName("infra_file_config")
public class InfraFileConfigPO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer storage;

    private String remark;

    private Boolean master;

    private String config;

    private String creator;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private String updater;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Boolean deleted;
}
