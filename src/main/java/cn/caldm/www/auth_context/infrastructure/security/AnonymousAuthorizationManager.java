package cn.caldm.www.auth_context.infrastructure.security;

import cn.caldm.www.infrastructure.annotation.Anonymous;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.function.Supplier;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class AnonymousAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final RequestMappingHandlerMapping handlerMapping;

    public AnonymousAuthorizationManager(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Nullable
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext object) {
        HttpServletRequest request = object.getRequest();

        try {
            HandlerExecutionChain handlerChain = handlerMapping.getHandler(request);
            if (handlerChain != null && handlerChain.getHandler() instanceof HandlerMethod handlerMethod) {
                Anonymous methodAnn = handlerMethod.getMethodAnnotation(Anonymous.class);
                Anonymous classAnn = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Anonymous.class);

                if (methodAnn != null || classAnn != null) {
                    return new AuthorizationDecision(true);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Authentication authentication = authenticationSupplier.get();
        boolean isAuthenticated = authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
        return new AuthorizationDecision(isAuthenticated);
    }
}
