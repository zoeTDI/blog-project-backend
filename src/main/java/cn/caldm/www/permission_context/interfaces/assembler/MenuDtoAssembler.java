package cn.caldm.www.permission_context.interfaces.assembler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.caldm.www.permission_context.domain.model.Resource;
import cn.caldm.www.permission_context.domain.model.ResourceNode;
import cn.caldm.www.permission_context.interfaces.menu.MenuVO;

@Component
public class MenuDtoAssembler {

    public List<MenuVO> toVOList(List<ResourceNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return List.of();
        }
        ArrayList<MenuVO> VOList = new ArrayList<>();
        for (ResourceNode node : nodes) {
            if (node == null || node.getNode() == null) {
                continue;
            }
            MenuVO vo = toVO(node);
            VOList.add(vo);
        }
        return VOList;
    }

    public MenuVO toVO(ResourceNode node) {
        if (node == null || node.getNode() == null) {
            throw new IllegalArgumentException("资源节点不能为空");
        }

        Resource res = node.getNode();
        MenuVO vo = new MenuVO();
        vo.setId(res.getId());
        vo.setName(res.getName());
        vo.setType(res.getType().getCode());
        vo.setSort(res.getSort());
        vo.setPath(res.getPath());
        vo.setComponent(res.getComponent());
        vo.setIcon(res.getIcon());
        vo.setTitleKey(res.getTitleKey());
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            vo.setChildren(toVOList(node.getChildren()));
        } else {
            vo.setChildren(List.of());
        }
        return vo;
    }

}
