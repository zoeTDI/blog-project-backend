package cn.caldm.www.user_context.domain.modal;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 *
 *
 * @author caldm
 */
@AllArgsConstructor
@Getter
public class UserRole {
    private String code;

    public boolean equals(UserRole role) {
        if (role == null || role.getCode() == null || role.getCode().isBlank()) {
            return false;
        }
        return code.equals(role.getCode());
    }
}
