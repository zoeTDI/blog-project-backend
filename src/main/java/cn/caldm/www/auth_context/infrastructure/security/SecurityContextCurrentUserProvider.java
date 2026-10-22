package cn.caldm.www.auth_context.infrastructure.security;

import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.shared_kernel.security.CurrentUser;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;
import cn.caldm.www.user_context.domain.model.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class SecurityContextCurrentUserProvider implements CurrentUserProvider {
    @Override
    public Optional<CurrentUser> getOptCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof AuthUser authUser) {
            return Optional.of(new CurrentUser(
                    authUser.getId(),
                    authUser.getUsername(),
                    authUser.getRoles() == null ? List.of() : authUser.getRoles().stream().map(UserRole::getCode).toList()
            ));
        }
        return Optional.empty();
    }
}
