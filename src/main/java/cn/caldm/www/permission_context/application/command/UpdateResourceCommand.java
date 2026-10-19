package cn.caldm.www.permission_context.application.command;

import lombok.Data;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class UpdateResourceCommand {
    private long id;
    private String name;
    private String permission;
    private long parentId;
    private int sort;
    private String path;
    private String component;
    private String icon;
    private String titleKey;
}
