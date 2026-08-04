package cn.caldm.www.user_context.domain.modal;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ADMIN("ADMIN", "管理员"),
    AUDITOR("AUDITOR", "审核"),
    AUTHOR("AUTHOR", "作者");

    private final String code;
    private final String desc;

    RoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 将 String code 转换为 Role（安全转换，不抛异常，失败返回 null）
     *
     * @param code 角色编码或枚举名称
     * @return 对应的 Role 枚举，若无法匹配则返回 null
     */
    public static RoleEnum fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (RoleEnum role : RoleEnum.values()) {
            if (role.name().equalsIgnoreCase(code) || role.code.equalsIgnoreCase(code)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 校验传入的 String code 是否为合法的角色
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }

    /**
     * Role 与 Role 的比较
     *
     * @param targetRole 另一个角色
     * @return 是否相同
     */
    public boolean equalsRole(RoleEnum targetRole) {
        if (targetRole == null) {
            return false;
        }
        return this == targetRole;
    }

    /**
     *  String code 与当前 Role 的比较
     *
     * @param targetCode 目标角色字符串
     * @return 是否相同
     */
    public boolean equalsRole(String targetCode) {
        RoleEnum targetRole = fromCode(targetCode);
        return this.equalsRole(targetRole);
    }
}
