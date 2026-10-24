package cn.caldm.www.user_context.interfaces.dto;

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
