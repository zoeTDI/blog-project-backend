package cn.caldm.www.auth_context.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import cn.caldm.www.shared_kernel.security.PermissionType;
import cn.caldm.www.shared_kernel.security.RequirePermission;

/**
 * 全项目权限守卫扫描规则（防未来破坏约定的回归钉）：
 * 1. @RequirePermission 的枚举值必须为 BUTTON 类型
 *    —— 菜单/目录码（router:*）不得用于端点守卫；
 * 2. 权限码守卫必须统一走 @RequirePermission，
 *    禁止在 @PreAuthorize 里写 hasPermission/hasAuthority 或引用 PermissionCodes 常量。
 */
public class PermissionGuardScanTest {

    /** 检出 @PreAuthorize 里的权限码式写法 */
    private static final Pattern SPEL_PERMISSION_CHECK =
            Pattern.compile("hasPermission|hasAnyPermission|hasAllPermission|hasAuthority|PermissionCodes");

    @Test
    void everyPermissionGuard_shouldFollowConventions() throws Exception {
        List<String> violations = new ArrayList<>();

        for (Class<?> controller : scanControllers()) {
            for (Method method : controller.getDeclaredMethods()) {
                RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
                if (requirePermission != null
                        && requirePermission.value().getType() != PermissionType.BUTTON) {
                    violations.add(describe(method, "@RequirePermission 引用了非按钮码："
                            + requirePermission.value().name() + "（类型 "
                            + requirePermission.value().getType().getDesc() + "）"));
                }

                PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
                if (preAuthorize != null && SPEL_PERMISSION_CHECK.matcher(preAuthorize.value()).find()) {
                    violations.add(describe(method, "权限码守卫必须使用 @RequirePermission，禁止在 @PreAuthorize 中写："
                            + preAuthorize.value()));
                }
            }
        }

        assertThat(violations)
                .as("权限守卫违反约定（共 %d 处）", violations.size())
                .isEmpty();
    }

    private List<Class<?>> scanControllers() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

        List<Class<?>> controllers = new ArrayList<>();
        for (BeanDefinition definition : scanner.findCandidateComponents("cn.caldm.www")) {
            controllers.add(Class.forName(definition.getBeanClassName()));
        }
        assertThat(controllers).as("扫描不到任何 Controller，检查扫描基包").isNotEmpty();
        return controllers;
    }

    private String describe(Method method, String problem) {
        return method.getDeclaringClass().getSimpleName() + "#" + method.getName() + "： " + problem;
    }
}
