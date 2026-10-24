package cn.caldm.www.user_context.usermanagement;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class SoftDeleteReqDTO {
    @NotNull
    private Long targetUserId;
}
