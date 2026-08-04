package cn.caldm.www.user_context.interfaces.dto;

import lombok.Data;

import java.util.List;

/**
 *
 * 创建账户DTO
 *
 * @author caldm
 */
@Data
public class CreateReqDTO {
    private Long creatorId;
    private List<String> roles;
    private String username;
    private String password;
}
