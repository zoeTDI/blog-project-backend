package cn.caldm.www.permission_context.interfaces.dto.resource;

import cn.caldm.www.permission_context.application.command.UpdateResourceCommand;
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
public class UpdateResourceRequest {
    @NotBlank
    private String name;
    private String permission;
    @NotNull
    private Long parentId;
    @NotNull
    private Integer sort;
    private String path;
    private String component;
    private String icon;
    private String titleKey;

    public UpdateResourceCommand toCommand(Long id) {
        UpdateResourceCommand command = new UpdateResourceCommand();
        command.setId(id);
        command.setName(this.name);
        command.setPermission(this.permission);
        command.setParentId(this.parentId);
        command.setSort(this.sort);
        command.setPath(this.path);
        command.setComponent(this.component);
        command.setIcon(this.icon);
        command.setTitleKey(this.titleKey);
        return command;
    }
}
