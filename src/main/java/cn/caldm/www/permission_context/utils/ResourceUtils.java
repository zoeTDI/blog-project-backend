package cn.caldm.www.permission_context.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.caldm.www.permission_context.domain.model.ResourceNode;
import cn.caldm.www.permission_context.domain.model.Resource;

public class ResourceUtils {
    public static List<ResourceNode> buildTree(List<Resource> resources) {
        if (resources == null || resources.isEmpty()) {
            return List.of();
        }
        HashMap<Long, ResourceNode> map = new HashMap<>();
        resources.stream()
                .forEach(res -> map.put(res.getId(), new ResourceNode(res)));
        List<ResourceNode> rootNodes = new ArrayList<>();
        resources.stream()
                .forEach(res -> {
                    ResourceNode self = map.get(res.getId());
                    ResourceNode parent = (res.getParentId() == null) ? null : map.get(res.getParentId());
                    if (parent != null) {
                        parent.getChildren().add(self);
                    } else {
                        rootNodes.add(self);
                    }
                });
        map.values().stream().forEach(node -> node.freezeChildren());
        return Collections.unmodifiableList(rootNodes);
    }
}
