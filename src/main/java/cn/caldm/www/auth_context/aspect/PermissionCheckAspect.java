package cn.caldm.www.auth_context.aspect;

import org.springframework.security.access.AccessDeniedException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import cn.caldm.www.auth_context.infrastructure.security.SecurityExpressionService;
import cn.caldm.www.shared_kernel.security.RequirePermission;
import lombok.RequiredArgsConstructor;

@Aspect 
@Component 
@RequiredArgsConstructor 
public class PermissionCheckAspect {
    private final SecurityExpressionService securityExpressionService;

    @Around ("@annotation(requirePermission)")
    public Object check(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        if (!securityExpressionService.hasPermission(requirePermission.value().getPermission())) {
            throw new AccessDeniedException("缺少权限：" + requirePermission.value().getPermission());
        }
        return joinPoint.proceed();
    }
}
