package cn.caldm.www.user_context.domain.modal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 *
 *
 *
 * @author caldm
 */
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class UserRole {
    private Long id;
    private String code;

    public UserRole(String code) {
        this.code = code;
    }
}
