package cn.caldm.www.user_context.usermanagement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty
    private List<Long> roleIds;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
    private String nickname;
}
