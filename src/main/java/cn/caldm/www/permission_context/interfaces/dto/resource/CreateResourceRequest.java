package cn.caldm.www.permission_context.interfaces.dto.resource;

import cn.caldm.www.permission_context.application.command.CreateResourceCommand;
import cn.caldm.www.permission_context.domain.model.ResourceTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class CreateResourceRequest {

    @NotBlank
    private String name;
    private String permission;
    @NotNull
    private ResourceTypeEnum type;
    @NotNull
    private Long parentId;
    @NotNull
    private Integer sort;
    private String path;
    private String component;
    private String icon;
    private String titleKey;

    public CreateResourceCommand toCommand() {
        CreateResourceCommand command = new CreateResourceCommand();
        command.setName(this.name);
        command.setPermission(this.permission);
        command.setType(this.type);
        command.setParentId(this.parentId);
        command.setSort(this.sort);
        command.setPath(this.path);
        command.setComponent(this.component);
        command.setIcon(this.icon);
        command.setTitleKey(this.titleKey);
        return command;
    }
}
