package cn.caldm.www.user_context.usermanagement;

import cn.caldm.www.shared_kernel.domain.ResourceNotFoundException;
import cn.caldm.www.shared_kernel.domain.ResultCodeEnum;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;
import cn.caldm.www.user_context.domain.model.*;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import cn.caldm.www.user_context.ports.UserRoleFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 *
 * 用户管理用例：创建用户、封禁用户、软删除用户
 *
 * @author caldm
 */
@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final UserRoleFacadeService userRoleFacadeService;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(rollbackFor = Exception.class)
    public SysUser create(String username, String password, String email, String nickname, List<Long> roleIds) {
        Assert.notNull(roleIds, "The roles of new user must not be null.");

        List<UserRole> roles = userRoleFacadeService.getRolesByIds(roleIds);

        // 禁止创建管理员角色的用户
        if (roles.stream().anyMatch(role -> "ADMIN".equals(role.getCode()))) {
            throw new IllegalArgumentException("不能创建 ADMIN 角色的用户");
        }
        Long creatorId = currentUserProvider.getUserId();
        SysUser creator = userRepository.findById(creatorId);

        if (creator == null) {
            throw new IllegalStateException("当前登录用户不存在");
        }

        if (!userRoleFacadeService.hasRole(creatorId, "ADMIN")) {
            throw new IllegalArgumentException("没有权限创建用户");
        }

        String encode = passwordEncoder.encode(password);
        List<UserRole> filtered = roles.stream().filter(Objects::nonNull).toList();
        String finalNickname = (nickname == null || nickname.isBlank()) ? username : nickname;
        SysUser inserted = userRepository.insert(creator.getUsername(), username, encode, email, finalNickname);
        if (inserted == null) {
            throw new IllegalStateException("创建用户失败");
        }
        if (!filtered.isEmpty()) {
            List<String> roleCodes = filtered.stream().map(UserRole::getCode).toList();
            userRoleFacadeService.assignRolesToUser(inserted.getId(), roleCodes);
        }
        inserted.setRoles(userRoleFacadeService.getUserRolesById(inserted.getId()));
        return inserted;
    }

    public void ban(Long targetUserId) {
        Long updaterId = currentUserProvider.getUserId();
        SysUser updater = userRepository.findById(updaterId);

        if (updater == null) {
            throw new IllegalStateException("当前登录用户不存在");
        }

        if (!userRoleFacadeService.hasRole(updaterId, "ADMIN")) {
            throw new IllegalArgumentException("没有权限封禁用户");
        }

        SysUser targetUser = userRepository.findById(targetUserId);

        if (targetUser == null) {
            throw new ResourceNotFoundException(ResultCodeEnum.NOT_FOUND, "目标用户不存在");
        }

        if (userRoleFacadeService.hasRole(targetUserId, "ADMIN")) {
            throw new IllegalArgumentException("管理员不可封禁");
        }

        if (targetUser.isDeleted()) {
            throw new IllegalArgumentException("目标用户已删除");
        }

        userRepository.updateStatus(targetUserId, SysUserStatusEnum.DISABLED, updater.getUsername());
    }

    public void softDelete(Long targetUserId) {
        Long updaterId = currentUserProvider.getUserId();
        SysUser updater = userRepository.findById(updaterId);
        if (updater == null) {
            throw new IllegalStateException("当前登录用户不存在");
        }
        SysUser targetUser = userRepository.findById(targetUserId);
        if (targetUser == null) {
            throw new ResourceNotFoundException(ResultCodeEnum.NOT_FOUND, "目标用户不存在");
        }
        // 已删除的用户不能再次删除
        if (targetUser.isDeleted()) {
            throw new IllegalArgumentException("目标用户已删除");
        }

        boolean updaterIsAdmin = userRoleFacadeService.hasRole(updaterId, "ADMIN");
        boolean targetIsAdmin = userRoleFacadeService.hasRole(targetUserId, "ADMIN");
        boolean isSelf = updaterId.equals(targetUserId);

        // 管理员不能删除管理员用户
        if (updaterIsAdmin && targetIsAdmin) {
            throw new IllegalArgumentException("管理员不可删除");
        }
        // 非管理员用户无权删除他人
        if (!updaterIsAdmin && !isSelf) {
            throw new IllegalArgumentException("没有权限删除用户");
        }

        userRepository.markDeleted(targetUserId, updater.getUsername());
    }
}
