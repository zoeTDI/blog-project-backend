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
    private String name;

    public boolean equals(UserRole role) {
        if (role == null || role.getName() == null || role.getName().isBlank()) {
            return false;
        }
        return name.equals(role.getName());
    }
}
