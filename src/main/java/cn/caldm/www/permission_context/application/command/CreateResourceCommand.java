package cn.caldm.www.permission_context.application.command;

import cn.caldm.www.permission_context.domain.model.ResourceTypeEnum;
import lombok.Data;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class CreateResourceCommand {
    private String name;
    private String permission;
    private ResourceTypeEnum type;
    private Long parentId;
    private Integer sort;
    private String path;
    private String component;
    private String icon;
    private String titleKey;

}
