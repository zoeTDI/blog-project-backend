package cn.caldm.www.permission_context.interfaces.assembler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

import cn.caldm.www.permission_context.domain.model.Resource;
import cn.caldm.www.permission_context.domain.model.ResourceNode;
import cn.caldm.www.permission_context.domain.model.ResourceTypeEnum;
import cn.caldm.www.permission_context.interfaces.menu.MenuVO;

/**
 * 菜单 VO 装配回归钉：字段映射、type 输出数字码、children 递归、空输入防御。
 */
public class MenuDtoAssemblerTest {

    private final MenuDtoAssembler assembler = new MenuDtoAssembler();

    @Test
    void toVOList_shouldReturnEmptyForNullOrEmptyInput() {
        assertThat(assembler.toVOList(null)).isEmpty();
        assertThat(assembler.toVOList(List.of())).isEmpty();
    }

    @Test
    void toVO_shouldMapAllFieldsAndTypeCode() {
        Resource resource = Resource.reconstitute(
                2L, "文章管理", "router:content:post", ResourceTypeEnum.MENU, 1L, 3,
                "/posts/list", "post/postManage/PostManage", "document-text", "postManage", true);

        MenuVO vo = assembler.toVO(new ResourceNode(resource));

        assertThat(vo.getId()).isEqualTo(2L);
        assertThat(vo.getName()).isEqualTo("文章管理");
        // type 必须输出数字码（枚举 @JsonValue 会序列化成中文描述，前端无法 switch）
        assertThat(vo.getType()).isEqualTo(2);
        assertThat(vo.getSort()).isEqualTo(3);
        assertThat(vo.getPath()).isEqualTo("/posts/list");
        assertThat(vo.getComponent()).isEqualTo("post/postManage/PostManage");
        assertThat(vo.getIcon()).isEqualTo("document-text");
        assertThat(vo.getTitleKey()).isEqualTo("postManage");
        assertThat(vo.getChildren()).isEmpty();
    }

    @Test
    void toVOList_shouldMapChildrenRecursively() {
        Resource root = Resource.reconstitute(
                1L, "内容管理", "router:content", ResourceTypeEnum.DIRECTORY, 0L, 1,
                "", "", "", "", true);
        Resource child = Resource.reconstitute(
                2L, "文章管理", "router:content:post", ResourceTypeEnum.MENU, 1L, 1,
                "/posts/list", "post/postManage/PostManage", "", "postManage", true);
        ResourceNode rootNode = new ResourceNode(root);
        rootNode.getChildren().add(new ResourceNode(child));

        List<MenuVO> vos = assembler.toVOList(List.of(rootNode));

        assertThat(vos).hasSize(1);
        assertThat(vos.get(0).getChildren()).hasSize(1);
        assertThat(vos.get(0).getChildren().get(0).getTitleKey()).isEqualTo("postManage");
    }

    @Test
    void toVO_shouldRejectNullNode() {
        assertThatThrownBy(() -> assembler.toVO(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
