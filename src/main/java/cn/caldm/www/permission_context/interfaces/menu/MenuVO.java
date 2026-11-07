package cn.caldm.www.permission_context.interfaces.menu;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data 
public class MenuVO {
    private Long id;
    private String name;
    private Integer type;
    private Integer sort;
    private String path;
    private String component;
    private String icon;
    private String titleKey;
    private List<MenuVO> children = new ArrayList<>();
}
