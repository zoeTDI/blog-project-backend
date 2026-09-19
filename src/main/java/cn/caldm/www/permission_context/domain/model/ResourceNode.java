package cn.caldm.www.permission_context.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data 
public class ResourceNode {
    private SystemResource node;
    private List<ResourceNode> children = new ArrayList<>();

    public ResourceNode(SystemResource node) {
        this.node = node;
        this.children = new ArrayList<>();
    }

    public void freezeChildren() {
        if (children instanceof ArrayList) {
            children = Collections.unmodifiableList(children);
        }

        for (ResourceNode child : children) {
            child.freezeChildren();
        }
    }
}
