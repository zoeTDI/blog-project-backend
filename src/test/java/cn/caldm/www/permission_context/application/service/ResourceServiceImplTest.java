package cn.caldm.www.permission_context.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cn.caldm.www.permission_context.application.command.UpdateResourceCommand;
import cn.caldm.www.permission_context.domain.model.Resource;
import cn.caldm.www.permission_context.domain.model.ResourceTypeEnum;
import cn.caldm.www.permission_context.domain.repository.ResourceRepository;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;
    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    void updateResource_shouldRejectDirectoryUnderMenu() {
        // 资源当前是根目录（parentId=0），试图挂到菜单（parentId=5）下：
        // 若 validateParent 在 update 之前基于旧 parentId 校验，该非法层级会被放行
        Resource directory = Resource.reconstitute(
                1L, "系统管理", "system", ResourceTypeEnum.DIRECTORY,
                0L, 1, "/system", null, null, "menu.system", true);
        Resource menuParent = Resource.reconstitute(
                5L, "博客菜单", "blog:view", ResourceTypeEnum.MENU,
                0L, 1, "/blog", "BlogView", "icon", "menu.blog", true);

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(directory));
        when(resourceRepository.findById(5L)).thenReturn(Optional.of(menuParent));

        UpdateResourceCommand command = new UpdateResourceCommand();
        command.setId(1L);
        command.setName("系统管理");
        command.setPermission("system");
        command.setParentId(5L);
        command.setSort(1);
        command.setPath("/system");
        command.setComponent(null);
        command.setIcon(null);
        command.setTitleKey("menu.system");

        assertThatThrownBy(() -> resourceService.updateResource(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("非法的资源父子关系");
    }
}
