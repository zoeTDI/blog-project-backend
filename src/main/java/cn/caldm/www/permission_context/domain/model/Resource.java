package cn.caldm.www.permission_context.domain.model;

import cn.caldm.www.shared_kernel.utils.StringUtils;
import lombok.Data;

import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 *
 *
 *
 * @author caldm
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Resource implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 资源 ID
     */
    private Long id;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 资源类型
     */
    private ResourceTypeEnum type;

    /**
     * 父资源 ID
     */
    private Long parentId;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 前端路由地址
     */
    private String path;

    /**
     * 前端组件标识
     */
    private String component;

    /**
     * 前端图标标识
     */
    private String icon;

    /**
     * 前端国际化 Key
     */
    private String titleKey;

    /**
     * 是否启用
     */
    private boolean enabled;

    private static final Pattern PERMISSION_PATTERN = Pattern.compile("^[a-z][a-z0-9]*(:[a-z][a-z0-9]*)+$");

    private Resource() {
    }

    public static Resource create(
            String name,
            String permission,
            ResourceTypeEnum type,
            Long parentId,
            Integer sort,
            String path,
            String component,
            String icon,
            String titleKey) {
        Resource resource = new Resource();

        resource.name = name;
        resource.permission = permission;
        resource.type = type;
        resource.parentId = parentId;
        resource.sort = sort;
        resource.path = path;
        resource.component = component;
        resource.icon = icon;
        resource.titleKey = titleKey;
        resource.enabled = true;

        resource.validate();

        return resource;
    }

    public void update(
            String name,
            String permission,
            Long parentId,
            Integer sort,
            String path,
            String component,
            String icon,
            String titleKey) {
        this.name = name;
        this.permission = permission;
        this.parentId = parentId;
        this.sort = sort;
        this.path = path;
        this.component = component;
        this.icon = icon;
        this.titleKey = titleKey;

        validate();
    }

    public static Resource reconstitute(
            Long id,
            String name,
            String permission,
            ResourceTypeEnum type,
            Long parentId,
            Integer sort,
            String path,
            String component,
            String icon,
            String titleKey,
            boolean enabled
    ) {
        Resource resource = new Resource();

        resource.id = id;
        resource.name = name;
        resource.permission = permission;
        resource.type = type;
        resource.parentId = parentId;
        resource.sort = sort;
        resource.path = path;
        resource.component = component;
        resource.icon = icon;
        resource.titleKey = titleKey;
        resource.enabled = enabled;

        return resource;

    }

    public boolean isDirectory() {
        return ResourceTypeEnum.DIRECTORY.equals(type);
    }

    public boolean isMenu() {
        return ResourceTypeEnum.MENU.equals(type);
    }

    public boolean isButton() {
        return ResourceTypeEnum.BUTTON.equals(type);
    }

    public boolean canHaveChildren() {
        return !isButton();
    }

    public boolean isRoot() {
        return parentId == 0l;
    }

    public void disable() {
        this.enabled = false;
    }

    public void enable() {
        this.enabled = true;
    }

    public void validateParent(Resource parent) {
        if (isRoot()) {
            if (!isDirectory()) {
                throw new IllegalArgumentException("顶级资源必须是目录。");
            }
            return;
        }

        if (parent == null) {
            throw new IllegalArgumentException("父资源不存在。");
        }

        if (parent.isDirectory() && isMenu()) {
            return;
        }

        if (parent.isMenu() && isButton()) {
            return;
        }
        throw new IllegalArgumentException("非法的资源父子关系。");
    }

    private void validatePermission(String permission) {

        if (permission == null || permission.trim().isBlank()) {
            throw new IllegalArgumentException("权限标识不能为空");
        }

        if (!PERMISSION_PATTERN.matcher(permission).matches()) {
            throw new IllegalArgumentException(
                    "权限标识格式错误：" + permission);
        }
    }

    private void validate() {
        validateCommonField();
        validateByType();
    }

    private void validateCommonField() {
        if (name == null || name.trim().isBlank()) {
            throw new IllegalArgumentException("资源名称不能为空");
        }

        if (type == null) {
            throw new IllegalArgumentException("资源类型不能为空");
        }

        if (parentId == null) {
            throw new IllegalArgumentException("父资源 ID 不能为空");
        }

        if (sort == null) {
            throw new IllegalArgumentException("排序值不能为空");
        }
    }

    private void validateByType() {
        switch (type) {
            case DIRECTORY -> validateDirectory();
            case MENU -> validateMenu();
            case BUTTON -> validateButton();
        }
    }

    private void validateButton() {
        if (StringUtils.isBlank(permission)) {
            throw new IllegalArgumentException("按钮权限标识不能为空");
        }
        if (!StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("按钮不能配置路由地址");
        }
        if (!StringUtils.isBlank(component)) {
            throw new IllegalArgumentException("按钮不能配置组件");
        }
        if (!StringUtils.isBlank(icon)) {
            throw new IllegalArgumentException("按钮不能配置图标");
        }
    }

    private void validateMenu() {
        if (StringUtils.isBlank(permission)) {
            throw new IllegalArgumentException("菜单权限标识不能为空");
        }
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("菜单路由地址不能为空");
        }
        if (StringUtils.isBlank(component)) {
            throw new IllegalArgumentException("菜单组件标识不能为空");
        }
        if (StringUtils.isBlank(titleKey)) {
            throw new IllegalArgumentException("菜单国际化 Key 不能为空");
        }
    }

    private void validateDirectory() {
    }

}
