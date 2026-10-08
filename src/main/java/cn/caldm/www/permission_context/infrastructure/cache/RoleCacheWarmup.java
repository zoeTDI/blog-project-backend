package cn.caldm.www.permission_context.infrastructure.cache;

import cn.caldm.www.permission_context.application.service.RoleCacheService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 *
 * 预热
 *
 * @author caldm
 */
@Component
@AllArgsConstructor
public class RoleCacheWarmup implements ApplicationRunner {
    private final RoleCacheService roleCacheService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        roleCacheService.refresh();
    }
}
