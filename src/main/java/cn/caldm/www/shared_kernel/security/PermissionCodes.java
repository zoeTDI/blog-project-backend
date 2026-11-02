package cn.caldm.www.shared_kernel.security;

import lombok.Getter;

@Getter
public enum PermissionCodes {
    // ===== 目录/菜单（动态路由与菜单渲染，码仅作稳定标识） =====
    ROUTER_CONTENT("内容管理", "router:content", PermissionType.CATEGORY),
    ROUTER_CONTENT_POST("文章管理", "router:content:post", PermissionType.MENU),
    ROUTER_CONTENT_CATEGORY("分类/标签管理", "router:content:category", PermissionType.MENU),
    ROUTER_CONTENT_REVIEW("审核队列", "router:content:review", PermissionType.MENU), // 端点待实现，先建菜单
    ROUTER_ADMIN("系统管理", "router:admin", PermissionType.CATEGORY),
    ROUTER_ADMIN_USER_MANAGE("用户管理", "router:admin:userManage", PermissionType.MENU),
    ROUTER_ADMIN_RESOURCE_MANAGE("资源管理", "router:admin:resourceManage", PermissionType.MENU),

    // ===== 操作权限（@ss.hasPermission 端点守卫 + 前端按钮显隐） =====
    POST_CREATE("创建文章", "post:create", PermissionType.BUTTON),
    POST_UPDATE("更新文章", "post:update", PermissionType.BUTTON),
    POST_DELETE("删除文章", "post:delete", PermissionType.BUTTON),
    POST_SUBMIT("提交审核", "post:submit", PermissionType.BUTTON),
    POST_PUBLISH("发布文章", "post:publish", PermissionType.BUTTON),
    CATEGORY_MANAGE("分类管理", "category:manage", PermissionType.BUTTON),
    TAG_MANAGE("标签管理", "tag:manage", PermissionType.BUTTON),
    USER_MANAGE("用户管理操作", "user:manage", PermissionType.BUTTON),
    RESOURCE_MANAGE("资源管理操作", "resource:manage", PermissionType.BUTTON);

    private final String name;
    private final String permission;
    private final PermissionType type;

    PermissionCodes(String name, String permission, PermissionType type) {
        this.permission = permission;
        this.name = name;
        this.type = type;
    }

    static PermissionCodes fromPermission(String permission, PermissionType type) {
        for (PermissionCodes code : values()) {
            if (code.getPermission().equals(permission) && code.getType().equals(type)) {
                return code;
            }
        }
        return null;
    }
}
