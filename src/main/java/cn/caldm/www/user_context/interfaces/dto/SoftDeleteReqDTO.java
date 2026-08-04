package cn.caldm.www.user_context.interfaces.dto;

import lombok.Data;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class SoftDeleteReqDTO {
    private Long updaterId;
    private Long targetUserId;
}
