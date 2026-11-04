package cn.caldm.www.auth_context.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import cn.caldm.www.auth_context.infrastructure.security.SecurityExpressionService;
import cn.caldm.www.shared_kernel.security.PermissionCodes;
import cn.caldm.www.shared_kernel.security.RequirePermission;

/**
 * 权限切面的放行/拒绝回归钉。
 * 拒绝时必须抛 Spring Security 的 AccessDeniedException（走全局异常处理器 403 分支），
 * 且绝不执行业务方法。
 */
@ExtendWith(MockitoExtension.class)
public class PermissionCheckAspectTest {

    @Mock
    private SecurityExpressionService securityExpressionService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @InjectMocks
    private PermissionCheckAspect permissionCheckAspect;

    @Test
    void check_shouldProceed_whenPermissionGranted() throws Throwable {
        RequirePermission annotation = annotationStub(PermissionCodes.POST_CREATE);
        when(securityExpressionService.hasPermission("post:create")).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("result");

        Object result = permissionCheckAspect.check(joinPoint, annotation);

        assertThat(result).isEqualTo("result");
        verify(joinPoint).proceed();
    }

    @Test
    void check_shouldThrowAccessDenied_whenPermissionDenied() throws Throwable {
        RequirePermission annotation = annotationStub(PermissionCodes.POST_PUBLISH);
        when(securityExpressionService.hasPermission("post:publish")).thenReturn(false);

        assertThatThrownBy(() -> permissionCheckAspect.check(joinPoint, annotation))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("post:publish");

        // 拒绝时必须短路，绝不执行业务方法
        verify(joinPoint, never()).proceed();
    }

    private RequirePermission annotationStub(PermissionCodes code) {
        RequirePermission annotation = mock(RequirePermission.class);
        when(annotation.value()).thenReturn(code);
        return annotation;
    }
}
