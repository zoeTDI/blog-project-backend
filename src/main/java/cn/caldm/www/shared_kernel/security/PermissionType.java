package cn.caldm.www.shared_kernel.security;

import lombok.Getter;

@Getter 
public enum PermissionType {
    CATEGORY(1, "目录"),
    MENU(2, "菜单"),
    BUTTON(3, "按钮");

    private final Integer code;
    private final String desc;

    PermissionType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    static PermissionType formCode(Integer code) {
        for (PermissionType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
