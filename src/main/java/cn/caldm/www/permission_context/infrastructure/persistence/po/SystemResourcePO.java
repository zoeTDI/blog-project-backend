package cn.caldm.www.permission_context.infrastructure.persistence.po;

import cn.caldm.www.permission_context.domain.model.ResourceDeletedEnum;
import cn.caldm.www.permission_context.domain.model.ResourceStatusEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.caldm.www.permission_context.domain.model.ResourceTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("system_menu")
public class SystemResourcePO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private String permission;
    private ResourceTypeEnum type;
    private Long parentId;
    private Integer sort;
    private String path;
    private String component;
    private String icon;
    private String titleKey;
    private ResourceStatusEnum status;
    private String creator;
    private LocalDateTime createTime;
    private String updater;
    private LocalDateTime updateTime;
    @TableLogic
    private ResourceDeletedEnum deleted;
}
