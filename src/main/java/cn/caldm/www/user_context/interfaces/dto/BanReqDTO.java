package cn.caldm.www.user_context.interfaces.dto;

import lombok.Data;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class BanReqDTO {
    private Long updaterId;
    private Long targetUserId;
}
