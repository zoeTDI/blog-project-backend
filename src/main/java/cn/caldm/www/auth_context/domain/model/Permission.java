package cn.caldm.www.auth_context.domain.model;

import cn.caldm.www.permission_context.domain.model.ResourceTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 *
 *
 * @author caldm
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Permission implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long id;
    private String name;
    private String permission;
    private ResourceTypeEnum type;
    private long parentId;
    private boolean enabled;
}
