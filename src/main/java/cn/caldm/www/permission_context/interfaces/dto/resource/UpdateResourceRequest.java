package cn.caldm.www.permission_context.interfaces.dto.resource;

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
}
