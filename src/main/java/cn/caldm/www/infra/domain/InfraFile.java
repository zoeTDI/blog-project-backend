package cn.caldm.www.infra.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件记录表实体
 *
 * @author caldm
 */
@Data
@TableName("infra_file")
public class InfraFile {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long configId;

    private String name;

    private String path;

    private String url;

    private String type;

    private Integer size;

    private String creator;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private String updater;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Boolean deleted;
}
