package cn.caldm.www.user_context.interfaces.dto;

import lombok.Data;

/**
 *
 * 创建账户DTO
 *
 * @author caldm
 */
@Data
public class CreateReqDTO {
    private Long creatorId;
    private String role;
    private String username;
    private String password;
}
