package cn.caldm.www.auth_context.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import cn.caldm.www.shared_kernel.security.PermissionCodes;
import cn.caldm.www.shared_kernel.security.PermissionType;

/**
 * 全项目权限守卫扫描规则（防未来破坏约定的回归钉）：
 * 1. @ss.hasPermission 引用的每个常量必须存在于 PermissionCodes 且类型为 BUTTON
 *    —— 菜单/目录码（router:*）不得用于端点守卫；
 * 2. 禁止字符串字面量写法（@ss.hasPermission('post:create')），
 *    必须使用 T(PermissionCodes).XXX 编译期常量。
 */
public class PermissionGuardScanTest {

    /** 提取表达式里所有 T(...PermissionCodes).CONSTANT 的常量名 */
    private static final Pattern CONSTANT_REFERENCE = Pattern.compile("PermissionCodes\\)\\.([A-Z][A-Z0-9_]*)");

    /** 检出字符串字面量写法 */
    private static final Pattern STRING_LITERAL = Pattern.compile("@ss\\.has(?:Any|All)?Permission\\('");

    @Test
    void everyPermissionGuard_shouldReferenceButtonTypeConstant() throws Exception {
        List<String> violations = new ArrayList<>();

        for (Class<?> controller : scanControllers()) {
            for (Method method : controller.getDeclaredMethods()) {
                PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);
                if (annotation == null) {
                    continue;
                }
                String expression = annotation.value();

                Matcher literal = STRING_LITERAL.matcher(expression);
                if (literal.find()) {
                    violations.add(describe(method, "禁止字符串字面量权限码，请使用 T(PermissionCodes).XXX 常量："
                            + expression));
                }

                Matcher constants = CONSTANT_REFERENCE.matcher(expression);
                while (constants.find()) {
                    String constantName = constants.group(1);
                    PermissionCodes code = PermissionCodes.valueOf(constantName);
                    if (code.getType() != PermissionType.BUTTON) {
                        violations.add(describe(method,
                                "菜单/目录码不得用于端点守卫（当前 " + constantName + " 类型为 "
                                        + code.getType().getDesc() + "）"));
                    }
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
