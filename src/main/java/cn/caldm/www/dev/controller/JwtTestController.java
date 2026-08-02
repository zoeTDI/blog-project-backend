package cn.caldm.www.dev.controller;

import cn.caldm.www.auth.jwt.utils.JwtUtils;
import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import cn.caldm.www.common.utils.LogUtils;
import cn.caldm.www.login.domain.SysUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 带token验证的控制器
 *
 * @author caldm
 */
@Slf4j
@RestController
public class JwtTestController {
    static Map<Integer, SysUser> userMap = new HashMap<>();

    static {
        SysUser user1 = new SysUser();
        user1.setId(1L);
        user1.setUsername("张三");
        user1.setPassword("123456");
        userMap.put(1, user1);
        SysUser user2 = new SysUser();
        user2.setId(2L);
        user2.setUsername("李四");
        user2.setPassword("abcdefg");
        userMap.put(2, user2);
    }

    @RequestMapping("/login")
    public Result<String> login(@RequestBody SysUser user) {
        for (SysUser dbUser: userMap.values()) {
            if (dbUser.getUsername().equals(user.getUsername())
                    && dbUser.getPassword().equals(user.getPassword())) {
                LogUtils.info("登录成功！生成token！");
                user.setId(dbUser.getId());
                String token = JwtUtils.createToken(user);
                return Result.success(token);
            }
        }
        return Result.error(ResultCodeEnum.BAD_REQUEST);
    }

    /**
     * 查询 用户信息，登录后携带JWT才能访问
     */
    @RequestMapping("/secure/getUserInfo")
    public Result<String> getUserInfo(HttpServletRequest request) {
        Long id = (Long) request.getAttribute("id");
        String username = request.getAttribute("username").toString();
        String password= request.getAttribute("password").toString();
        return Result.success("当前用户信息id=" + id + ",username=" + username+ ",password=" + password);
    }
}
