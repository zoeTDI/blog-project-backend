package cn.caldm.www.user_context.interfaces.rest;

import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import cn.caldm.www.user_context.application.service.UserApplicationService;
import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.interfaces.dto.CreateReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 *
 * @author caldm
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserApplicationService userService;

    @PostMapping("/create")
    private Result<SysUser> create(@RequestBody CreateReqDTO createReqDTO) {
        Long creatorId = createReqDTO.getCreatorId();
        String role = createReqDTO.getRole();
        String username = createReqDTO.getUsername();
        String password = createReqDTO.getPassword();
        if (
                creatorId == null
                || role == null
                || role.isEmpty()
                || username == null
                || username.isEmpty()
                || password == null
                || password.isEmpty()
        ) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        // todo 验证角色是否合法
        SysUser newUser = userService.create(creatorId, role, username, password);
        if (newUser == null) {
            return Result.error(ResultCodeEnum.INTERNAL_SERVER_ERROR);
        } else {
            return Result.success(newUser);
        }
    }
}
