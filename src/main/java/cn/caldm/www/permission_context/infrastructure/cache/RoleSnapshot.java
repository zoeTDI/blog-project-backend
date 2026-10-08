package cn.caldm.www.permission_context.infrastructure.cache;

import cn.caldm.www.permission_context.domain.model.Role;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record RoleSnapshot(Map<Long, Role> byId, Map<String, Role> byCode) {

    public static RoleSnapshot empty() {
        return new RoleSnapshot(Map.of(), Map.of());
    }

    public static RoleSnapshot from(List<Role> roles) {
        Map<Long, Role> byId = roles.stream()
                .collect(Collectors.toUnmodifiableMap(Role::getId, Function.identity()));
        Map<String, Role> byCode = roles.stream()
                .collect(Collectors.toUnmodifiableMap(Role::getCode, Function.identity()));
        return new RoleSnapshot(byId, byCode);
    }

}
